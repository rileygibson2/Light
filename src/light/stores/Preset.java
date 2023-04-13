package light.stores;

import java.util.HashSet;
import java.util.Set;

import light.Programmer;
import light.fixtures.Attribute;
import light.fixtures.Fixture;
import light.fixtures.profile.Profile;
import light.fixtures.profile.ProfileChannel;
import light.general.ConsoleAddress;
import light.general.DataStore;
import light.persistency.PersistencyCapable;

public class Preset extends AbstractStore implements PersistencyCapable {
    
    public enum PresetType {
        Dimmer,
        Color,
        Position,
        Beam,
        Gobo,
        Shaper,
        Prisim;

        public ConsoleAddress getBaseAddress() {
            return new ConsoleAddress(Preset.class, this.ordinal()+1, 0);
        }
    };
    
    private PresetType type;
    
    public Preset(ConsoleAddress address, PresetType type) {
        super(address);
        this.type = type;
        setLabel("Preset "+address.toAddressString());
    }
    
    /**
     * Get type of this preset
     * @return
     */
    public PresetType getType() {return type;}

    /**
     * Get the preset type related to the prefix of the address
     * @param address
     * @return
     */
    public static PresetType getTypeFromAddress(ConsoleAddress address) {
        if (address==null||!address.matchesScope(Preset.class)) return null;
        if (address.getPrefix()<=0||address.getPrefix()-1>PresetType.values().length) return null;
        return PresetType.values()[address.getPrefix()-1];
    }
    
    public void set(Fixture fixture, Attribute attribute, Double value) {
        if (!isValidForPresetType(fixture, attribute, type)) return;  //Check attribute being added is present in this preset type
        getStore().set(fixture, attribute, value, true);
    }
    
    /**
     * Overwrites the current data store with the new one.
     * @param store
     */
    public void set(DataStore store) {
        setStore(store);
        clean();
    }

    /**
     * Cleans this preset's DataStore of all fixture values that should not be present in
     * this preset.
     */
    public void clean() {
        DataStore store = getStore();

        for (Fixture fixture : store.getFixtureSet()) {
            Set<Attribute> toRemove = new HashSet<>();
            Profile profile = fixture.getProfile();

            //Find fixture attributes that shouldn't be in this preset
            for (Attribute attribute : store.getFixtureValues(fixture).keySet()) {
                if (!profile.getChannelWithAttribute(attribute).getPresetType().equals(type)) toRemove.add(attribute);
            }
            //Remove those attributes
            for (Attribute attribute : toRemove) store.remove(fixture, attribute);
        }
    }

    /**
     * Evaluates whether the provided attribute is valid for the given preset type
     * @param a
     * @param p
     * @return
     */
    public static boolean isValidForPresetType(Fixture f, Attribute a, PresetType p) {
        ProfileChannel c = f.getProfile().getChannelWithAttribute(a);
        if (c==null) return false;
        return c.getPresetType().equals(p);
    }

    @Override
    public void load() {
        //Load into programmer
        Programmer.getInstance().combine(getStore(), true);
    }

    @Override
    public void merge(AbstractStore toMerge) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'merge'");
    }

    @Override
    public void replace(AbstractStore toReplace) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'replace'");
    }

    @Override
    public byte[] getBytes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }

    @Override
    public void generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
