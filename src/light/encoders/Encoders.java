package light.encoders;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Encoders {
    
    private static Encoders singleton;

    public enum Encoder {
        A, B, C, D;
    }

    EncoderCapable controller;

    private Map<Encoder, Integer> encoderValues;
    private Set<Encoder> activeEncoders;

    private Encoders() {
        encoderValues = new HashMap<Encoder, Integer>();
        encoderValues.put(Encoder.A, 0);
        encoderValues.put(Encoder.B, 0);
        encoderValues.put(Encoder.C, 0);
        encoderValues.put(Encoder.D, 0);

        activeEncoders = new HashSet<Encoder>();
    }

    public static Encoders getInstance() {
        if (singleton==null) singleton = new Encoders();
        return singleton;
    }

    public void aquireEncoders(EncoderCapable controller) {
        this.controller = controller;
    }

    public EncoderCapable getController() {return controller;}

    public void clearEncoders() {
        controller = null;
        encoderValues.clear();;
        activeEncoders.clear();
    }

    public void setEncoderValue(Encoder encoder, int value) {
        if (controller==null||!activeEncoders.contains(encoder)) return;
        encoderValues.put(encoder, value);
        controller.encoderUpdated(encoder);
    }

    public int getEncoderValue(Encoder encoder) {
        return encoderValues.get(encoder);
    }

    public void activateAllEncoders() {
        if (controller==null) return;
        activeEncoders.add(Encoder.A);
        activeEncoders.add(Encoder.B);
        activeEncoders.add(Encoder.C);
        activeEncoders.add(Encoder.D);
    }

    public void activateEncoder(Encoder encoder) {
        if (controller!=null) activeEncoders.add(encoder);
    }

    public void deactivateEncoder(Encoder encoder) {
        activeEncoders.remove(encoder);
    }
}
