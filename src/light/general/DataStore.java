package light.general;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.crypto.Data;

import light.fixtures.Attribute;
import light.fixtures.FeatureGroup;
import light.fixtures.Fixture;
import light.fixtures.PatchManager;
import light.fixtures.profile.ProfileChannel;
import light.persistency.PersistencyCapable;
import light.persistency.PersistencyWriter;

public class DataStore implements PersistencyCapable {
    
    public final static double NONE = -12345d;
    
    protected Map<ConsoleAddress, Map<Attribute, Double>> store;
    private Submitter<Set<Fixture>> updateAction; //Action run when store is updated
    
    public DataStore() {
        store = new HashMap<ConsoleAddress, Map<Attribute, Double>>();
    }
    
    public void setUpdateAction(Submitter<Set<Fixture>> action) {this.updateAction = action;}
    public boolean hasUpdateAction() {return updateAction!=null;}
    public Submitter<Set<Fixture>> getUpdateAction() {return updateAction;}
    
    public void set(Fixture fixture, Attribute attribute, Double value, boolean overwritePriority) {
        set(fixture.getAddress(), attribute, value, overwritePriority);
    }
    
    /**
    * 
    * @param fixture
    * @param attribute
    * @param value
    * @param overwritePriority - if set then the given value will overwrite the stores value. Otherwsie the data stores value will persist 
    */
    public void set(ConsoleAddress address, Attribute attribute, Double value, boolean overwritePriority) {
        if (address==null||attribute==null) return;
        Fixture fixture = PatchManager.getInstance().getFixture(address);
        if (fixture==null||!fixture.hasAttribute(attribute)) return;
        
        value = validate(fixture, attribute, value);
        
        //If not overwrite then return if store already contains value 
        if (!overwritePriority&&contains(fixture, attribute)) return;
        
        if (store.containsKey(address)&&store.get(address)!=null) { //Add when fixture already present in store
            store.get(address).put(attribute, value);
        }
        else { //Add when fixture not present in store
            Map<Attribute, Double> attributes = new HashMap<Attribute, Double>();
            attributes.put(attribute, value);
            store.put(address, attributes);
        }
        
        if (hasUpdateAction()) updateAction.submit(Collections.singleton(fixture));
    }
    
    public void set(Fixture fixture, Map<Attribute, Double> attributes, boolean overwritePriority) {
        set(fixture.getAddress(), attributes, overwritePriority);
    }
    
    public void set(ConsoleAddress address, Map<Attribute, Double> attributes, boolean overwritePriority) {
        if (address==null) throw new Error("Cannot add null fixture address to datastore");
        if (attributes==null) throw new Error("Cannot add null values/attributes to datastore");
        Fixture fixture = PatchManager.getInstance().getFixture(address);
        if (fixture==null) return;
        
        //Check target fixture has attributes
        Set<Attribute> toRemove = new HashSet<>();
        for (Map.Entry<Attribute, Double> v : attributes.entrySet()) {
            if (!fixture.hasAttribute(v.getKey())) toRemove.add(v.getKey());
            
            //Check value not present if not overwrite
            if (!overwritePriority&&contains(fixture, v.getKey())) toRemove.add(v.getKey());
        }
        attributes.keySet().removeAll(toRemove);
        
        //Validate values TODO: make more elegant
        for (Map.Entry<Attribute, Double> v : attributes.entrySet()) {
            attributes.put(v.getKey(), validate(fixture, v.getKey(), v.getValue()));
        }
        
        //Assign values to this fixture's mapping
        store.put(address, attributes);
        
        if (hasUpdateAction()) updateAction.submit(Collections.singleton(fixture));
    }
    
    private double validate(Fixture fixture, Attribute attribute, double value) {
        ProfileChannel channel = fixture.getProfile().getChannelWithAttribute(attribute);
        if (channel==null) return Double.MAX_VALUE;
        
        if (!channel.valueInRange(value)) {
            if (value>channel.getMaxValue()) return channel.getMaxValue();
            else return channel.getMinValue();
        }
        return value;
    }
    
    public void remove(Fixture fixture) {
        store.remove(fixture.getAddress());
    }
    
    public void remove(ConsoleAddress address) {
        store.remove(address);
    }
    
    public void remove(Fixture fixture, Attribute attribute) {
        remove(fixture.getAddress(), attribute);
    }
    
    public void remove(ConsoleAddress address, Attribute attribute) {
        if (store.containsKey(address)&&store.get(address)!=null) {
            store.get(address).remove(attribute);
            
            //If no values left then remove fixture
            if (store.get(address).isEmpty()) remove(address);
        }
        
        if (hasUpdateAction()) {
            Fixture f = PatchManager.getInstance().getFixture(address);
            if (f!=null) updateAction.submit(Collections.singleton(f));
        }
    }
    
    public void clear() {
        Set<Fixture> fixtures = getFixtureSet();
        store.clear();
        if (hasUpdateAction()) updateAction.submit(fixtures);
    }
    
