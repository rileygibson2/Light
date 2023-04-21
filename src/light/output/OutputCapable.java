package light.output;

import light.general.DataStore;

/**
 * Classes implementing this are capable of affecting the output of the console
 */
public interface OutputCapable {
    
    /**
     * Used to obtain the output of this element at the time of the request
     * Should only be called in elements that have registered themselves with Output
     * @return
     */
    public DataStore getOutput();

    /**
     * Called in elements to check they still intend to be registered at the current moment
     * in time.
     * @return Whether or not the element should be currently registered
     */
    public boolean outputRegistrationCheck();
}
