package light.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import light.Programmer;
import light.fixtures.Attribute;
import light.fixtures.Fixture;
import light.general.DMXAddress;
import light.general.DataStore;
import light.guipackage.general.Pair;
import light.stores.Sequence;
import light.stores.effects.Effect;

public class Output {
    
    private static Output singleton;
    
    private List<OutputCapable> outputters;
    
    private Output() {
        outputters = new ArrayList<OutputCapable>();
    }
    
    public static Output getInstance() {
        if (singleton==null) singleton = new Output();
        return singleton;
    }
    
    public void register(OutputCapable outputter) {
        if (!outputters.contains(outputter)) {
            outputters.add(outputter);
        }
        Collections.sort(outputters, new OutputCapableComparator());
    }
    
    public void deregister(OutputCapable outputter) {
        outputters.remove(outputter);
        Collections.sort(outputters, new OutputCapableComparator());
    }

    public boolean isRegistered(OutputCapable toCheck) {return outputters.contains(toCheck);}
    
    public void deregisterAll() {outputters.clear();}
    
    public void generateOutput() {
        DataStore result = new DataStore();
        for (OutputCapable outputter : outputters) {
            result.combine(outputter.getOutput(), true);
        }
    }
    
    public void generateDMX(DataStore data) {
        Map<DMXAddress, List<Pair<DMXAddress, Integer>>> dmx = new LinkedHashMap<>();
        
        for (Fixture f : data.getFixtureSet()) {
            for (Map.Entry<Attribute, Double> v : data.getFixtureValues(f).entrySet()) {
                DMXAddress d = f.getAddressForAttribute(v.getKey());
                
                //Add to dmx at correct dmx address
                if (dmx.containsKey(d.getBaseUniverseAddress())) { //Universe exists in dmx
                    dmx.get(d.getBaseUniverseAddress()).add(new Pair<DMXAddress, Integer> (d, percToDMX(v.getValue())));
                }
                else { //Universe does not exist in dmx
                    List<Pair<DMXAddress, Integer>> u = new ArrayList<>();
                    u.add(new Pair<DMXAddress, Integer> (d, percToDMX(v.getValue())));
                    dmx.put(d.getBaseUniverseAddress(), u);
                }
            }
        }
        
        //Sort all universes
        for (Map.Entry<DMXAddress, List<Pair<DMXAddress, Integer>>> u : dmx.entrySet()) {
            Collections.sort(u.getValue(), new Comparator<Pair<DMXAddress, Integer>>() {
                @Override
                public int compare(Pair<DMXAddress, Integer> a, Pair<DMXAddress, Integer> b) {
                    return a.a.compareTo(b.a);
                }
            });
        }
    }

    public static int percToDMX(double p) {return (int) ((p/100)*255);}
}

class OutputCapableComparator implements Comparator<OutputCapable> {
    
    /**
    * Ranking
    * 
    * Highlight
    * Programmer
    * Sequences (in priority order)
    * Effects (in priority order)
    */
    
    @Override
    public int compare(OutputCapable first, OutputCapable second) {
        //Programmer
        if (first instanceof Programmer) return 1;
        if (second instanceof Programmer) return -1;
        
        //Sequence
        if (first instanceof Sequence&&!(second instanceof Sequence)) return 1;
        if (!(first instanceof Sequence)&&second instanceof Sequence) return -1;
        if (first instanceof Sequence&&second instanceof Sequence) {
            return ((Sequence) first).getPriority()-((Sequence) second).getPriority();
        }
        
        if (first instanceof Effect&&second instanceof Effect) {
            return ((Effect) first).getPriority()-((Effect) second).getPriority();
        }
        
        return 0;
    }
}
