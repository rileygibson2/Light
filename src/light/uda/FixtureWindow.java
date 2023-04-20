package light.uda;

import light.uda.guiinterfaces.FixtureWindowGUIInterface;
import light.uda.guiinterfaces.GUIInterface;

public class FixtureWindow implements UDACapable {

    private FixtureWindowGUIInterface gui;

    @Override
    public void setGUI(GUIInterface gui) {
        if (!(gui instanceof FixtureWindowGUIInterface)) return;
        this.gui = (FixtureWindowGUIInterface) gui;
    }

    public FixtureWindowGUIInterface getGUI() {return gui;}
}
