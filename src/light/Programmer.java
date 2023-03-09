package light;

import light.stores.DataStore;

public class Programmer {
    
    private static Programmer singleton;
    
    private DataStore data;

    private Programmer() {
        data = new DataStore();
    }

    public static Programmer getInstance() {
        if (singleton==null) singleton = new Programmer();
        return singleton;
    }

}
