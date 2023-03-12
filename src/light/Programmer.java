package light;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import light.general.Attribute;
import light.general.DataStore;

public class Programmer extends DataStore {
    
    private static Programmer singleton;

    List<Fixture> selectedFixtures;

    private Programmer() {
        super();
        selectedFixtures = new ArrayList<Fixture>();
    }

    public static Programmer getInstance() {
        if (singleton==null) singleton = new Programmer();
        return singleton;
    }

    public void selectFixture(Fixture f) {selectedFixtures.add(f);}
    public void selectFixtures(List<Fixture> f) {selectedFixtures.addAll(f);}

    public void deselectFixture(Fixture f) {selectedFixtures.remove(f);}
    public void deselectFixtures(List<Fixture> f) {selectedFixtures.removeAll(f);}

    public void clearSelectedFixtures() {selectedFixtures.clear();}
}
