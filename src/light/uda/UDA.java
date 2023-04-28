package light.uda;

import java.util.HashMap;
import java.util.Map;

import light.Light;
import light.encoders.Encoders;
import light.general.ConsoleAddress;
import light.guipackage.cli.CLI;
import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.gui.GUI;
import light.stores.View;
import light.uda.guiinterfaces.GUIInterface;
import light.uda.guiinterfaces.UDAGUIInterface;

public class UDA {
    
    private View view;
    private UDAGUIInterface gui;
    private Map<UDACapable, Rectangle> zones;

    private int preferredWidth = 20;
    
    public UDA(View view) {
        this.view = view;
        zones = new HashMap<UDACapable, Rectangle>();
        
        gui = (UDAGUIInterface) GUI.getInstance().addUDA(this);
    }

    public View getView() {return view;}
    
    public void createZone(Object tag, Rectangle zoneRec) {
        UDACapable o = null;

        //All pools
        if (tag instanceof ConsoleAddress) o = Light.getInstance().getPool((ConsoleAddress) tag);
        if (tag==FixtureWindow.class) o = new FixtureWindow();
        if (tag==Encoders.class) o = Encoders.getInstance();

        if (o!=null) {
            zones.put(o, zoneRec);
            GUIInterface gui = GUI.getInstance().addToGUI(o);
            o.setGUI(gui);
        }
    }

    public Rectangle getCellsForUDAElement(UDACapable zone) {
        for (Map.Entry<UDACapable, Rectangle> z : zones.entrySet()) if (z.getKey()==zone) return z.getValue();
        return null;
    }

    public int getPreferredWidth() {return preferredWidth;}

    public Point getSize() {return gui.getSize();}

    public UDACapable getUDAElementForClass(Class<? extends UDACapable> clazz) {
        for (UDACapable element : zones.keySet()) {
            if (element.getClass().equals(clazz)) return element;
        }
        return null;
    }
    
    public void doClick(int x, int y) {
        // Check other zones to find edge of new zone - bias this towards a zone spanning left to right
        Point edge = getSize().clone();
        for (Map.Entry<UDACapable, Rectangle> z : zones.entrySet()) {
            if (z.getValue().y>=y&&z.getValue().x>=x&&z.getValue().x<edge.x) edge.x = z.getValue().x;
        }
        //Width has been found so check if anything below to get height
        for (Map.Entry<UDACapable, Rectangle> z : zones.entrySet()) {
            if (((z.getValue().x>=x&&z.getValue().x<=x+edge.x)||(z.getValue().x<=x&&z.getValue().x+z.getValue().width>=x))&&
            z.getValue().y>y&&z.getValue().y<edge.y) {
                edge.y = z.getValue().y;
            }
        }
        
        Rectangle r = new Rectangle(x, y, edge.x-x, edge.y-y);
        Point size = getSize().clone();
        if (r.x>size.x||r.x<0||r.y>size.y||r.y<0||r.width<=0||r.height<=0) {
            CLI.error("Cannot create zone with dims "+r);
            return;
        }
        
        gui.openZonePicker(r);
    }
    
    public void clear() {
        
    };
}
