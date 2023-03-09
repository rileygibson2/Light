package light.stores;

import java.io.Console;

import light.general.ConsoleAddress;
import light.uda.UDA;

public class View {
    
    private UDA uda;
    private ConsoleAddress address;

    public View(UDA uda) {
        this.uda = uda;
    }

    public boolean hasAddress() {return address!=null;}
    public void setAddress(ConsoleAddress address) {this.address = address;}

    public UDA getUDA() {return uda;}

    public void clear() {
        uda.clear();
    }

    public void load() {
        
    };
}
