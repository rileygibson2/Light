package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import light.encoders.Encoders;
import light.executors.Executor;
import light.general.ConsoleAddress;
import light.general.Utils;
import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.GUI;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.basecomponents.Label;
import light.guipackage.gui.components.basecomponents.TempWindow;
import light.guipackage.gui.components.boxes.CollumnBox;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.stores.Group;
import light.stores.Preset.PresetType;
import light.stores.View;
import light.stores.effects.Effect;
import light.uda.FixtureWindow;
import light.uda.KeyWindow;
import light.uda.UDA;
import light.uda.guiinterfaces.UDAGUIInterface;

public class UDAGUI extends SimpleBox implements UDAGUIInterface {
    
    private UnitPoint cellDims; //Width and height of a cell
    
    private TempWindow openZonePicker;
    
    public UDAGUI(UnitRectangle r, UDA uda) {
        super(r);
        
        /*
         * This constructor needs to find the amount of uda cells there can be for this device and
         * this screen. UDA cells must be a square and the UDA class defines the amount of cells wide
         * it wants the program to have.
         * Therefore this class uses that information to find the width of a cell based on the width
         * of this element, sets the height a cell to the same and then the UDA class can query for the
         * actual maximum number of cells deep this GUI was able to construct. The collaboration between
         * this GUI class and the UDA element is what sets the size of the user defined area.
         * 
         * Note: UDA is passed as constructor param to prevent stack overflow, as UDA is not finished
         * instantiating when the UDAGUI is created so UDA.getInstance() cannot be called.
         */
        setDOMEntryAction(() -> {
            if (cellDims!=null) return; //Stops recalculation and recreation of all dots on view switch
            /*
             * TODO need something here where on a view switch all uda capable elements are rechecked
             * incase the uda window has been changed or resized somehow.
             * Ideally this would happen without all the uda elements rebuilding
             */

            //Find cell dims
            cellDims = new UnitPoint(getFuncWidth().clone(), new UnitValue());
            cellDims.x.v = cellDims.x.v/uda.getPreferredWidth();
            cellDims.x = translateToUnit(cellDims.x, this, Unit.vw, this);
            cellDims.y = cellDims.x.clone();

            //Add dots
            int i = 0;
            for (i=0; i<1000; i++) { //Limit is just a saftey to replace a while true
                SimpleBox dot = new SimpleBox(new UnitRectangle(new UnitValue(), new UnitValue(), cellDims));
                dot.setPosition(Position.Relative);
                dot.setTag("udaDot");
                addComponent(dot);
                SimpleBox oval = new SimpleBox(new UnitRectangle(48, 48, 4, 4), new Color(100, 100, 100));
                oval.setOval(true);
                dot.addComponent(oval);
                
                //Check if last created cell's bottom edge is overflowing this height
                double bottomEdge = translateToUnit(dot.getFuncY(), this, getFuncHeight().u, this).v+translateToUnit(dot.getFuncHeight(), this, getFuncHeight().u, this).v;
                if (bottomEdge>getFuncHeight().v) {
                    removeComponent(dot);
                    break;
                }
            }

            /*
             * Need to set new height of UDAGUI to actual height of all cells
             * So need to find number of cells in each collumn.
             */
            double numInRow = Math.round(translateToUnit(getFuncWidth(), this, cellDims.x.u, this).v/cellDims.x.v);
            setHeight(new UnitValue((i/numInRow)*cellDims.y.v, cellDims.y.u));
        });
        
        //Click action
        setClickAction(p -> click(p));
    }
    
    public Point getSize() {
        if (cellDims==null) return null; //Size won't have been calculated
        UnitValue width = translateToUnit(getWidth(), this, cellDims.x.u, this);
        UnitValue height = translateToUnit(getHeight(), this, cellDims.y.u, this);
        return new Point((int) (Math.round(width.v/cellDims.x.v)), (int) (Math.round(height.v/cellDims.y.v)));
    }
    
    public UnitPoint getCellDimensions() {return cellDims;}