    public Set<ConsoleAddress> getAddressSet() {
        Set<ConsoleAddress> copy = new HashSet<>();
        copy.addAll(store.keySet());
        return copy;
    }
    
    public Set<Fixture> getFixtureSet() {
        Set<Fixture> copy = new HashSet<>();
        for (ConsoleAddress address : store.keySet()) {
            Fixture f = PatchManager.getInstance().getFixture(address);
            if (f!=null) copy.add(f);
        }
        return copy;
    }
    
    public Double get(Fixture fixture, Attribute attribute) {return get(fixture.getAddress(), attribute);}
    
    public Double get(ConsoleAddress address, Attribute attribute) {
        if (address==null||attribute==null||!store.containsKey(address)||!store.get(address).containsKey(attribute)) return NONE;
        return store.get(address).get(attribute);
    }
    
    public Map<Attribute, Double> get(ConsoleAddress address) {return store.get(address);}
    
    public Map<Attribute, Double> get(Fixture fixture) {return store.get(fixture.getAddress());}
    
    public Map<Attribute, Double> getFixtureValuesMatchingGroup(Fixture fixture, FeatureGroup group) {
        Map<Attribute, Double> result = new HashMap<>();
        Map<Attribute, Double> values = store.get(fixture.getAddress());
        if (values==null) return result;
        
        for (Map.Entry<Attribute, Double> value : values.entrySet()) {
            if (value.getKey().getFeature().getFeatureGroup()==group) result.put(value.getKey(), value.getValue());
        }
        return result;
    }
    
    public boolean contains(Fixture fixture) {return fixture!=null&&store.containsKey(fixture.getAddress());}
    
    public boolean contains(Fixture fixture, Attribute attribute) {return contains(fixture.getAddress(), attribute);}
    
    public boolean contains(ConsoleAddress address, Attribute attribute) {
        return address!=null&&attribute!=null
        &&store.containsKey(address)&&store.get(address)!=null
        &&store.get(address).containsKey(attribute);
    }
    
    /**
    * Combine the given datastore into this data store.
    * 
    * 
    * @param o - the data store to combine into this data store.
    * @param combineMethod - if set then the given data store's values will overwrite the current stores values. Otherwsie the current data stores values will persist 
    * @return this Datastore to allow chaining
    */
    public DataStore combine(DataStore o, boolean overwritePriority) {
        for (ConsoleAddress address : o.getAddressSet()) {
            for (Map.Entry<Attribute, Double> values : o.get(address).entrySet()) {
                set(address, values.getKey(), values.getValue(), overwritePriority);
            }
        }
        return this;
    }
    
    /**
    * Returns clone of data store that has been filtered to only contain specified attributes
    * @param filter - set of specified attributes
    * @return
    */
    public DataStore getFilteredClone(Set<Attribute> filter) {
        DataStore filtered = new DataStore();
        
        for (ConsoleAddress address : getAddressSet()) {
            for (Map.Entry<Attribute, Double> v: get(address).entrySet()) {
                if (filter.contains(v.getKey())) filtered.set(address, v.getKey(), v.getValue(), true);
            }
        }
        return filtered;
    }
    
    public DataStore getZeroedClone() {
        DataStore clone = this.clone();
        
        for (Map.Entry<ConsoleAddress, Map<Attribute, Double>> e : clone.store.entrySet()) {
            if (e.getKey()==null||e.getValue()==null) continue;
            for (Attribute attr : e.getValue().keySet()) clone.store.get(e.getKey()).put(attr, 0d);
        }
        
        return clone;
    }
    
    @Override
    public DataStore clone() {
        Map<ConsoleAddress, Map<Attribute, Double>> newStore = new HashMap<>();
        
        for (Map.Entry<ConsoleAddress, Map<Attribute, Double>> e : store.entrySet()) {
            if (e.getKey()==null||e.getValue()==null) continue;
            
            Map<Attribute, Double> attrs = new HashMap<>();
            for (Map.Entry<Attribute, Double> e1 : e.getValue().entrySet()) {
                double i = e1.getValue();
                attrs.put(e1.getKey(), i);
            }
            newStore.put(e.getKey(), attrs);
        }
        
        DataStore clone = new DataStore();
        clone.store = newStore;
        return clone;
    }
    
    public String toStoreString() {
        return store.toString();
    }
    
    @Override
    public byte[] getBytes() {
        PersistencyWriter pW = new PersistencyWriter();
        
        for (ConsoleAddress a : getAddressSet()) {
            pW.openSegment();
            pW.writeObject(a);
            for (Map.Entry<Attribute, Double> e : get(a).entrySet()) {
                pW.writeInt(e.getKey().ordinal());
                pW.writeDouble(e.getValue());
            }
            pW.closeSegmenet();
        }
        
        pW.wrapInSegment();
        return pW.getBytes();
    }
    
    public static DataStore generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
