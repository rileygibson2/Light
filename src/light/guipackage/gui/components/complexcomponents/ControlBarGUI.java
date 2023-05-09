package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;

import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.primitives.Image;
import light.guipackage.gui.components.primitives.TempWindow;
import light.guipackage.gui.components.primitives.boxes.CollumnBox;
import light.guipackage.gui.components.primitives.boxes.SimpleBox;
import light.uda.guiinterfaces.ControlBarGUIInterface;

public class ControlBarGUI extends SimpleBox implements ControlBarGUIInterface {

    public ControlBarGUI(UnitRectangle r) {
        super(r);
        setRounded(true);
        setColor(new Color(20, 20, 20));
        setBorder(new Color(50, 50, 50));

        CollumnBox mainBox = new CollumnBox();
        mainBox.setMinWidth(new UnitValue(100, Unit.pcw));
        mainBox.setMinHeight(new UnitValue(100, Unit.pch));
        mainBox.setMaxWidth(new UnitValue(100, Unit.pcw));
        mainBox.setMaxHeight(new UnitValue(100, Unit.pch));
        addComponent(mainBox);

        //Buttons
        SimpleBox button = new SimpleBox(new UnitRectangle(0, Unit.pcw, 0, Unit.pch, 100, Unit.pcw, 100, Unit.pcw));
        button.setColor(new Color(100, 100, 100));
        button.setRounded(true);
        button.setClickAction(() -> openPatch());
        mainBox.addComponent(button);

        Image patch = new Image(new UnitRectangle(10, Unit.pcw, 10, Unit.pch, 80, Unit.pcw, 80, Unit.pch), "patchicon.png");
        button.addComponent(patch);
    }

    private void openPatch() {
        TempWindow tW = new TempWindow("Fixture Patch");
        GUI.getInstance().getCurrentRoot().addComponent(tW);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
