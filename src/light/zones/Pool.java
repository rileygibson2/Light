package light.zones;

import guipackage.general.Rectangle;
import guipackage.gui.GUI;

public class Pool extends Zone {
    
    public enum PoolType {
        Dimmer,
        Color,
        Position,
        Focus,
        Beam,
        Gobo,
        Shaper,
        Effect,
        Group
    };

    PoolType type;
    public Rectangle cellDims;

    public Pool(PoolType type, Rectangle cellDims) {
        this.type = type;
        this.cellDims = cellDims;
        GUI.getInstance().addZoneToView(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pool)) return false;
        Pool p = (Pool) o;
        if (p.type!=this.type) return false;
        return true;
    }
}
