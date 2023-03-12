package light.stores;

import light.general.ConsoleAddress;
import light.uda.UDA;

public class View extends AbstractStore {
    
    private UDA uda;

    public View(UDA uda) {
        super(new ConsoleAddress(View.class, 0, 0));
        this.uda = uda;
    }

    public View(ConsoleAddress address, UDA uda) {
        super(address);
        this.uda = uda;
    }

    public UDA getUDA() {return uda;}

    public void clear() {
        uda.clear();
    }

    public void load() {
        
    };
}
