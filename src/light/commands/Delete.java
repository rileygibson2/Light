package light.commands;

import light.Light;
import light.Pool;
import light.general.ConsoleAddress;

public class Delete implements Command {

    ConsoleAddress target;

    public Delete(ConsoleAddress target) {
        this.target = target;
    }


    @Override
    public void execute() {
        //Delete command at this stage can only be used to delete something that will live in a pool
        Pool<?> pool = Light.getInstance().getPoolWithScope(target.getScope());
        pool.remove(target);
    }
}
