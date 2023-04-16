package light.guipackage.gui.components.boxes;

import light.guipackage.general.UnitPoint;
import light.guipackage.gui.components.Component;

public class CollumnBox extends FlexBox {
    
    public CollumnBox() {
        super(new UnitPoint());
    }

    public CollumnBox(UnitPoint pos) {
        super(pos);
    }
    
    @Override
    public void addComponent(Component c) {
        if (c.getPosition()!=Position.GlobalFixed) c.setPosition(Position.CollumnRelative);
        super.addComponent(c);
    }

    @Override
    public void addComponentAtFront(Component c) {
        if (c.getPosition()!=Position.GlobalFixed) c.setPosition(Position.CollumnRelative);
        super.addComponentAtFront(c);
    }

    @Override
    public void addComponentAtIndex(Component c, int index) {
        if (c.getPosition()!=Position.GlobalFixed) c.setPosition(Position.CollumnRelative);
        super.addComponentAtIndex(c, index);
    }
}
