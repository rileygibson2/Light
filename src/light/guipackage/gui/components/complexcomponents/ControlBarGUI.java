package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;

import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.components.basecomponents.Image;
import light.guipackage.gui.components.boxes.CollumnBox;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.uda.guiinterfaces.ControlBarGUIInterface;

public class ControlBarGUI extends SimpleBox implements ControlBarGUIInterface {

    public ControlBarGUI(UnitRectangle r) {
        super(r);
        setRounded(true);
        setColor(new Color(20, 20, 20));
        setBorder(new Color(50, 50, 50));

        CollumnBox mainBox = new CollumnBox();
        mainBox.setMaxWidth(r.width);
        mainBox.setMaxHeight(r.height);
        addComponent(mainBox);

        //Buttons
        Image patch = new Image(new UnitRectangle(0, Unit.pcw, 0, Unit.pch, 5, Unit.vw, 5, Unit.vh), "patchicon.png");
        patch.setColor(Color.BLUE);
        //mainBox.addComponent(patch);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
