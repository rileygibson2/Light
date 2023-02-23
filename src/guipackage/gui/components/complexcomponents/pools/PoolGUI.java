package guipackage.gui.components.complexcomponents.pools;

import guipackage.cli.CLI;
import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.SimpleBox;
import guipackage.gui.components.complexcomponents.UDAGUI;
import light.zones.Pool;
import light.zones.UDA;

public class PoolGUI extends Component {

    Pool pool;
    SimpleBox mainBox;
    Point cellDims;

    public PoolGUI(Pool pool, UDA uda, UDAGUI udaGUI) {
        super(new Rectangle());

        mainBox = new SimpleBox(new Rectangle(0, 0, 100, 100), GUI.fg);
        mainBox.setRounded(true);
        addComponent(mainBox);

        cellDims = new Point(100/uda.size, GUI.getScreenUtils().rHP(udaGUI, 100/uda.size));
        setX(pool.cellDims.x*cellDims.x);
        setY(pool.cellDims.y*cellDims.y);
        setWidth(pool.cellDims.width*cellDims.x);
        setHeight(pool.cellDims.height*cellDims.y);

        //mainBox.addComponent(new Image(new Rectangle(0.5, 15, 4, 70), "icon.png"));
        CLI.debug("complete"+r+", "+cellDims+", "+pool.cellDims);
    }
    
}
