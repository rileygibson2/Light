package light.stores;

import light.commands.Command;
import light.general.ConsoleAddress;
import light.output.OutputCapable;

public class Cue implements OutputCapable {
    public enum CueTrigger {
        Go,
        Follow,
        TimeCode
    }
    
    private ConsoleAddress address;
    private DataStore data;
    private DataStore dataReal; //Real/current values for this cue, used to implement fading
    
    private String label;
    private double fadeTime;
    private Command command;
    private CueTrigger trigger;

    public Cue(ConsoleAddress address, DataStore data) {
        this.address = address;
        this.data = data;
        if (data!=null) this.dataReal = data.getZeroedClone();
    }

    public void addData(DataStore data) {
        this.data = data;
        if (data!=null) this.dataReal = data.getZeroedClone();
    }

    public ConsoleAddress getAddress() {return address;}

    public void go() {

    };

    public void release() {

    }

    @Override
    public DataStore getOutput() {
        throw new UnsupportedOperationException("Unimplemented method 'getOutput'");
    };
}
