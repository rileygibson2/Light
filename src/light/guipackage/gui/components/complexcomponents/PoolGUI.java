package light.guipackage.gui.components.complexcomponents;

import java.util.ArrayList;
import java.util.List;

import light.Pool;
import light.general.ConsoleAddress;
import light.guipackage.general.Rectangle;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.uda.UDA;
import light.uda.guiinterfaces.PoolGUIInterface;

public class PoolGUI extends Component implements PoolGUIInterface {

    private Pool<?> pool;
    private SimpleBox mainBox;
    private List<PoolCellGUI> poolCellGUIs;

    public PoolGUI(UnitRectangle r, Pool<?> pool) {
        super(r);
        this.pool = pool;

        mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
        addComponent(mainBox);

        //Make cells
        poolCellGUIs = new ArrayList<PoolCellGUI>();
        
        UDA uda = GUI.getCurrentUDAGUI().getUDA();
        UnitPoint cellDims = GUI.getCurrentUDAGUI().getCellDimensions();
        Rectangle poolCells = uda.getCellsForUDAElement(pool);
        PoolCellGUI p;

        int i = 0;
        for (int y=0; y<poolCells.height; y++) {
            for (int x=0; x<poolCells.width; x++, i++) {
                ConsoleAddress a = pool.getAddress().clone();
                a.setSuffix(i);
                boolean titleCell = (y==0&&x==0) ? true : false;

                p = new PoolCellGUI(new UnitRectangle(new UnitValue(0, Unit.vw), new UnitValue(0, Unit.vh), cellDims.x, cellDims.y), pool, a, titleCell);
                p.setPosition(Position.Relative);
                poolCellGUIs.add(p);
                mainBox.addComponent(p);
            }
        }

        poolCellGUIs.get(poolCellGUIs.size()-1).addDragIcon();
    }

    public Pool<?> getPool() {return pool;}

    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
