package light.commands;

import light.stores.Cue;
import light.stores.Preset;

public class Store implements Command {

    public Store(Preset target) {

    }

    public Store(Cue target) {
        
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
    
}
