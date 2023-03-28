package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;

import light.Light;
import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
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
import light.stores.Preset.PresetType;
import light.uda.UDA;
import light.uda.guiinterfaces.UDAGUIInterface;

public class UDAGUI extends Component implements UDAGUIInterface {
    
    UDA uda;
    private UnitPoint cellDims; //Dimensions of a cell relative to the mainBox
    
    private boolean windowPickerOpen;
    
    public UDAGUI(UnitRectangle r, UDA uda) {
        super(r);
        this.uda = uda;
        cellDims = new UnitPoint(8, Unit.vw, 8, Unit.vw); //Cell dimensions
        
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
    }
    
    public Point getSize() {
        UnitValue width = translateToUnit(getWidth(), this, cellDims.x.u, this);
        UnitValue height = translateToUnit(getHeight(), this, cellDims.y.u, this);
        return new Point((int) (width.v/cellDims.x.v), (int) (height.v/cellDims.y.v));
    }
    
    public UnitPoint getCellDims() {return cellDims;}
    
    @Override
    public void doClick(Point p) {
        Point p1 = scalePoint(p);
        
        int x = (int) Math.abs((p1.x*100)/translateToUnit(cellDims.x, this, Unit.pcw, this).v);
        int y = (int) Math.abs((p1.y*100)/translateToUnit(cellDims.y, this, Unit.pch, this).v);
        uda.doClick(x, y);
        super.doClick(p);
    }
    
    public void openZonePicker(Rectangle zoneRec) {
        if (windowPickerOpen) return;
        
        TempWindow tB = new TempWindow("Create Zone");
        addComponent(tB);
        tB.addTab("Presets");
        tB.addTab("Pools");
        tB.addTab("Sheets");
        tB.addTab("Playbacks");
        tB.addTab("Other");
        
        //Pools tab
        CollumnBox cB = new CollumnBox(new UnitPoint(25, Unit.px, 0, Unit.px));
        cB.setPosition(Position.Relative);
        
        int i = 0;
        for (PresetType p : PresetType.values()) {
            if (i==3) {
                tB.addContent(cB, 0);
                cB = new CollumnBox(new UnitPoint(25, Unit.px, 0, Unit.px));
                cB.setPosition(Position.Relative);
                i = 0;
            }
            Label l = new Label(new UnitRectangle(0, 10, 100, 50, Unit.px), p.toString(), new Font(GUI.baseFont, Font.BOLD, 11), new Color(230, 230, 230));
            l.setColor(GUI.bg);
            l.setRounded(true);
            l.setBorder(GUI.focusOrange);
            l.setTextCentered(true);

            l.setClickAction(() -> uda.addPool(Light.getInstance().getPresetPool(p), zoneRec));

            cB.addComponent(l);
            i++;
        }
        
        if (i==3) tB.addContent(cB, 0);
        
        // Table table = new Table(new UnitPoint(0, Unit.px, 5, Unit.vh));
        // table.addCollumn(Label.class, "Attrib", new UnitValue(50, Unit.px));
        // table.addCollumn(Label.class, "Inter", new UnitValue(150, Unit.px));
        // table.addCollumn(Label.class, "Mode", new UnitValue(50, Unit.px));
        // table.addRow();
        // contentBox.addComponent(table);
        GUI.getInstance().scanDOM(tB, "");
        windowPickerOpen = true;
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
    
}
