package guipackage.gui.components.complexcomponents;

import java.util.ArrayList;
import java.util.List;

import guipackage.general.UnitPoint;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.boxes.SimpleBox;
import guipackage.gui.components.complexcomponents.PoolCellGUI.PoolCellType;
import light.Pool;
import light.uda.UDAZone;
import light.uda.guiinterfaces.PoolInterface;

public class PoolGUI extends Component implements PoolInterface {

    UDAZone<Pool<?>> zone;
    SimpleBox mainBox;
    List<PoolCellGUI> poolCells;

    public PoolGUI(UDAZone<Pool<?>> zone) {
        super(new UnitRectangle());
        this.zone = zone;

        mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
        addComponent(mainBox);

        UnitPoint cellDims = GUI.getUDAPair().a.getCellDims();
        setX(new UnitValue(zone.cells.x*cellDims.x.v, cellDims.x.u));
        setY(new UnitValue(zone.cells.y*cellDims.y.v,cellDims.y.u));
        setWidth(new UnitValue(zone.cells.width*cellDims.x.v, cellDims.x.u));
        setHeight(new UnitValue(zone.cells.height*cellDims.y.v, cellDims.y.u));

        //Make cells
        poolCells = new ArrayList<PoolCellGUI>();
        PoolCellGUI p;

        int i = 0;
        for (int y=0; y<zone.cells.height; y++) {
            for (int x=0; x<zone.cells.width; x++, i++) {
                PoolCellType t = PoolCellType.Empty;
                if (y==0&&x==0) t = PoolCellType.Title;

                p = new PoolCellGUI(new UnitRectangle(new UnitValue(0, Unit.vw), new UnitValue(0, Unit.vh), cellDims.x, cellDims.y), this, t, i);
                p.setPosition(Position.Relative);
                poolCells.add(p);
                mainBox.addComponent(p);
            }
        }

        poolCells.get(poolCells.size()-1).addDragIcon();
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
