package light.uda;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import light.Light;
import light.encoders.Encoders;
import light.general.ConsoleAddress;
import light.guipackage.cli.CLI;
import light.guipackage.general.Pair;
import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.gui.GUI;
import light.stores.View;
import light.uda.guiinterfaces.GUIInterface;
import light.uda.guiinterfaces.UDAGUIInterface;

public class UDA {
    
    private static UDA singleton;
    
    private Map<Pair<UDACapable, Rectangle>, GUIInterface> zones;
    
    private int preferredWidth = 18;
    
    private UDA() {
        zones = new HashMap<Pair<UDACapable, Rectangle>, GUIInterface>();
    }
    
    public static UDA getInstance() {
        if (singleton==null) singleton = new UDA();
        return singleton;
    }
    
    public void loadView(View view) {
        clear();
        for (Pair<UDACapable, Rectangle> zone : view.getZones()) {
            if (!validateZoneRectangle(zone.b)) continue;
            createZone(zone.a, zone.b);
        }
    }
    
    public void clear() {   
        zones.clear();
        GUI.getInstance().clearUDAGUI();
    }
    
    /**
    * Primarily used by gui so zones can be built by a tag stored in a gui element rather then storing
    * a reference to the full UDA capable class in the element.
    * @param tag
    * @param zoneRec
    */
    public void createZoneFromTag(Object tag, Rectangle zoneRec) {
        //Get UDACapable object from tag
        UDACapable o = null;
        if (tag instanceof ConsoleAddress) o = Light.getInstance().getPool((ConsoleAddress) tag);
        if (tag==FixtureWindow.class) o = new FixtureWindow();
        if (tag==Encoders.class) o = Encoders.getInstance();
        if (tag==KeyWindow.class) o = new KeyWindow();
        
        if (o!=null) createZone(o, zoneRec);
    }
    
    private void createZone(UDACapable o, Rectangle zoneRec) {
        if (o==null||zoneRec==null||!validateZoneRectangle(zoneRec)) return;
        
        //Add to store
        Pair<UDACapable, Rectangle> zone = new Pair<>(o, zoneRec);
        zones.put(zone, null); //Need to add first so GUI instantiation can read cell rect if needs to
        GUIInterface g = GUI.getInstance().addUDAElementToGUI(o, zoneRec);

        if (g!=null) zones.put(zone, g); //Overwrite pervious to add gui interface
        else zones.remove(zone); //Problem getting gui interface so remove mapping
    }
    
    public Rectangle getCellsForUDAElement(UDACapable zone) {
        for (Pair<UDACapable, Rectangle> z : zones.keySet()) if (z.a==zone) return z.b;
        return null;
    }
    
    public int getPreferredWidth() {return preferredWidth;}
    
    public Point getSize() {
        GUIInterface gui = Light.getInstance().getStaticGUIElement(UDAGUIInterface.class);
        if (gui!=null) return ((UDAGUIInterface) gui).getSize();
        return null;
    }
    
    public boolean validateZoneRectangle(Rectangle zoneRec) {
        Point size = getSize();
        if (size==null) return false;
        return zoneRec.x>=0&&zoneRec.x+zoneRec.width>0&&zoneRec.y>=0&&zoneRec.height>0
        &&zoneRec.x+zoneRec.width<=size.x&&zoneRec.y+zoneRec.height<=size.y;
    }
    
    public Set<GUIInterface> getGUIInterfacesOfClass(Class<? extends GUIInterface> clazz) {
        Set<GUIInterface> result = new HashSet<>();
        for (GUIInterface inter : zones.values()) {
            List<Class<?>> interfaces = Arrays.asList(inter.getClass().getInterfaces());
            if (interfaces.contains(clazz)) result.add(inter);
        }
        return result;
    }
    
    public void doClick(int x, int y) {
        // Check other zones to find edge of new zone - bias this towards a zone spanning left to right
        Point edge = getSize().clone();
        for (Pair<UDACapable, Rectangle> z : zones.keySet()) {
            if (z.b.y>=y&&z.b.x>=x&&z.b.x<edge.x) edge.x = z.b.x;
        }
        //Width has been found so check if anything below to get height
        for (Pair<UDACapable, Rectangle> z : zones.keySet()) {
            if (((z.b.x>=x&&z.b.x<=x+edge.x)||(z.b.x<=x&&z.b.x+z.b.width>=x))&&
            z.b.y>y&&z.b.y<edge.y) {
                edge.y = z.b.y;
            }
        }
        
        Rectangle r = new Rectangle(x, y, edge.x-x, edge.y-y);
        Point size = getSize().clone();
        if (r.x>size.x||r.x<0||r.y>size.y||r.y<0||r.width<=0||r.height<=0) {
            CLI.error("Cannot create zone with dims "+r);
            return;
        }
        
        GUIInterface gui = Light.getInstance().getStaticGUIElement(UDAGUIInterface.class);
        if (gui!=null) ((UDAGUIInterface) gui).openZonePicker(r);
    }
}
