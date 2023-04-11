package light.fixtures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import light.general.ConsoleAddress;

public class PatchManager {
    
    private static PatchManager singleton;
    
    private Map<FixtureProfile, Set<Fixture>> patch;
    
    private PatchManager() {
        patch = new HashMap<FixtureProfile, Set<Fixture>>();
    }
    
    public static PatchManager getInstance() {
        if (singleton==null) singleton = new PatchManager();
        return singleton;
    }
    
    public void addFixture(Fixture fixture) {
        if (patch.containsKey(fixture.getProfile())) patch.get(fixture.getProfile()).add(fixture);
        else {
            HashSet<Fixture> profile = new HashSet<Fixture>();
            profile.add(fixture);
            patch.put(fixture.getProfile(), profile);
            
        }
    }
    
    public void remove(Fixture fixture) {
        for (Set<Fixture> profile : patch.values()) profile.remove(fixture);
    }
    
    public Fixture getFixture(ConsoleAddress address) {
        for (Set<Fixture> profile : patch.values()) {
            for (Fixture f : profile) {
                if (f.getAddress().equals(address)) return f;
            }
        }
        return null;
    }

    public Set<Fixture> getFixtures(List<ConsoleAddress> addresses) {
        Set<Fixture> fixtures = new HashSet<>();
        
        for (Set<Fixture> profile : patch.values()) {
            for (Fixture f : profile) {
                if (addresses.contains(f.getAddress())) fixtures.add(f);
            }
        }
        return fixtures;
    }

    public Set<Fixture> allFixtureSet() {
        Set<Fixture> allFixtures = new HashSet<>();
        for (Set<Fixture> profile : patch.values()) allFixtures.addAll(profile);
        return allFixtures;
    }
    
    public void fixtureProfileUpdated(Fixture f) {
        remove(f);
        addFixture(f);
    }
}
