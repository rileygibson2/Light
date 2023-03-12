package light.stores;

import light.commands.Command;
import light.general.ConsoleAddress;
import light.general.DataStore;
import light.output.OutputCapable;

public class Cue extends AbstractStore implements OutputCapable {
    public enum CueTrigger {
        Go,
        Follow,
        TimeCode
    }
    
    //Cue data 
    private DataStore store;
    private DataStore storeReal; //Real/current values for this cue, used to implement fading
    
    //Cue attributes
    private String label;
    private double fadeTime;
    private Command command;
    private CueTrigger trigger;

    public Cue(ConsoleAddress address) {
        super(address);
    }

    public Cue(ConsoleAddress address, DataStore store) {
        super(address);
        this.store = store;
        if (store!=null) this.storeReal = store.getZeroedClone();
    }

    public void setData(DataStore store) {
        this.store = store;
        if (store!=null) this.storeReal = store.getZeroedClone();
    }

    public double getFadeTime() {return fadeTime;}
    public void setFadeTime(double fadeTime) {this.fadeTime = fadeTime;}

    public String getLabel() {return label;}
    public void setLabel(String label) {this.label = label;}

    public CueTrigger getCueTrigger() {return trigger;}
    public void setCueTrigger(CueTrigger trigger) {this.trigger = trigger;}

    public Command getCommand() {return command;}
    public void setCommand(Command c) {this.command = c;}

    public void go() {

    };

    public void release() {

    }

    @Override
    public DataStore getOutput() {
        throw new UnsupportedOperationException("Unimplemented method 'getOutput'");
    };
}
