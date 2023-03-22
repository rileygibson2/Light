package light.stores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import light.executors.ExecutorCapable;
import light.general.ConsoleAddress;
import light.general.DataStore;

public class Sequence extends AbstractStore implements ExecutorCapable {
    
    List<Cue> cues;
    Cue currentCue;

    DataStore trackingStore;

    public enum CueMergeMode {
        IsolateChange,
        TrackChange;
    }

    int priority;
    
    public Sequence(ConsoleAddress baseAddress) {
        super(baseAddress);
        cues = new ArrayList<Cue>();
        currentCue = null;
        trackingStore = new DataStore();
    }
    
    public void addNext(Cue cue) {
        ConsoleAddress address = new ConsoleAddress(Sequence.class, getAddress().getPrefix(), cues.size()+1);
        cue.setAddress(address);
        cues.add(cue);
        sortCues();
    }
    
    public void add(Cue cue) {
        cues.add(cue);
        sortCues();
    }

    public void remove(Cue toRemove) {
        cues.remove(toRemove);
        sortCues();
    }

    public void remove(ConsoleAddress toRemove) {
        for (Cue cue : cues) {
            if (cue.getAddress().equals(toRemove)) cues.remove(cue);
        }
        sortCues();
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
    
    public int getPriority() {return priority;}
    
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
        if (currentCue==null) return null;
        return currentCue.getOutput().clone().combine(trackingStore, false);
    }
    
    @Override
    public boolean registrationCheck() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registrationCheck'");
    }
    
    @Override
    public void go() {
        /*
        * Find current cues position in list
        * Need to recheck position instead of just storing an index to position
        * of current cue because cuelist may have changed since current cue was entered
        */
        for (int i=0; i<cues.size(); i++) {
            if (cues.get(i)==currentCue) {
                if (i+1<cues.size()) currentCue = cues.get(i+1);
                else currentCue = cues.get(0);
                
                if (currentCue!=null) {
                    /**
                     * Tracking is regenerated each time an action is called instead of the same
                     * store being added to each time an action is called. This is again because
                     * the order of cues is ephemeral and cues could have been inserted before the
                     * new cue and their values need to be tracked so better to recalculate each time 
                     */
                    trackingStore = generateTrackingToCue(currentCue);
                    //currentCue.go();
                }
            }
        }
    }
    
    @Override
    public void goBack() {
        /*
        * Find current cues position in list
        * Need to recheck position instead of just storing an index to position
        * of current cue because cuelist may have changed since current cue was entered
        */
        for (int i=0; i<cues.size(); i++) {
            if (cues.get(i)==currentCue) {
                if (i-1>0) currentCue = cues.get(i-1);
                else currentCue = cues.get(cues.size()-1);
                if (currentCue!=null) {
                    //Re generate tracking
                    trackingStore = generateTrackingToCue(currentCue);
                    //currentCue.go();
                }
            }
        }
    }

    public DataStore generateTrackingToCue(Cue endCue) {
        DataStore tracking = new DataStore();
        for (Cue cue : cues) {
            if (cue==endCue) return tracking;
            tracking.combine(cue.getStore(), true);
        }
        return null; //end cue not found
    }
    
    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }
    
    @Override
    public void flash() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'flash'");
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
	public void setMasterValue(int v) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'setMasterValue'");
	}

	@Override
	public void on() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'on'");
	}

	@Override
	public void off() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'off'");
	}

	@Override
	public void temp() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'temp'");
	}

	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'toggle'");
	}

	@Override
	public void swop() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'swop'");
	}
}
