package light.stores;

import java.util.ArrayList;
import java.util.List;

import light.general.ConsoleAddress;
import light.guipackage.general.Pair;
import light.guipackage.general.Rectangle;
import light.persistency.PersistencyCapable;
import light.uda.UDA;
import light.uda.UDACapable;

public class View extends AbstractStore implements PersistencyCapable {
    
    private List<Pair<UDACapable, Rectangle>> zones;

    public View(ConsoleAddress address) {
        super(address);
        zones = new ArrayList<Pair<UDACapable, Rectangle>>();
    }

    public List<Pair<UDACapable, Rectangle>> getZones() {
        //Clone list
        List<Pair<UDACapable, Rectangle>> copy = new ArrayList<>();
        for (Pair<UDACapable, Rectangle> zone : zones) copy.add(new Pair<UDACapable, Rectangle>(zone.a, zone.b));
        return copy;
    }

    public void add(UDACapable udaCapable, Rectangle zoneRec) {
        zones.add(new Pair<UDACapable,Rectangle>(udaCapable, zoneRec));
    }

    public void clear() {
        zones.clear();
    }

    @Override
    public void load() {
       UDA.getInstance().loadView(this);
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }

    @Override
    public void generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
