package guipackage.gui.components.basecomponents;

import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;

public class FlexBox extends Component {
    
    public FlexBox(UnitRectangle r) {
        super(new UnitRectangle(r.x.v, r.x.u, r.y.v, r.y.u, 0, Unit.vw, 0, Unit.vh));
    }
    
    @Override
    public void addComponent(Component c) {
        super.addComponent(c);
        //Trigger resize
        resize();
    }

    /**
     * Resize box to encapsulate all components.
     * Do this by finding the component which has a real value which has the furtherst away
     * bottom right corner and set the bottom right corner of this component to the same.
     * Will not consider components that have any percentage values in their dimensions
     */
    public void resize() {
        double newWidth = 0;
        double newHeight = 0;

        for (Component c : getComponents()) {
            UnitRectangle uR = c.getRec();
            if (uR.hasUnit(Unit.pc)) continue;
            uR = GUI.getScreenUtils().translateToVP(uR);

            if (uR.x.v+uR.width.v>newWidth) newWidth = uR.x.v+uR.width.v;
            if (uR.y.v+uR.height.v>newHeight) newHeight = uR.y.v+uR.height.v;
        }

        setWidth(newWidth);
        setHeight(newHeight);
    }  
}
