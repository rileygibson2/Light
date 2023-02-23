package guipackage.gui.components.complexcomponents.pools;

import java.awt.Color;

import guipackage.general.Rectangle;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.SimpleBox;
import light.zones.Pool.PoolType;

public class PoolCellGUI extends Component {

    SimpleBox mainBox;

    public PoolCellGUI(Rectangle r, PoolType type) {
        super(r);
        
        mainBox = new SimpleBox(new Rectangle(0, 0, 100, 100), Color.RED);
        mainBox.setRounded(true);
    }
    
}
