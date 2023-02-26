package light.layouts;

import java.util.HashSet;
import java.util.Set;

import light.zones.Zone;

public abstract class Layout {
    
    private Set<Zone> zones;

    public Layout() {
        zones = new HashSet<Zone>();
    }

    public void addZone(Zone z) {zones.add(z);}

    public Set<Zone> getZones() {return zones;}
}
