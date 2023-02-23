package light.zones;

import guipackage.general.Rectangle;
import guipackage.gui.GUI;
import light.Light;
import light.zones.Pool.PoolType;

public class UDA extends Zone {

    public int size;

    public UDA() {
        size = 10;
        GUI.getInstance().addZoneToView(this);
    }

    public void cellClicked(int row, int col) {
        Pool p = new Pool(PoolType.Beam, new Rectangle(row, col, (size-1)-row, (size-1)-col));
        Light.getInstance().getCurrentLayout().addZone(p);
    }
}
