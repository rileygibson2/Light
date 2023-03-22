package light.stores;

import light.commands.commandline.CommandLine;
import light.commands.commandline.CommandProxy;
import light.general.Addressable;
import light.general.ConsoleAddress;
import light.general.DataStore;

public abstract class AbstractStore extends Addressable {

    private DataStore store;
    private String label;

    public AbstractStore(ConsoleAddress address) {
        super(address);
        label = "";
        store = new DataStore();
    }

    public String getLabel() {return label;}

    public DataStore getStore() {return store;}

    public void setStore(DataStore store) {this.store = store;}

    public void setLabel(String label) {this.label = label;}

    public void select() {
        //Load into command line
        CommandLine.getInstance().addToCommand(new CommandProxy(getAddress()));
    }

    public abstract void merge(AbstractStore toMerge);
    public abstract void replace(AbstractStore toReplace);

    @Override
    public String toString() {
        return this.getClass().toString()+" - "+label;
    }
}
