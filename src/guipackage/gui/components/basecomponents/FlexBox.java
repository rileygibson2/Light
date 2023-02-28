package guipackage.gui.components.basecomponents;

import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.components.Component;

public class FlexBox extends Component {
    
    public FlexBox(UnitRectangle r) {
        super(new UnitRectangle(r.x.v, r.x.u, r.y.v, r.y.u, 0, Unit.vw, 0, Unit.vh));
    }
    
    @Override
    public void addComponent(Component c) {
        super.addComponent(c);
        resize(); //Trigger resize
    }

    @Override
    public void childUpdated() {
        resize(); //Trigger resize
        super.childUpdated();
    }

    /**
     * Resize box to encapsulate all components.
     * Do this by finding the component which has a real value which has the furtherst away
     * bottom right corner and set the bottom right corner of this component to the same.
     */
    public void resize() {
        double newWidth = 0;
        double newHeight = 0;

        for (Component c : getComponents()) {
            UnitRectangle uR = c.getFunctionalRec();
            
            if (!uR.allUnitsReal()) {
                /*
                * Only consider elements with a relative size in the resizing if this box has a
                * min width or min height set as that is the only way they should actually affect
                * the space
                */
                if (getMinWidth().v==0&&getMinHeight().v==0) continue;
            }

            //Translate childs values into viewport values
            uR = translateToVP(uR, c, this);
            if (uR.x.v+uR.width.v>newWidth)  newWidth = uR.x.v+uR.width.v;
            if (uR.y.v+uR.height.v>newHeight) newHeight = uR.y.v+uR.height.v;
        }

        setWidth(new UnitValue(newWidth, Unit.vw));
        setHeight(new UnitValue(newHeight, Unit.vh));
    }
}
