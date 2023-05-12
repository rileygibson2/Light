package light.stores;

import java.util.ArrayList;
import java.util.List;

import light.Programmer;
import light.fixtures.Fixture;
import light.general.ConsoleAddress;
import light.persistency.PersistencyCapable;

public class Group extends AbstractStore implements PersistencyCapable {

    List<Fixture> fixtures;

    public Group(ConsoleAddress address) {
        super(address);
        fixtures = new ArrayList<Fixture>();
    }

    public void add(Fixture fixture) {fixtures.add(fixture);}

    public void remove(Fixture fixture) {fixtures.remove(fixture);}

    public boolean contains(Fixture fixture) {return fixtures.contains(fixture);}

    public int getSize() {return fixtures.size();}

    @Override
    public void load() {
        Programmer.getInstance().select(fixtures);
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
        return new byte[0];
    }

    public static Group generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
