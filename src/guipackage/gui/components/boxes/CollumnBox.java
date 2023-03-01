package guipackage.gui.components.boxes;

import guipackage.general.UnitPoint;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.components.Component;

public class CollumnBox extends FlexBox {
    
    public CollumnBox() {
        super(new UnitPoint());
    }

    public CollumnBox(UnitPoint pos) {
        super(pos);
    }
    
    @Override
    public void addComponent(Component c) {
        super.addComponent(c);
        position();
    }
    
    @Override
    public void childUpdated() {
        position();
        super.childUpdated();
    }
    
    public void position() {
        UnitValue y = new UnitValue(0, Unit.px);
        for (Component c : getComponents()) {
            c.setYNQ(y);
            UnitValue posY = translateToUnit(c.getY(), c, Unit.px, c);
            UnitValue height = translateToUnit(c.getHeight(), c, Unit.px, c);
            y = new UnitValue(posY.v+height.v, Unit.px);
        }
    }
}
