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

    /**
     * Can be overridden but by default will load this store's address into the commandline
     */
    public void select() {
        //Load into command line
        CommandLine.getInstance().addToCommand(new CommandProxy(getAddress()));
    }

    /**
     * Used as a general purpose method trigger this store's primary action.
     * Other more specific actions can be implemented by the inheriting store or by using
     * the executor interfaces
     */
    public abstract void load();

    public abstract void merge(AbstractStore toMerge);
    public abstract void replace(AbstractStore toReplace);

    @Override
    public String toString() {
        return this.getClass().toString()+" - "+label;
    }
}
