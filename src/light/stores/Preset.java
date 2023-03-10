package light.stores;

import light.general.Addressable;
import light.general.ConsoleAddress;

public class Preset extends Addressable {
    
    public enum PresetType {
        Dimmer,
        Color,
        Position,
        Focus,
        Beam,
        Gobo,
        Shaper, Prisim;
    };

    private PresetType type;

    private DataStore data;

    public Preset(ConsoleAddress address, PresetType type) {
        super(address);
        this.type = type;
        data = new DataStore();
    }

    public PresetType getType() {return type;}
}
