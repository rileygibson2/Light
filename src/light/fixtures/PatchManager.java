package light.fixtures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import light.fixtures.profile.Profile;
import light.general.ConsoleAddress;

public class PatchManager {
    
    private static PatchManager singleton;
    
    private Map<Profile, Set<Fixture>> patch;
    
    private PatchManager() {
        patch = new LinkedHashMap<Profile, Set<Fixture>>();
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
        if (!address.matchesScope(Fixture.class)) return null;
        for (Set<Fixture> profile : patch.values()) {
            for (Fixture f : profile) {
                if (f.getAddress().equals(address)) return f;
            }
        }
        return null;
    }

    public List<Fixture> getFixtures(List<ConsoleAddress> addresses) {
        List<Fixture> fixtures = new ArrayList<>();
        
        for (Set<Fixture> profile : patch.values()) {
            for (Fixture f : profile) {
                if (addresses.contains(f.getAddress())) fixtures.add(f);
            }
        }
        Collections.sort(fixtures);
        return fixtures;
    }

    public List<Fixture> getFixtures(Profile profile) {
        List<Fixture> fixtures = new ArrayList<>();
        fixtures.addAll(patch.get(profile));
        Collections.sort(fixtures);
        return fixtures;
    }

    public List<Fixture> allFixtureList() {
        List<Fixture> allFixtures = new ArrayList<>();
        for (Set<Fixture> profile : patch.values()) allFixtures.addAll(profile);
        Collections.sort(allFixtures);
        return allFixtures;
    }

    public List<Attribute> allAttributeList() {
        Set<Attribute> allAttributes = new HashSet<>();
        for (Fixture fixture : allFixtureList()) {
            allAttributes.addAll(fixture.getProfile().getAttributeList());
        }
        List<Attribute> result = new ArrayList<>(allAttributes);
        Collections.sort(result);
        return result;
    }

    public Set<Profile> allProfileSet() {
        Set<Profile> result = new LinkedHashSet<>();
        for (Profile profile : patch.keySet()) result.add(profile);
        return result;
    }
    
    public void fixtureProfileUpdated(Fixture f) {
        remove(f);
        addFixture(f);
    }
}
