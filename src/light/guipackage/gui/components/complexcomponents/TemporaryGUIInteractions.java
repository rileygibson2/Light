package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;

import light.Light;
import light.commands.LabelCommand;
import light.guipackage.cli.CLI;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.Element.Center;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.basecomponents.TempWindow;
import light.guipackage.gui.components.basecomponents.TextInput;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.uda.guiinterfaces.TemporyGUIInteractionsInterface;

/**
* A collection of temp windows which are used by different backend components where
* a full GUI interface is not justified as normally a single action where the gui is required is nessacary.
*/
public class TemporaryGUIInteractions implements TemporyGUIInteractionsInterface {
    
    @Override
    public void openLabelCommandWindow(LabelCommand command) {
        TempWindow tW = new TempWindow("Label "+command.target.toDisplayString());
        tW.addSmother(80);
        
        SimpleBox box = new SimpleBox(new UnitRectangle(0, Unit.vw, 0, Unit.vh, 100, Unit.pcw, 10, Unit.vh));

        TextInput input = new TextInput(new UnitRectangle(0, Unit.vw, 0, Unit.vh, 8, Unit.vw, 5, Unit.vh));
        input.setCentered(Center.xyCentered);

        input.setActions(() -> Light.getInstance().resolveAddress(command.target).getLabel(), v -> {
            tW.getParent().removeComponent(tW); //Destroy this window
            //Re-execute label command
            command.label = v;
            command.execute();
        });
        box.addComponent(input);

        tW.addContent(box);
        GUI.getInstance().getCurrentRoot().addComponent(tW);
    }
    
}
