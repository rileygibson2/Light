package light.encoders;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import light.uda.UDACapable;
import light.uda.guiinterfaces.GUIInterface;

public class Encoders implements UDACapable {
    
    private static Encoders singleton;

    private GUIInterface gui;

    public enum Encoder {
        A, B, C, D;
    }

    private Map<Encoder, Double> encoderValues;
    private Set<Encoder> activeEncoders;

    EncoderCapable currentController;
    Deque<EncoderCapable> waitingControllers; //Used to store controllers that had control but lost it

    public enum EncoderDefaultCalculatorMacros {
        Off,
        Release,
        Remove,
        Default;
    }

    private Encoders() {
        encoderValues = new HashMap<Encoder, Double>();
        encoderValues.put(Encoder.A, 0d);
        encoderValues.put(Encoder.B, 0d);
        encoderValues.put(Encoder.C, 0d);
        encoderValues.put(Encoder.D, 0d);

        activeEncoders = new HashSet<Encoder>();
        waitingControllers = new ArrayDeque<EncoderCapable>();
    }

    public static Encoders getInstance() {
        if (singleton==null) singleton = new Encoders();
        return singleton;
    }

    public void setGUI(GUIInterface gui) {this.gui = gui;}

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

    public void clearValues() {
        encoderValues.clear();
        activeEncoders.clear();
        gui.update();
    }

    public void clearAll() {
        currentController = null;
        waitingControllers.clear();
        encoderValues.clear();
        activeEncoders.clear();
        gui.update();
    }

    public void set(Encoder encoder, double value) {
        if (currentController==null||!activeEncoders.contains(encoder)) return;
        encoderValues.put(encoder, value);
        currentController.encoderUpdated(encoder);
    }

    public double getEncoderValue(Encoder encoder) {
        return encoderValues.get(encoder);
    }

    public String getEncoderTitle(Encoder encoder) {
        if (encoder==null||currentController==null) return null;
        return currentController.getEncoderTitle(encoder);
    }

    public String getEncoderCalculatorTitle(Encoder encoder) {
        if (encoder==null||currentController==null) return null;
        return currentController.getEncoderCalculatorTitle(encoder);
    }

    public String getEncoderValueText(Encoder encoder) {
        if (encoder==null||currentController==null) return null;
        return currentController.getEncoderValueText(encoder);
    }

    public Map<String, Double> getEncoderCalculatorMacros(Encoder encoder) {
        if (encoder==null||currentController==null) return null;
        return currentController.getEncoderCalculatorMacros(encoder);
    }

    public void activate(Encoder encoder) {
        if (currentController!=null) activeEncoders.add(encoder);
    }

    public void deactivate(Encoder encoder) {
        if (currentController!=null) activeEncoders.remove(encoder);
    }

    public void setActivation(Encoder encoder, boolean activation) {
        if (currentController==null) return;
        if (activation) activate(encoder);
        else deactivate(encoder);
    }

    public void activateAll() {
        if (currentController==null) return;
        activeEncoders.add(Encoder.A);
        activeEncoders.add(Encoder.B);
        activeEncoders.add(Encoder.C);
        activeEncoders.add(Encoder.D);
    }

    public void deactivateAll() {
        if (currentController!=null) activeEncoders.clear();
        gui.update();
    }

    /**
     * Called by an element when it wants the encoder's to recheck and update their values
     */
    public void update() {
        if (currentController!=null) {
            for (Encoder e : Encoder.values()) {
                setActivation(e, currentController.checkEncoderActivation(e));
                set(e, currentController.checkEncoderValue(e));
            }
        }
        if (gui!=null) gui.update();
    }
}
