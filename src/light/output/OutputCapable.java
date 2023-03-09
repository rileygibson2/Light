package light.output;

import light.stores.DataStore;

/**
 * Classes implementing this are capable of affecting the output of the console
 */
public interface OutputCapable {
    
    public DataStore getOutput();
}
