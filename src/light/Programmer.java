package light;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import light.encoders.EncoderCapable;
import light.encoders.Encoders;
import light.encoders.Encoders.Encoder;
import light.fixtures.Attribute;
import light.fixtures.Fixture;
import light.fixtures.PatchManager;
import light.fixtures.profile.ProfileChannel;
import light.fixtures.profile.ProfileChannelMacro;
import light.general.ConsoleAddress;
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
        Encoders.getInstance().aquireEncoders(this);
    }
    
    public static Programmer getInstance() {
        if (singleton==null) singleton = new Programmer();
        return singleton;
    }
    
    public void selectFixture(Fixture fixture) {
        if (fixture!=null) selectedFixtures.add(fixture);
    }
    
    public void selectFixtures(Collection<Fixture> fixtures) {
        if (fixtures!=null) selectedFixtures.addAll(fixtures);
    }
    
    public void selectFixture(ConsoleAddress address) {
        if (address==null||!address.matchesScope(Fixture.class)) return;
        Fixture fixture = PatchManager.getInstance().getFixture(address);
        if (fixture!=null)  selectedFixtures.add(fixture);
    }
    
    public void deselectFixture(Fixture f) {
        selectedFixtures.remove(f);
        if (selectedFixtures.isEmpty()) Encoders.getInstance().update();
    }
    public void deselectFixtures(List<Fixture> f) {
        selectedFixtures.removeAll(f);
        if (selectedFixtures.isEmpty()) Encoders.getInstance().update();
    }
    
    public List<Fixture> getSelectedFixtures() {return selectedFixtures;}
    
    public void clearSelectedFixtures() {
        selectedFixtures.clear();
        Encoders.getInstance().update();
    }
    
    public void addEffect(Effect e) {activeEffects.add(e);}
    
    public void removeEffect(Effect e) {activeEffects.remove(e);}
    
    @Override
    public String getEncoderTitle(Encoder encoder) {
        ProfileChannel channel =  selectedFixtures.get(0).getProfile().getChannelWithAttribute(Attribute.DIM);
        switch (encoder) {
            case A: return channel.getAttributeUserName();
            case B: return "";
            case C: return "";
            case D: return "";
            default: return null;
        }
    }

    @Override
    public String getEncoderCalculatorTitle(Encoder encoder) {
        ProfileChannel channel =  selectedFixtures.get(0).getProfile().getChannelWithAttribute(Attribute.DIM);
        return channel.getAttributeUserName()+" ["+channel.getMinValue()+" .. "+channel.getMaxValue()+"]";
    }
    
    @Override
    public String getEncoderValueText(Encoder encoder) {
        return Encoders.getInstance().getEncoderValue(encoder)+" open";
    }
    
    @Override
    public void encoderUpdated(Encoder encoder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'encoderUpdated'");
    }
    
    @Override
    public double checkEncoderValue(Encoder encoder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkEncoderValue'");
    }
    
    @Override
    public boolean checkEncoderActivation(Encoder encoder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkEncoderActivation'");
    }

    @Override
    public Map<String, Double> getEncoderCalculatorMacros(Encoder encoder) {
        Map<String, Double> results = new LinkedHashMap<>();
        ProfileChannel channel =  selectedFixtures.get(0).getProfile().getChannelWithAttribute(Attribute.DIM);

        for (ProfileChannelMacro macro : channel.getAllMacros()) {
            results.put(macro.getName(), macro.getMidDMX());
        }
        return results;
    }
}
