package light.stores;

import java.util.ArrayList;
import java.util.List;

import light.general.ConsoleAddress;

public class Sequence {
    
    ConsoleAddress address;
    List<Cue> cues;

    public Sequence(ConsoleAddress baseAddress) {
        this.address = baseAddress;
        cues = new ArrayList<Cue>();
    }

    public void addCue(DataStore store) {
        ConsoleAddress a = new ConsoleAddress(address.prefix, cues.size()+1);
        cues.add(new Cue(address, store));
    }

    public int getSize() {return cues.size();}

    public Cue getCue(ConsoleAddress address) {
        for (Cue cue : cues) {
            if (cue.getAddress().equals(address)) return cue;
        }
        return null;
    }
}
