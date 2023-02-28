package guipackage.gui.components.basecomponents;

import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.components.Component;

public class CollumnBox extends FlexBox {

    public CollumnBox(UnitRectangle r) {
        super(r);
    }

    @Override
    public void addComponent(Component c) {
        super.addComponent(c);
        position();
    }

    public void position() {
        UnitValue y = null;
        for (Component c : getComponents()) {
            if (y==null) {
                c.setY(new UnitValue(0, Unit.px));
                y = c.getY();
            }
            else {
                c.setY(y);
                UnitValue posY = translateToUnit(c.getY(), c, Unit.px, c);
                UnitValue height = translateToUnit(c.getHeight(), c, Unit.px, c);
                y = new UnitValue(posY.v+height.v, Unit.px);
            }
        }
    }
    
}
