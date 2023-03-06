package light;

import java.util.HashSet;
import java.util.Set;

import guipackage.gui.GUI;
import light.uda.UDA;
import light.uda.commandline.CommandLine;

public class Light {

    static Light singleton;

    private Set<UDA> udas;
    private UDA currentUDA;

    //Elements not tied to UDA
    CommandLine cL;

    private Light() {
        setup();
    }

    public static Light getInstance() {
        if (singleton==null) singleton = new Light();
        return singleton;
    }

    private void setup() {
        GUI.initialise(this, null);
        cL = new CommandLine();

        udas = new HashSet<UDA>();
        currentUDA = new UDA();
        udas.add(currentUDA);
    }

    public UDA getCurrentUDA() {return currentUDA;}

    public static void main(String args[]) {
        Light.getInstance();
    }
}
