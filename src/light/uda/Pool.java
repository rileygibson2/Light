package light.uda;

import guipackage.general.Rectangle;
import guipackage.gui.GUI;
import light.Zone;
import light.general.ConsoleAddress;
import light.uda.guiinterfaces.PoolInterface;

public class Pool extends Zone {
    
    PoolInterface gui;

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

    public PoolType type;
    public ConsoleAddress address;
    public Rectangle cells;

    public Pool(PoolType type, Rectangle cells) {
        this.type = type;
        this.cells = cells;
        address = new ConsoleAddress(type.ordinal(), 0);
        gui = (PoolInterface) GUI.getInstance().addToGUI(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pool)) return false;
        Pool p = (Pool) o;
        if (p.type!=this.type) return false;
        return true;
    }
}
