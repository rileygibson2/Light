package light.uda;

import java.util.HashSet;
import java.util.Set;

import guipackage.cli.CLI;
import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.gui.GUI;
import light.Zone;
import light.uda.Pool.PoolType;
import light.uda.guiinterfaces.UDAInterface;

public class UDA extends Zone {

    UDAInterface gui;
    public Point size;
    public Set<Zone> zones;

    public UDA() {
        size = new Point(15, 0);
        zones = new HashSet<Zone>();

        gui = (UDAInterface) GUI.getInstance().addToGUI(this);
        size = gui.getSize();
    }

    public void cellClicked(int x, int y) {
        gui.openWindowPicker();
        
        
        // // Check other pools to find edge of new pool - bias this towards a pool spanning left to right
        // Point edge = size.clone();
        // for (Zone z : pools) {
        //     Pool p = (Pool) z;
        //     if (p.cells.y==y&&p.cells.x>=x&&p.cells.x<edge.x) edge.x = p.cells.x;
        // }
        // //Width has been found so check if anything below to get height
        // for (Zone z : pools) {
        //     Pool p = (Pool) z;
        //     if (((p.cells.x>=x&&p.cells.x<=x+edge.x)||(p.cells.x<=x&&p.cells.x+p.cells.width>=x))&&
        //     p.cells.y>y&&p.cells.y<edge.y) {
        //         edge.y = p.cells.y;
        //     }
        // }

        // Rectangle r = new Rectangle(x, y, edge.x-x, edge.y-y);
        // if (r.x>size.x||r.x<0||r.y>size.y||r.y<0||r.width<=0||r.height<=0) {
        //     CLI.error("Cannot create pool with dims "+r);
        //     return;
        // }

        // //Add pool
        // Pool p = new Pool(PoolType.Beam, r);
        // pools.add(p);
    }
}
