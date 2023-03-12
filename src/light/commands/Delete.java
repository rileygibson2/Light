package light.commands;

import light.Light;
import light.Pool;
import light.general.ConsoleAddress;
import light.stores.AbstractStore;

public class Delete implements Command {

    ConsoleAddress target;

    public Delete(ConsoleAddress target) {
        this.target = target;
    }

    @Override
    public void execute() { //Delete command at this stage can only be used to delete something that will live in a pool
        Pool<? extends AbstractStore> pool = Light.getInstance().getPoolWithScope(target.getScope());
        if (pool!=null)pool.remove(target);
    }
}
