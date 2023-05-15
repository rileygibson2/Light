package light.commands;

import light.Light;
import light.general.ConsoleAddress;
import light.guipackage.cli.CLI;
import light.guipackage.gui.GUI;
import light.stores.AbstractStore;

public class LabelCommand implements Command {

    public ConsoleAddress target;
    public String label;

    public LabelCommand(ConsoleAddress target) {
        this.target = target;
    }

    public LabelCommand(ConsoleAddress target, String label) {
        this.target = target;
        this.label = label;
    }

    @Override
    public void execute() {
        CLI.debug("Executing label");
        AbstractStore store = (AbstractStore) Light.getInstance().resolveAddress(target);
        if (store==null) return;

        if (label==null) { //Need to open label picker window
            GUI.getInstance().getTemporyActionsImplementation().openLabelCommandWindow(this);
            return;
        }
        //String has been set
        store.setLabel(label);
        
        //Update relevant guis
    }
}
