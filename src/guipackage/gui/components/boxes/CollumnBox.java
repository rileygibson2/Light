package guipackage.gui.components.boxes;

import guipackage.general.UnitPoint;
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
        c.setCollumnRelative(true);
        super.addComponent(c);
    }
}
