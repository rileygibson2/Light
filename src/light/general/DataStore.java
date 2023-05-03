package light.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import light.fixtures.Attribute;
import light.fixtures.FeatureGroup;
import light.fixtures.Fixture;
import light.fixtures.profile.ProfileChannel;
import light.persistency.PersistencyCapable;
import light.persistency.PersistencyWriter;

public class DataStore implements PersistencyCapable {
    
    public final static double NONE = -12345d;

    protected Map<Fixture, Map<Attribute, Double>> store;
    private Runnable updateAction; //Action run when store is updated
    
    public DataStore() {
        store = new HashMap<Fixture, Map<Attribute, Double>>();
    }

    public void setUpdateAction(Runnable action) {this.updateAction = action;}
    public boolean hasUpdateAction() {return updateAction!=null;}
    public Runnable getUpdateAction() {return updateAction;}
    
    /**
     * 
     * @param fixture
     * @param attribute
     * @param value
     * @param overwritePriority - if set then the given value will overwrite the stores value. Otherwsie the data stores value will persist 
     */
    public void set(Fixture fixture, Attribute attribute, Double value, boolean overwritePriority) {
        if (fixture==null||attribute==null||!fixture.hasAttribute(attribute)) return;
        value = validate(fixture, attribute, value);

        //If not overwrite then return if store already contains value 
        if (!overwritePriority&&contains(fixture, attribute)) return;

        if (store.containsKey(fixture)&&store.get(fixture)!=null) { //Add when fixture already present in store
            store.get(fixture).put(attribute, value);
        }
        else { //Add when fixture not present in store
            Map<Attribute, Double> attributes = new HashMap<Attribute, Double>();
            attributes.put(attribute, value);
            store.put(fixture, attributes);
        }

        if (hasUpdateAction()) updateAction.run();
    }

    public void set(Fixture fixture, Map<Attribute, Double> attributes, boolean overwritePriority) {
        if (fixture==null) throw new Error("Cannot add null fixture to datastore");
        if (attributes==null) throw new Error("Cannot add null values/attributes to datastore");

        //Check target fixture has attributes
        Set<Attribute> toRemove = new HashSet<>();
        for (Map.Entry<Attribute, Double> v : attributes.entrySet()) {
            if (!fixture.hasAttribute(v.getKey())) toRemove.add(v.getKey());

            //Check value not present if overwrite
            if (!overwritePriority&&contains(fixture, v.getKey())) toRemove.add(v.getKey());
        }
        attributes.keySet().removeAll(toRemove);

        //Validate values TODO: make more elegant
        for (Map.Entry<Attribute, Double> v : attributes.entrySet()) {
            attributes.put(v.getKey(), validate(fixture, v.getKey(), v.getValue()));
        }

        //Assign values to this fixture's mapping
        store.put(fixture, attributes);

        if (hasUpdateAction()) updateAction.run();
    }

    public double validate(Fixture fixture, Attribute attribute, double value) {
        ProfileChannel channel = fixture.getProfile().getChannelWithAttribute(attribute);
        if (!channel.valueInRange(value)) {
            if (value>channel.getMaxValue()) return channel.getMaxValue();
            else return channel.getMinValue();
        }
        return value;
    }
    
    public void remove(Fixture fixture) {
        store.remove(fixture);
    }

    public void remove(Fixture fixture, Attribute attribute) {
        if (store.containsKey(fixture)&&store.get(fixture)!=null) {
            store.get(fixture).remove(attribute);

            //If no values left then remove fixture
            if (store.get(fixture).isEmpty()) remove(fixture);
        }

        if (hasUpdateAction()) updateAction.run();
    }

    public void clear() {
        store.clear();
        if (hasUpdateAction()) updateAction.run();
    }

    public Double get(Fixture fixture, Attribute attribute) {
        if (fixture==null||attribute==null||!store.containsKey(fixture)||!store.get(fixture).containsKey(attribute)) return NONE;
        return store.get(fixture).get(attribute);
    }

    public Set<Fixture> getFixtureSet() {return store.keySet();}

    public Map<Attribute, Double> getFixtureValues(Fixture fixture) {return store.get(fixture);}

    public Map<Attribute, Double> getFixtureValuesMatchingGroup(Fixture fixture, FeatureGroup group) {
        Map<Attribute, Double> result = new HashMap<>();
        Map<Attribute, Double> values = store.get(fixture);
        if (values==null) return result;

        for (Map.Entry<Attribute, Double> value : values.entrySet()) {
            if (value.getKey().getFeature().getFeatureGroup()==group) result.put(value.getKey(), value.getValue());
        }
        return result;
    }

    public boolean contains(Fixture fixture) {return fixture!=null&&store.containsKey(fixture);}

    public boolean contains(Fixture fixture, Attribute attribute) {
        return fixture!=null&&attribute!=null
            &&store.containsKey(fixture)&&store.get(fixture)!=null
            &&store.get(fixture).containsKey(attribute);
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
        for (Fixture f : o.getFixtureSet()) {
            set(f, o.getFixtureValues(f), overwritePriority);
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

        for (Fixture f : getFixtureSet()) {
            for (Map.Entry<Attribute, Double> v: getFixtureValues(f).entrySet()) {
                if (filter.contains(v.getKey())) filtered.set(f, v.getKey(), v.getValue(), true);
            }
        }
        return filtered;
    }

    public DataStore getZeroedClone() {
        DataStore clone = this.clone();

        for (Map.Entry<Fixture, Map<Attribute, Double>> e : clone.store.entrySet()) {
            if (e.getKey()==null||e.getValue()==null) continue;
            for (Attribute attr : e.getValue().keySet()) clone.store.get(e.getKey()).put(attr, 0d);
        }

        return clone;
    }

    @Override
    public DataStore clone() {
        Map<Fixture, Map<Attribute, Double>> newStore = new HashMap<>();

        for (Map.Entry<Fixture, Map<Attribute, Double>> e : store.entrySet()) {
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

    @Override
    public byte[] getBytes() {
        PersistencyWriter pW = new PersistencyWriter();

        for (Fixture f : getFixtureSet()) {
            pW.openSegment();
            pW.put(f.getAddress().getBytes());
    
            for (Map.Entry<Attribute, Double> e : getFixtureValues(f).entrySet()) {
                //pW.put(e.getKey().getBytes());
                pW.put(e.getValue().byteValue());
            }
            pW.closeSegmenet();
        }

        pW.wrapInSegment();
        return pW.toArray();
    }

    @Override
    public void generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
