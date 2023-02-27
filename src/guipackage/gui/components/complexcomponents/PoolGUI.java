package guipackage.gui.components.complexcomponents;

import java.util.ArrayList;
import java.util.List;

import guipackage.general.Point;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.SimpleBox;
import guipackage.gui.components.complexcomponents.PoolCellGUI.PoolCellType;
import light.zones.Pool;

public class PoolGUI extends Component {

    Pool pool;
    SimpleBox mainBox;
    List<PoolCellGUI> poolCells;

    public PoolGUI(Pool pool) {
        super(new UnitRectangle());
        this.pool = pool;

        mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
        addComponent(mainBox);

        Point cellDims = GUI.getUDAPair().a.getCellDims();
        setX(new UnitValue(pool.cells.x*cellDims.x, Unit.pcw));
        setY(new UnitValue(pool.cells.y*cellDims.y, Unit.pch));
        setWidth(new UnitValue(pool.cells.width*cellDims.x, Unit.pcw));
        setHeight(new UnitValue(pool.cells.height*cellDims.y, Unit.pch));
        
        //Change cell dims to be realtive to this box
        cellDims = GUI.getUDAPair().a.translateToElement(GUI.getUDAPair().a.getCellDims(), this);

        //Make cells
        poolCells = new ArrayList<PoolCellGUI>();
        PoolCellGUI p;

        int i = 0;
        for (int y=0; y<pool.cells.height; y++) {
            for (int x=0; x<pool.cells.width; x++, i++) {
                PoolCellType t = PoolCellType.Empty;
                if (y==0&&x==0) t = PoolCellType.Title;

                p = new PoolCellGUI(new UnitRectangle(x*cellDims.x, y*cellDims.y, cellDims.x, cellDims.y), this, t, i);
                poolCells.add(p);
                mainBox.addComponent(p);
            }
        }

        poolCells.get(poolCells.size()-1).addDragIcon();
    }
}
