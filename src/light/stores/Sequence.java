package light.stores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import light.general.ConsoleAddress;
import light.general.DataStore;
import light.output.OutputCapable;

public class Sequence extends AbstractStore implements OutputCapable {
    
    List<Cue> cues;

    public Sequence(ConsoleAddress baseAddress) {
        super(baseAddress);
        cues = new ArrayList<Cue>();
    }

    public void addAsNext(Cue cue) {
        ConsoleAddress address = new ConsoleAddress(Sequence.class, getAddress().getPrefix(), cues.size()+1);
        cue.setAddress(address);
        cues.add(cue);
    }

    public void add(Cue cue) {
        cues.add(cue);
    }

    public boolean hasCue(ConsoleAddress address) {
        return getCue(address)!=null;
    }

    public Cue getCue(ConsoleAddress address) {
        for (Cue cue : cues) {
            if (cue.getAddress().equals(address)) return cue;
        }
        return null;
    }

    public int getSize() {return cues.size();}

    private void sortCues() {
        Collections.sort(cues, new Comparator<Cue>() {
            public int compare(Cue a, Cue b){
              if (a.getAddress().getSuffix()<b.getAddress().getSuffix()) return -1;
              if (a.getAddress().getSuffix()>b.getAddress().getSuffix()) return 1;
              return 0;
            }
        });
    }

    @Override
    public DataStore getOutput() {
        sortCues(); //????? Should I
        return null;
    }
}