    public void clear() {
		Set<Component> toRemove = new HashSet<>();
		for (Component c : getComponents()) {
			if (!c.hasTag()||!c.getTag().equals("udaDot")) toRemove.add(c);
		}
		removeComponents(toRemove);
	}
    
    
    public void click(Point p) {
        Point p1 = scalePoint(p);
        
        int x = (int) Math.abs((p1.x*100)/translateToUnit(cellDims.x, this, Unit.pcw, this).v);
        int y = (int) Math.abs((p1.y*100)/translateToUnit(cellDims.y, this, Unit.pch, this).v);
        UDA.getInstance().doClick(x, y);
    }
    
    public void openZonePicker(Rectangle zoneRec) {
        if (openZonePicker!=null) return;

        TempWindow tB = new TempWindow("Create Zone");
        tB.setCloseAction(() -> {openZonePicker = null;});
        GUI.getInstance().getCurrentRoot().addComponent(tB);
        tB.addTab("Presets");
        tB.addTab("Pools");
        tB.addTab("Sheets");
        tB.addTab("Playbacks");
        tB.addTab("Other");
        
        //Presets tab
        Map<String, Object> nametags = new LinkedHashMap<>();
        for (PresetType p : PresetType.values()) nametags.put(Utils.capitaliseFirst(p.toString()), p.getBaseAddress());
        fillTab(tB, 0, zoneRec, nametags);
        
        //Pools tab
        nametags.clear();
        nametags.put("Groups", ConsoleAddress.getBase(Group.class));
        nametags.put("Effects", ConsoleAddress.getBase(Effect.class));
        nametags.put("Executors", ConsoleAddress.getBase(Executor.class));
        nametags.put("Views", ConsoleAddress.getBase(View.class));
        fillTab(tB, 1, zoneRec, nametags);
        
        //Sheets tab
        nametags.clear();
        nametags.put("Fixtures", FixtureWindow.class);
        nametags.put("Encoders", Encoders.class);
        fillTab(tB, 2, zoneRec, nametags);

        //Other tab
        nametags.clear();
        nametags.put("Command Palate", KeyWindow.class);
        fillTab(tB, 4, zoneRec, nametags);
        
        openZonePicker = tB;
    }
    
    private void fillTab(TempWindow tB, int tabNum, Rectangle zoneRec, Map<String, Object> nametags) {
        CollumnBox cB = new CollumnBox(new UnitPoint(25, Unit.px, 0, Unit.px));
        cB.setPosition(Position.Relative);
        
        int i = 0;
        for (Map.Entry<String, Object> nametag : nametags.entrySet()) {
            if (i==2) {
                tB.addContent(cB, tabNum);
                cB = new CollumnBox(new UnitPoint(25, Unit.px, 0, Unit.px));
                cB.setPosition(Position.Relative);
                i = 0;
            }
            Label l = new Label(new UnitRectangle(0, 10, 100, 50, Unit.px), nametag.getKey(), new Font(Styles.baseFont, Font.BOLD, 11), new Color(230, 230, 230));
            l.setColor(Styles.bg);
            l.setRounded(true);
            l.setBorder(Styles.focusOrange);
            l.setTextCentered(true);
            l.setTag(nametag.getValue());
            
            l.setClickAction(() -> {
                closeZonePicker();
                UDA.getInstance().createZoneFromTag(l.getTag(), zoneRec);
            });
            
            cB.addComponent(l);
            i++;
        }
        
        if (cB.getNumComponents()!=0) tB.addContent(cB, tabNum);
    }
    
    public void closeZonePicker() {
        if (openZonePicker!=null) openZonePicker.close(false);
    }
    
    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
    
    // Table table = new Table(new UnitPoint(0, Unit.px, 5, Unit.vh));
    // table.addCollumn(Label.class, "Attrib", new UnitValue(50, Unit.px));
    // table.addCollumn(Label.class, "Inter", new UnitValue(150, Unit.px));
    // table.addCollumn(Label.class, "Mode", new UnitValue(50, Unit.px));
    // table.addRow();
    // contentBox.addComponent(table);
}
