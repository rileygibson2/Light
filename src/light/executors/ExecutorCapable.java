package light.executors;

import light.output.OutputCapable;

public interface ExecutorCapable extends OutputCapable {

    public void setMasterValue(int v);
    public void go();
    public void goBack();
    public void pause();
    public void on();
    public void off();
    public void flash();
    public void temp();
    public void toggle();
    public void swop();
}
