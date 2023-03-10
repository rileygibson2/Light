package light.stores;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import light.Fixture;
import light.general.Attribute;

public class DataStore {
    
    protected Map<Fixture, Map<Attribute, Integer>> store;
    
    public DataStore() {
        store = new HashMap<Fixture, Map<Attribute, Integer>>();
    }
    
    public void set(Fixture fixture, Attribute attribute, Integer value) {
        if (fixture==null) throw new Error("Cannot add null fixture to datastore");
        if (attribute==null) throw new Error("Cannot add null attribute to datastore");
        if (value<0||value>255) throw new Error("Value "+value+" outside of dmx bounds");
        if (!fixture.hasAttribute(attribute)) return;
        
        Map<Attribute, Integer> attributes;
        if (store.containsKey(fixture)&&store.get(fixture)!=null) {
            store.get(fixture).put(attribute, value);
        }
        else {
            attributes = new HashMap<Attribute, Integer>();
            attributes.put(attribute, value);
            store.put(fixture, attributes);
        }
    }

    public void set(Fixture fixture, Map<Attribute, Integer> attributes) {
        if (fixture==null) throw new Error("Cannot add null fixture to datastore");
        if (attributes==null) throw new Error("Cannot add null values/attributes to datastore");

        //Check target fixture has all attributes in map and that values are within bounds
        Set<Attribute> toRemove = new HashSet<>();
        for (Map.Entry<Attribute, Integer> v : attributes.entrySet()) {
            if (!fixture.hasAttribute(v.getKey())) toRemove.add(v.getKey());
            if (v.getValue()<0||v.getValue()>255) toRemove.add(v.getKey());
        }
        attributes.keySet().removeAll(toRemove);

        //Assign values to this fixture's mapping
        store.put(fixture, attributes);
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
