package light.guipackage.gui.components.primitives.boxes;

import java.awt.Color;

import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.components.Component;

public class FlexBox extends SimpleBox {
    
    public FlexBox(UnitPoint pos) {
        super(new UnitRectangle(pos, new UnitValue(0, pos.x.u), new UnitValue(0, pos.y.u)));
        //setColor(Color.ORANGE);
    }
    
    @Override
    public void addComponent(Component c) {
        super.addComponent(c);
        resize(); //Trigger resize
    }

    @Override
    public void addComponentAtFront(Component c) {
        super.addComponentAtFront(c);
        resize(); //Trigger resize
    }

    @Override
    public void addComponentAtIndex(Component c, int index) {
        super.addComponentAtIndex(c, index);
        resize(); //Trigger resize
    }

    @Override
    public void doPositioning() {
        resize(); //Trigger resize
        super.doPositioning();
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
            
            /* Should only consider elements with a relative size in the resizing if this box has a
            * min width or min height set as that is the only way they should actually affect
             * the space */
            if (!uR.allUnitsReal()&&getMinWidth().v==0&&getMinHeight().v==0) continue;
            
            //Translate childs values into viewport values
            uR = translateToVP(uR, c, this);
            if (uR.x.v+uR.width.v>newWidth)  newWidth = uR.x.v+uR.width.v;
            if (uR.y.v+uR.height.v>newHeight) newHeight = uR.y.v+uR.height.v;
        }

        UnitValue width = translateToUnit(new UnitValue(newWidth, Unit.vw), this, getWidth().u, this);
        UnitValue height = translateToUnit(new UnitValue(newHeight, Unit.vh), this, getHeight().u, this);
        setWidthNQ(width);
        setHeightNQ(height);
    }
}
