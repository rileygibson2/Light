package light.stores;

import java.util.ArrayList;
import java.util.List;

import light.general.Addressable;
import light.general.ConsoleAddress;

public class Sequence extends Addressable {
    
    List<Cue> cues;

    public Sequence(ConsoleAddress baseAddress) {
        super(baseAddress);
        cues = new ArrayList<Cue>();
    }

    public void addCue(DataStore store) {
        ConsoleAddress a = new ConsoleAddress(Sequence.class, getAddress().getPrefix(), cues.size()+1);
        cues.add(new Cue(a, store));
    }

    public int getSize() {return cues.size();}

    public Cue getCue(ConsoleAddress address) {
        for (Cue cue : cues) {
            if (cue.getAddress().equals(address)) return cue;
        }
        return null;
    }
}
