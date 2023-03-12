package light.stores;

import light.general.Addressable;
import light.general.ConsoleAddress;

public abstract class AbstractStore extends Addressable {

    private String label;

    public AbstractStore(ConsoleAddress address) {
        super(address);
        label = "";
    }

    public String getLabel() {return label;}

    public void setLabel(String label) {this.label = label;}
}
