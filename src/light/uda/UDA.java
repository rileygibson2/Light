package light.uda;

import java.util.HashSet;
import java.util.Set;

import light.Pool;
import light.guipackage.cli.CLI;
import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.gui.GUI;
import light.uda.guiinterfaces.UDAGUIInterface;

public class UDA {

    UDAGUIInterface gui;
    public Point size;
    public Set<UDAZone<?>> zones;

    public UDA() {
        size = new Point(15, 0);
        zones = new HashSet<UDAZone<?>>();

        gui = (UDAGUIInterface) GUI.getInstance().addToGUI(this);
        size = gui.getSize();
    }

    public void addPool(Pool<?> pool, Rectangle zoneRec) {
        UDAZone<Pool<?>> zone = new UDAZone<Pool<?>>(pool, null);
        zones.add(zone);
    }

    public void doClick(int x, int y) {
        // Check other zones to find edge of new zone - bias this towards a zone spanning left to right
        Point edge = size.clone();
        for (UDAZone<?> zone : zones) {
            if (zone.cells.y==y&&zone.cells.x>=x&&zone.cells.x<edge.x) edge.x = zone.cells.x;
        }
        //Width has been found so check if anything below to get height
        for (UDAZone<?> zone : zones) {
            if (((zone.cells.x>=x&&zone.cells.x<=x+edge.x)||(zone.cells.x<=x&&zone.cells.x+zone.cells.width>=x))&&
            zone.cells.y>y&&zone.cells.y<edge.y) {
                edge.y = zone.cells.y;
            }
        }

        Rectangle r = new Rectangle(x, y, edge.x-x, edge.y-y);
        if (r.x>size.x||r.x<0||r.y>size.y||r.y<0||r.width<=0||r.height<=0) {
            CLI.error("Cannot create zone with dims "+r);
            return;
        }
        
        gui.openZonePicker(r);
    }

    public void clear() {

    };
}
