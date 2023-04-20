package light.encoders;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import light.guipackage.general.Pair;
import light.uda.UDACapable;
import light.uda.guiinterfaces.EncodersGUIInterface;
import light.uda.guiinterfaces.GUIInterface;

public class Encoders implements UDACapable {
    
    private static Encoders singleton;

    public enum Encoder {
        A, B, C, D;
    }

    private EncodersGUIInterface gui;

    private int page;

    EncoderCapable currentController;
    Deque<EncoderCapable> waitingControllers; //Used to store controllers that had control but lost it

    public enum EncoderDefaultCalculatorMacros {
        Off,
        Release,
        Remove,
        Default;
    }

    private Encoders() {
        waitingControllers = new ArrayDeque<EncoderCapable>();
        page = -1;
    }

    public static Encoders getInstance() {
        if (singleton==null) singleton = new Encoders();
        return singleton;
    }

    @Override
    public void setGUI(GUIInterface gui) {
        if (!(gui instanceof EncodersGUIInterface)) return;
        this.gui = (EncodersGUIInterface) gui;
    }

    public void aquireEncoders(EncoderCapable controller) {
        if (currentController!=null) { //Add to holding stack
            if (waitingControllers.contains(controller)) waitingControllers.remove(controller);
            waitingControllers.push(controller);
        }
        this.currentController = controller;
    }

    public void releaseEncoders(EncoderCapable controller) {
        if (currentController==controller) {
            if (!waitingControllers.isEmpty()) currentController = waitingControllers.pop(); //Swap out with last in holding stack
            else currentController = null; //No other controller is waiting
        }
        else if (waitingControllers.contains(controller)) waitingControllers.remove(controller);
    }

    public EncoderCapable getController() {return currentController;}

    public void clearAll() {
        currentController = null;
        waitingControllers.clear();
        gui.update();
    }

    public void set(Encoder encoder, double value) {
        if (currentController==null||!currentController.getEncoderActivation(encoder)) return;
        currentController.encoderChanged(encoder, value);
        if (gui!=null) gui.updateEncoders();
    }

    public double getEncoderValue(Encoder encoder) {
        return !getEncoderActivation(encoder) ? -1 : currentController.getEncoderValue(encoder);
    }

    public String getEncoderTitle(Encoder encoder) {
        return !getEncoderActivation(encoder) ? "" : currentController.getEncoderTitle(encoder);
    }

    public String getEncoderCalculatorTitle(Encoder encoder) {
        if (encoder==null||currentController==null||!currentController.getEncoderActivation(encoder)) return "";
        return !getEncoderActivation(encoder) ? "" : currentController.getEncoderCalculatorTitle(encoder);
    }

    public String getEncoderValueText(Encoder encoder) {
        return !getEncoderActivation(encoder) ? "" : currentController.getEncoderValueText(encoder);
    }

    public Map<String, Pair<Double, String>> getEncoderCalculatorMacros(Encoder encoder) {
        return !getEncoderActivation(encoder) ? null : currentController.getEncoderCalculatorMacros(encoder);
    }

    public boolean getEncoderActivation(Encoder encoder) {
        return (encoder==null||currentController==null) ? false : currentController.getEncoderActivation(encoder);
    }

    public List<String> getAllPageNames() {
        if (currentController==null) return null;
        return currentController.getAllEncoderPageNames();
    }

    public int getCurrentPage() {
        if (currentController==null) return -1;
        return page;
    }

    public void setPage(int page, Object caller) {
        //Ensure caller has permission to change page
        if (!(caller.equals(currentController)||caller instanceof EncodersGUIInterface)) return;
        if (page<0||page>=currentController.getNumEncoderPages()) return;
        this.page = page;

        if (gui!=null) {
            gui.updatePages();
            gui.updateEncoders();
        }
    }

    public int getEncoderIndex(Encoder encoder) {
        if (encoder==null) return -1;
        int i = 0;
        for (Encoder e : Encoder.values()) {
            if (e==encoder) return i;
            i++;
        }
        return -1;
    }

    /**
     * Prompts Encoders to recheck all data and update gui
     */
    public void update() {
        if (gui!=null) {
            gui.updatePages();
            gui.updateEncoders();
        }
    }
}
