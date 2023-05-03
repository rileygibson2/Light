package light.stores;

import light.commands.commandcontrol.CommandLine;
import light.commands.commandcontrol.CommandProxy;
import light.general.Addressable;
import light.general.ConsoleAddress;
import light.general.DataStore;

public abstract class AbstractStore extends Addressable {

    private DataStore store;

    public AbstractStore(ConsoleAddress address) {
        super(address);
        store = new DataStore();
    }

    public DataStore getStore() {return store;}

    public void setStore(DataStore store) {this.store = store;}

    /**
     * Overridden here so same update action passed to for Addressable super can be used for updates
     * to store as well.
     */
    @Override
    public void setUpdateAction(Runnable action) {
        super.setUpdateAction(action);
        store.setUpdateAction(action);
    }

    /**
     * Can be overridden but by default will load this store's address into the commandline
     */
    public void select() {
        //Load into command line
        CommandLine.getInstance().getCommandController().addToCommand(new CommandProxy(getAddress()));
    }

    /**
     * Used as a general purpose method trigger this store's primary action.
     * Other more specific actions can be implemented by the inheriting store or by using
     * the executor interfaces
     */
    public abstract void load();

    public abstract void merge(AbstractStore toMerge);
    public abstract void replace(AbstractStore toReplace);
}
