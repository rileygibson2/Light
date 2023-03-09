package light.stores;

public class Preset {
    
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

    public Preset(PresetType type) {
        this.type = type;
        data = new DataStore();
    }

    public PresetType getType() {return type;}
}
