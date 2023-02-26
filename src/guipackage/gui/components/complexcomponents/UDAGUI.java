package guipackage.gui.components.complexcomponents;

import java.awt.Color;

import guipackage.cli.CLI;
import guipackage.general.Point;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.SimpleBox;
import guipackage.gui.components.basecomponents.TempBox;
import light.zones.UDA;

public class UDAGUI extends Component {

    UDA uda;
    SimpleBox mainBox;
    private Point cellDims; //Dimensions of a cell relative to the mainBox
    private Point size; //Size of area in cells

    public UDAGUI(UnitRectangle r, UDA uda) {
        super(r);
        this.uda = uda;

        mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
        addComponent(mainBox);

        setDOMEntryAction(() -> {
            //Cell dimensions
            cellDims = new Point(100/uda.size.x, GUI.getScreenUtils().rHP(mainBox, 100/uda.size.x, Unit.pc));
        
            //Dots
            int colCount = 0;
                for (double y=0; y<r.height.v; y+=cellDims.y) {
                    if (y+cellDims.y>r.height.v) break;
                    for (int i=0; i<uda.size.x; i++) {
                        SimpleBox sB = new SimpleBox(new UnitRectangle(i*cellDims.x, y, cellDims.x, cellDims.y));
                        mainBox.addComponent(sB);
                        SimpleBox oval = new SimpleBox(new UnitRectangle(48, 48, 4, 4), new Color(100, 100, 100));
                        oval.setOval(true);
                        sB.addComponent(oval);
                    }
                    colCount++;
                }

                size = new Point(uda.size.x, colCount);
            });
    }

    public Point getSize() {return size;}

    public Point getCellDims() {return cellDims;}

    @Override
    public void doClick(Point p) {
        Point p1 = scalePoint(p);
        int x = (int) Math.abs((p1.x*100)/cellDims.x);
        int y = (int) Math.abs((p1.y*100)/cellDims.y);
        uda.cellClicked(x, y);
        super.doClick(p);
    }

    public void openWindowPicker() {
        TempBox tB = new TempBox("Create Window");
        addComponent(tB);
        CLI.debug("tB: "+tB.toString());
        GUI.getInstance().scanDOM(tB, "");
    }
    
}
