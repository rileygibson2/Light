package light.stores;

import light.general.ConsoleAddress;
import light.persistency.PersistencyCapable;
import light.uda.UDA;

public class View extends AbstractStore implements PersistencyCapable {
    
    private UDA uda;

    public View(ConsoleAddress address) {
        super(address);
        this.uda = new UDA(this);
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
        
    }

    @Override
    public void merge(AbstractStore toMerge) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'merge'");
    }

    @Override
    public void replace(AbstractStore toReplace) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'replace'");
    }

    @Override
    public byte[] getBytes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }

    @Override
    public void generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
