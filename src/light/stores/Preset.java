package light.stores;

import java.util.HashSet;
import java.util.Set;

import light.Programmer;
import light.fixtures.Attribute;
import light.fixtures.Fixture;
import light.fixtures.profile.ProfileChannel;
import light.general.ConsoleAddress;
import light.general.DataStore;
import light.guipackage.cli.CLI;
import light.persistency.PersistencyCapable;
import light.persistency.PersistencyWriter;

public class Preset extends AbstractStore implements PersistencyCapable {
    
    public enum PresetType {
        DIMMER,
        COLOR,
        POSITION,
        BEAM,
        GOBO,
        SHAPER,
        PRISM;

        public ConsoleAddress getBaseAddress() {
            return new ConsoleAddress(Preset.class, this.ordinal()+1, 0);
        }

        public static PresetType getPresetType(String text) {
            for (PresetType type : PresetType.values()) {
                if (type.toString().equals(text)) return type;
            }
            return null;
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
    private void clean() {
        DataStore store = getStore();

        for (Fixture fixture : store.getFixtureSet()) {
            Set<Attribute> toRemove = new HashSet<>();

            //Find fixture attributes that shouldn't be in this preset
            for (Attribute attribute : store.get(fixture).keySet()) {
                if (!isValidForPresetType(fixture, attribute, type)) toRemove.add(attribute);
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
        CLI.debug("Loading into prog:\n"+getStore().toStoreString());
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
        PersistencyWriter pW = new PersistencyWriter();
        pW.writeObject(getAddress());
        pW.writeString(getLabel());
        pW.writeObject(getStore());
        pW.wrapInSegment();
        return pW.getBytes();
    }

    public static Preset generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
