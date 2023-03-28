package light.executors;

import light.general.Addressable;
import light.general.ConsoleAddress;
import light.persistency.PersistencyCapable;

/**
 * An executor is just a method of controlling a store
 * It simply provides control mechanisims and keeps track of state, all data manipulation and output
 * formatting is done by the ActionableStore assigned to this executor.
 * This way the control mechanisim and state is abstracted away from the information store but the store
 * retains the ability to choose how it reacts to the controls.
 * 
 * As such the executor cannot be output capable but instead is assigned an element that is OutputCapable.
 * As such an interface is provided that all elements that may be assigned to an executor must implement that
 * ensures that the all assignable elements can be controlled by the same mechanisms.
 */
public class Executor extends Addressable implements PersistencyCapable {

    private ExecutorCapable assignment;

    private double masterValue;

    private double assertTime;
    private double releaseTime;

    public enum ExecType {
        Master,
        Go,
        GoBack,
        Pause,
        On,
        Off,
        Flash,
        FlashOn,
        FlashOff,
        Temp,
        Toggle,
        Swop,
        HalfSpeed,
        HalfRate,
        DoubleSpeed,
        DoubleRate;
    };

    public Executor(ConsoleAddress address) {
		super(address);
	}

    public void assign(ExecutorCapable assignment) {
        this.assignment = assignment;
    }

    public boolean isAssigned() {return assignment!=null;}

    public ExecutorCapable getAssignedElement() {return assignment;}

    public void clearAssignment() {assignment = null;}

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