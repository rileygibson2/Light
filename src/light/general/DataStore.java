package light.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.AttributeInUseException;

import org.w3c.dom.Attr;

import light.Fixture;

public class DataStore {
    
    protected Map<Fixture, Map<Attribute, Integer>> store;
    
    public DataStore() {
        store = new HashMap<Fixture, Map<Attribute, Integer>>();
    }
    
    /**
     * 
     * @param fixture
     * @param attribute
     * @param value
     * @param overwritePriority - if set then the given value will overwrite the stores value. Otherwsie the data stores value will persist 
     */
    public void set(Fixture fixture, Attribute attribute, Integer value, boolean overwritePriority) {
        if (fixture==null||attribute==null||!fixture.hasAttribute(attribute)||value<0||value>255) return;

        //If not overwrite then return if store already contains value 
        if (!overwritePriority&&contains(fixture, attribute)) return;

        if (store.containsKey(fixture)&&store.get(fixture)!=null) { //Add when fixture already present in store
            store.get(fixture).put(attribute, value);
        }
        else { //Add when fixture not present in store
            Map<Attribute, Integer> attributes = new HashMap<Attribute, Integer>();
            attributes.put(attribute, value);
            store.put(fixture, attributes);
        }
    }

    public void set(Fixture fixture, Map<Attribute, Integer> attributes, boolean overwritePriority) {
        if (fixture==null) throw new Error("Cannot add null fixture to datastore");
        if (attributes==null) throw new Error("Cannot add null values/attributes to datastore");

        //Validate
        Set<Attribute> toRemove = new HashSet<>();
        for (Map.Entry<Attribute, Integer> v : attributes.entrySet()) {
            //Check target fixture has all attributes in given map and that values are within bounds
            if (v.getValue()<0||v.getValue()>255||!fixture.hasAttribute(v.getKey())) toRemove.add(v.getKey());

            //Check value not present if overwrite
            if (!overwritePriority&&contains(fixture, v.getKey())) toRemove.add(v.getKey());
        }
        attributes.keySet().removeAll(toRemove);

        //Assign values to this fixture's mapping
        store.put(fixture, attributes);
    }

    public boolean contains(Fixture fixture, Attribute attribute) {
        return fixture!=null&&attribute!=null
            &&store.containsKey(fixture)&&store.get(fixture)!=null
            &&store.get(fixture).containsKey(attribute);
    }
    
    public void remove(Fixture fixture) {
        store.remove(fixture);
    }

    public void remove(Fixture fixture, Attribute attribute) {
        if (store.containsKey(fixture)&&store.get(fixture)!=null) store.get(fixture).remove(attribute);
    }

    public void clear() {store.clear();}

    public Set<Fixture> getFixtureSet() {return store.keySet();}

    public Map<Attribute, Integer> getFixtureValues(Fixture fixture) {return store.get(fixture);}

    public boolean hasFixture(Fixture fixture) {return store.containsKey(fixture);}

    public boolean hasFixtureAndAttribute(Fixture fixture, Attribute attribute) {
        if (store.containsKey(fixture)&&store.get(fixture)!=null) return store.get(fixture).containsKey(attribute);
        return false;
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
            for (Map.Entry<Attribute, Integer> v: getFixtureValues(f).entrySet()) {
                if (filter.contains(v.getKey())) filtered.set(f, v.getKey(), v.getValue(), true);
            }
        }
        return filtered;
    }

    public DataStore getZeroedClone() {
        DataStore clone = this.clone();

        for (Map.Entry<Fixture, Map<Attribute, Integer>> e : clone.store.entrySet()) {
            if (e.getKey()==null||e.getValue()==null) continue;
            for (Attribute attr : e.getValue().keySet()) clone.store.get(e.getKey()).put(attr, 0);
        }

        return clone;
    }

    @Override
    public DataStore clone() {
        Map<Fixture, Map<Attribute, Integer>> newStore = new HashMap<>();

        for (Map.Entry<Fixture, Map<Attribute, Integer>> e : store.entrySet()) {
            if (e.getKey()==null||e.getValue()==null) continue;

            Map<Attribute, Integer> attrs = new HashMap<>();
            for (Map.Entry<Attribute, Integer> e1 : e.getValue().entrySet()) {
                int i = e1.getValue();
                attrs.put(e1.getKey(), i);
            }
            newStore.put(e.getKey(), attrs);
        }

        DataStore clone = new DataStore();
        clone.store = newStore;
        return clone;
    }
}
