package light.uda.guiinterfaces;

import java.util.Collection;

import light.fixtures.Fixture;

public interface FixtureWindowGUIInterface extends GUIInterface {
    
    public void updateFixtures(Collection<Fixture> fixtures);
}
