package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

import light.executors.Executor;
import light.general.ConsoleAddress;
import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.general.Submitter;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.basecomponents.Label;
import light.guipackage.gui.components.basecomponents.TempWindow;
import light.guipackage.gui.components.boxes.CollumnBox;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.stores.Group;
import light.stores.Preset.PresetType;
import light.stores.effects.Effect;
import light.uda.FixtureWindow;
import light.uda.UDA;
import light.uda.guiinterfaces.UDAGUIInterface;

public class UDAGUI extends Component implements UDAGUIInterface {
    
    private UDA uda;
    private UnitPoint cellDims; //Dimensions of a cell relative to the mainBox
    
    private TempWindow openZonePicker;
    
    public UDAGUI(UnitRectangle r, UDA uda) {
        super(r);
        this.uda = uda;
        cellDims = new UnitPoint(6, Unit.vw, 6, Unit.vw); //Cell dimensions
        
        setDOMEntryAction(() -> {
            //Add dots
            for (int i=0; i<300; i++) {
                SimpleBox sB = new SimpleBox(new UnitRectangle(new UnitValue(), new UnitValue(), cellDims));
                //sB.setBorder(Color.WHITE);
                sB.setPosition(Position.Relative);
                addComponent(sB);
                SimpleBox oval = new SimpleBox(new UnitRectangle(48, 48, 4, 4), new Color(100, 100, 100));
                oval.setOval(true);
                sB.addComponent(oval);
                
                //Check not overflowing edge
                UnitValue x = translateToUnit(sB.getFuncX(), sB, getWidth().u, this);
                UnitValue y = translateToUnit(sB.getFuncY(), sB, getHeight().u, this);
                if (x.v>getWidth().v||y.v>getHeight().v) {
                    removeComponent(sB);
                    break;
                }
            }
        });
        
        //Click action
        setClickAction(new Submitter<Point>() {
            @Override
            public void submit(Point p) {
                click(p);
            }
        });
    }
    
    public Point getSize() {
        UnitValue width = translateToUnit(getWidth(), this, cellDims.x.u, this);
        UnitValue height = translateToUnit(getHeight(), this, cellDims.y.u, this);
        return new Point((int) (width.v/cellDims.x.v), (int) (height.v/cellDims.y.v));
    }
    
    public UnitPoint getCellDims() {return cellDims;}
    
    public UDA getUDA() {return uda;}
    
    public void click(Point p) {
        Point p1 = scalePoint(p);
        
        int x = (int) Math.abs((p1.x*100)/translateToUnit(cellDims.x, this, Unit.pcw, this).v);
        int y = (int) Math.abs((p1.y*100)/translateToUnit(cellDims.y, this, Unit.pch, this).v);
        uda.doClick(x, y);
    }
    
    public void openZonePicker(Rectangle zoneRec) {
        if (openZonePicker!=null) return;
        TempWindow tB = new TempWindow("Create Zone");
        addComponent(tB);
        tB.addTab("Presets");
        tB.addTab("Pools");
        tB.addTab("Sheets");
        tB.addTab("Playbacks");
        tB.addTab("Other");
        
        //Presets tab
        Map<String, Object> nametags = new LinkedHashMap<>();
        for (PresetType p : PresetType.values()) nametags.put(p.toString(), p.getBaseAddress());
        fillTab(tB, 0, zoneRec, nametags);
        
        //Pools tab
        nametags.clear();
        nametags.put("Groups", ConsoleAddress.getBase(Group.class));
        nametags.put("Effects", ConsoleAddress.getBase(Effect.class));
        nametags.put("Executors", ConsoleAddress.getBase(Executor.class));
        fillTab(tB, 1, zoneRec, nametags);
        
        //Sheets tab
        nametags.clear();
        nametags.put("Fixtures", FixtureWindow.class);
        fillTab(tB, 2, zoneRec, nametags);
        
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
            Label l = new Label(new UnitRectangle(0, 10, 100, 50, Unit.px), nametag.getKey(), new Font(GUI.baseFont, Font.BOLD, 11), new Color(230, 230, 230));
            l.setColor(GUI.bg);
            l.setRounded(true);
            l.setBorder(GUI.focusOrange);
            l.setTextCentered(true);
            l.setTag(nametag.getValue());
            
            l.setClickAction(() -> {
                closeZonePicker();
                uda.createZone(l.getTag(), zoneRec);
            });
            
            cB.addComponent(l);
            i++;
        }
        
        if (cB.getNumComponents()!=0) tB.addContent(cB, tabNum);
    }
    
    public void closeZonePicker() {
        if (openZonePicker==null) return;
        removeComponent(openZonePicker);
        openZonePicker = null;
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
