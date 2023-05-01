package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import light.Light;
import light.Pool;
import light.general.ConsoleAddress;
import light.guipackage.cli.CLI;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.boxes.CollumnBox;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.stores.View;
import light.uda.guiinterfaces.ViewGUIInterface;

public class ViewGUI extends SimpleBox implements ViewGUIInterface {
    
    private CollumnBox mainBox;
    private Set<PoolCellGUI> viewCells;
    
    public ViewGUI(UnitRectangle r) {
        super(r);
        viewCells = new HashSet<PoolCellGUI>();
        
        mainBox = new CollumnBox(new UnitPoint());
        mainBox.setMinWidth(new UnitValue(100, Unit.pcw));
        mainBox.setMinHeight(new UnitValue(100, Unit.pch));
        mainBox.setMaxWidth(new UnitValue(100, Unit.pcw));
        mainBox.setMaxHeight(new UnitValue(100, Unit.pch));
        addComponent(mainBox);
        
        Pool<View> viewPool = ((Light) GUI.getInstance().getCreator()).getViewPool(); //Cannot call Light.getInstance() due to point in instantiation cycle

        //Make cells
        int i = 0;
        while (i<100) {
            ConsoleAddress a = viewPool.getAddress().clone();
            a.setSuffix(i);
            
            PoolCellGUI p = new PoolCellGUI(new UnitRectangle(0, Unit.vw, 0, Unit.vh, 100, Unit.pcw, 10, Unit.vh), viewPool, a);
            p.setClickAction(() -> viewCellClicked(p));
            viewCells.add(p);
            mainBox.addComponent(p);

            if (mainBox.childrenOverflow()) break;
            i++;
        }
    }
    
    private void viewCellClicked(PoolCellGUI viewCell) {
        CLI.debug("clicked: "+viewCell.getAddress());
        View view = Light.getInstance().getViewPool().get(viewCell.getAddress());
        if (view!=null) view.load();
    }

    @Override
    public void update() {
        for (PoolCellGUI cell : viewCells) cell.build();
    }
    
}
