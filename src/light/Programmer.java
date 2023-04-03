package light;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import light.encoders.EncoderCapable;
import light.encoders.Encoders;
import light.encoders.Encoders.Encoder;
import light.general.DataStore;
import light.stores.effects.Effect;

public class Programmer extends DataStore implements EncoderCapable {
    
    private static Programmer singleton;

    private List<Fixture> selectedFixtures;
    private Set<Effect> activeEffects;

    private Programmer() {
        super();
        selectedFixtures = new ArrayList<Fixture>();
        activeEffects = new HashSet<Effect>();
    }

    public static Programmer getInstance() {
        if (singleton==null) singleton = new Programmer();
        return singleton;
    }

    public void selectFixture(Fixture f) {
        selectedFixtures.add(f);
        Encoders.getInstance().aquireEncoders(this);
    }
    public void selectFixtures(List<Fixture> f) {
        selectedFixtures.addAll(f);
        Encoders.getInstance().aquireEncoders(this);
    }

    public void deselectFixture(Fixture f) {
        selectedFixtures.remove(f);
        if (selectedFixtures.isEmpty()) Encoders.getInstance().clearEncoders();
    }
    public void deselectFixtures(List<Fixture> f) {
        selectedFixtures.removeAll(f);
        if (selectedFixtures.isEmpty()) Encoders.getInstance().clearEncoders();
    }

    public List<Fixture> getSelectedFixtures() {return selectedFixtures;}

    public void clearSelectedFixtures() {
        selectedFixtures.clear();
        Encoders.getInstance().clearEncoders();
    }

    public void addEffect(Effect e) {activeEffects.add(e);}

    public void removeEffect(Effect e) {activeEffects.remove(e);}

    @Override
    public String getEncoderTitle(Encoder encoder) {
        switch (encoder) {
            case A: return "Intensity";
            case B: return "";
            case C: return "";
            case D: return "Strobe";
            default: return null;
        }
    }

    @Override
    public double getInitialEncoderValue(Encoder encoder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getInitialEncoderValue'");
    }

    @Override
    public void encoderUpdated(Encoder encoder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'encoderUpdated'");
    }
}
