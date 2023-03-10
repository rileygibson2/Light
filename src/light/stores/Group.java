package light.stores;

import java.util.ArrayList;
import java.util.List;

import light.Fixture;
import light.Programmer;
import light.general.ConsoleAddress;

public class Group extends AbstractStore {

    List<Fixture> fixtures;

    public Group(ConsoleAddress address) {
        super(address);
        fixtures = new ArrayList<Fixture>();
    }

    public void add(Fixture fixture) {fixtures.add(fixture);}

    public void remove(Fixture fixture) {fixtures.remove(fixture);}

    public boolean contains(Fixture fixture) {return fixtures.contains(fixture);}

    public int getSize() {return fixtures.size();}

    public void selectFixtures() {
        Programmer.getInstance().selectFixtures(fixtures);
    }

}
