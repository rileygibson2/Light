package light.stores;

import java.util.HashSet;
import java.util.Set;

import light.Programmer;
import light.fixtures.Attribute;
import light.fixtures.Fixture;
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

    /**
     * Returns the set of valid attributes for this preset
     * @return
     */
    public Set<Attribute> getValidAttributes() {
        Set<Attribute> attributes = new HashSet<>();

        for (Attribute a : Attribute.values()) {
            if (isValid(a)) attributes.add(a);
        }
        return attributes;
    }
    
    public void set(Fixture fixture, Attribute attribute, Double value) {
        if (!isValid(attribute)) return;  //Check attribute being added is present in this preset type
        getStore().set(fixture, attribute, value, true);
    }
    
    /**
     * Overwrites the current data store with the new one. Does not perform attribute checkin atm
     * @param store
     */
    public void set(DataStore store) {
        setStore(store);
    }

    /**
     * Evaluates whether the provided attribute is valid for this preset
     * @param a
     * @return
     */
    public boolean isValid(Attribute a) {
        return isValidForPresetType(a, type);
    }

    /**
     * Evaluates whether the provided attribute is valid for the given preset type
     * @param a
     * @param p
     * @return
     */
    public static boolean isValidForPresetType(Attribute a, PresetType p) {
        switch (p) {
            case Beam: return a.isBeam();
            case Color: return a.isColor();
            case Dimmer: return a.isIntensity();
            case Gobo: return a.isGobo();
            case Position: return a.isPosition();
            case Prisim: return a.isPrisim();
            case Shaper: return a.isShaper();
        }
        return false;
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
