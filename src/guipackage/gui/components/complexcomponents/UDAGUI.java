package guipackage.gui.components.complexcomponents;

import java.awt.Color;

import guipackage.general.Point;
import guipackage.general.UnitPoint;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.TempWindow;
import guipackage.gui.components.boxes.SimpleBox;
import light.uda.UDA;
import light.uda.guiinterfaces.UDAInterface;

public class UDAGUI extends Component implements UDAInterface {
    
    UDA uda;
    private UnitPoint cellDims; //Dimensions of a cell relative to the mainBox
    
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
        uda.cellClicked(x, y);
        super.doClick(p);
    }
    
    public void openWindowPicker() {
        TempWindow tB = new TempWindow("Create Window");
        addComponent(tB);
        tB.addTab("aa");
        tB.addTab("bb");
        GUI.getInstance().scanDOM(tB, "");
    }
    
}
