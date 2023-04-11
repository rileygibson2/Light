package light.uda;

import java.util.HashSet;
import java.util.Set;

import light.Light;
import light.fixtures.Fixture;
import light.general.ConsoleAddress;
import light.guipackage.cli.CLI;
import light.guipackage.general.Pair;
import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.gui.GUI;
import light.uda.guiinterfaces.UDAGUIInterface;

public class UDA {
    
    UDAGUIInterface gui;
    public Point size;
    public Set<Pair<UDACapable, Rectangle>> zones;
    
    public UDA() {
        size = new Point(15, 0);
        zones = new HashSet<Pair<UDACapable, Rectangle>>();
        
        gui = (UDAGUIInterface) GUI.getInstance().addToGUI(this);
        size = gui.getSize();
    }
    
    public void createZone(Object tag, Rectangle zoneRec) {
        UDACapable o = null;

        //All pools
        if (tag instanceof ConsoleAddress) o = Light.getInstance().getPool((ConsoleAddress) tag);
        if (tag==FixtureWindow.class) o = new FixtureWindow();

        if (o!=null) {
            zones.add(new Pair<>(o, zoneRec));
            GUI.getInstance().addToGUI(o);
        }
    }

    public Rectangle getCells(UDACapable zone) {
        for (Pair<UDACapable, Rectangle> z : zones) if (z.a==zone) return z.b;
        return null;
    }
    
    public void doClick(int x, int y) {
        // Check other zones to find edge of new zone - bias this towards a zone spanning left to right
        Point edge = size.clone();
        for (Pair<? extends Object, Rectangle> zone : zones) {
            if (zone.b.y>=y&&zone.b.x>=x&&zone.b.x<edge.x) edge.x = zone.b.x;
        }
        //Width has been found so check if anything below to get height
        for (Pair<? extends Object, Rectangle> zone : zones) {
            if (((zone.b.x>=x&&zone.b.x<=x+edge.x)||(zone.b.x<=x&&zone.b.x+zone.b.width>=x))&&
            zone.b.y>y&&zone.b.y<edge.y) {
                edge.y = zone.b.y;
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
