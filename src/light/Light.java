package light;

import java.util.HashSet;
import java.util.Set;

import guipackage.gui.GUI;
import light.layouts.DefaultLayout;
import light.layouts.Layout;

public class Light {

    static Light singleton;

    private Set<Layout> layouts;
    private Layout currentLayout;

    private Light() {
        setup();
    }

    public static Light getInstance() {
        if (singleton==null) singleton = new Light();
        return singleton;
    }

    private void setup() {
        GUI.initialise(this, null);
        layouts = new HashSet<Layout>();

        currentLayout = new DefaultLayout();
        layouts.add(currentLayout);
    }

    public Layout getCurrentLayout() {return currentLayout;}

    public static void main(String args[]) {
        Light.getInstance();
    }
}
