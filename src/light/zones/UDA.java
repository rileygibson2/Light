package light.zones;

import java.util.HashSet;
import java.util.Set;

import guipackage.cli.CLI;
import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.complexcomponents.UDAGUI;
import light.Light;
import light.zones.Pool.PoolType;

public class UDA extends Zone {

    UDAGUI gui;
    public Point size;
    public Set<Pool> pools;

    public UDA() {
        size = new Point(15, 0);
        pools = new HashSet<Pool>();

        gui = (UDAGUI) GUI.getInstance().addZoneToView(this);
        size = gui.getSize();
    }

    public void cellClicked(int x, int y) {
        gui.openWindowPicker();
        
        //Check other pools to find edge of new pool - bias this towards a pool spanning left to right
        // Point edge = size.clone();
        // for (Pool p : pools) {
        //     if (p.cells.y==y&&p.cells.x>=x&&p.cells.x<edge.x) edge.x = p.cells.x;
        // }
        // //Width has been found so check if anything below to get height
        // for (Pool p : pools) {
        //     if (p.cells.x>=x&&p.cells.x<edge.x&&p.cells.y>=y&&p.cells.y<edge.y) edge.y = p.cells.y;
        // }

        // //Add pool
        // Pool p = new Pool(PoolType.Beam, new Rectangle(x, y, edge.x-x, edge.y-y));
        // pools.add(p);
        // Light.getInstance().getCurrentLayout().addZone(p);
    }
}
