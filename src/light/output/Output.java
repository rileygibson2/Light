package light.output;

import java.util.HashSet;
import java.util.Set;

public class Output {
    
    private static Output singleton;

    private Set<OutputCapable> outputters;

    private Output() {
        outputters = new HashSet<OutputCapable>();
    }

    public static Output getInstance() {
        if (singleton==null) singleton = new Output();
        return singleton;
    }

    public void register(OutputCapable outputter) {
        if (!outputters.contains(outputter)) outputters.add(outputter);
    }

    public void deregister(OutputCapable outputter) {outputters.remove(outputter);}

    public void deregisterAll() {outputters.clear();}
}
