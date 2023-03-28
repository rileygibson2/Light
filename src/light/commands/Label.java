package light.commands;

import light.Light;
import light.general.ConsoleAddress;
import light.stores.AbstractStore;

public class Label implements Command {

    ConsoleAddress target;
    String label;

    public Label(ConsoleAddress target, String label) {
        this.target = target;
        this.label = label;
    }

    @Override
    public void execute() {
        AbstractStore store = (AbstractStore) Light.getInstance().resolveAddress(target);
        store.setLabel(label);
    }
}
