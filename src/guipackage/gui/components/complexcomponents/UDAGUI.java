package guipackage.gui.components.complexcomponents;

import java.awt.Color;

import guipackage.cli.CLI;
import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.SimpleBox;
import light.zones.UDA;

public class UDAGUI extends Component {

    UDA uda;
    SimpleBox mainBox;
    Point cellDims;

    public UDAGUI(Rectangle r, UDA uda) {
        super(r);
        this.uda = uda;

        mainBox = new SimpleBox(new Rectangle(0, 0, 100, 100));
        addComponent(mainBox);

        //Dots
        cellDims = new Point(100/uda.size, GUI.getScreenUtils().rHP(mainBox, 100/uda.size));


        for (double y=0; y<r.height; y+=cellDims.y) {
            for (int i=0; i<uda.size; i++) {
                SimpleBox sB = new SimpleBox(new Rectangle(i*cellDims.x, y, cellDims.x, cellDims.y));
                SimpleBox oval = new SimpleBox(new Rectangle(4, 48, 4, 4), new Color(100, 100, 100));
                oval.setOval(true);
                sB.addComponent(oval);
                mainBox.addComponent(sB);
            }
        }
    }

    @Override
    public void doClick(Point p) {
        Point p1 = scalePoint(p);
        int row = (int) Math.abs((p1.x*100)/cellDims.x);
        int col = (int) Math.abs((p1.y*100)/cellDims.y);
        uda.cellClicked(row, col);
        super.doClick(p);
    }
    
}
