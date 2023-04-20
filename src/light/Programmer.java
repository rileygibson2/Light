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
import light.fixtures.FeatureGroup;
import light.fixtures.Fixture;
import light.fixtures.PatchManager;
import light.fixtures.profile.ProfileChannel;
import light.fixtures.profile.ProfileChannelMacro;
import light.fixtures.profile.ProfileWheelSlot;
import light.general.ConsoleAddress;
import light.general.DataStore;
import light.guipackage.general.Pair;
import light.stores.effects.Effect;
import light.uda.FixtureWindow;

public class Programmer extends DataStore implements EncoderCapable {
    
    private static Programmer singleton;
    
    private List<Fixture> selectedFixtures;
    private Set<Effect> activeEffects;
    
    private Programmer() {
        super();
        selectedFixtures = new ArrayList<Fixture>();
        activeEffects = new HashSet<Effect>();
        Encoders.getInstance().aquireEncoders(this);
        Encoders.getInstance().setPage(0, this);
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
        updateFixturesGUI(fixtures);
    }
    
    public void selectFixture(ConsoleAddress address) {
        if (address==null||!address.matchesScope(Fixture.class)) return;
        Fixture fixture = PatchManager.getInstance().getFixture(address);
        if (fixture!=null)  selectedFixtures.add(fixture);
        updateFixturesGUI(selectedFixtures);
    }
    
    public void deselectFixture(Fixture f) {
        selectedFixtures.remove(f);
        if (selectedFixtures.isEmpty()) Encoders.getInstance().update();
        updateFixtureGUI(f);
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

    @Override
    public void set(Fixture fixture, Map<Attribute, Double> attributes, boolean overwritePriority) {
        super.set(fixture, attributes, overwritePriority);
        updateFixtureGUI(fixture);
    }

    @Override
    public void set(Fixture fixture, Attribute attribute, Double value, boolean overwritePriority) {
        super.set(fixture, attribute, value, overwritePriority);
        updateFixtureGUI(fixture);
    }
    
    public void addEffect(Effect e) {activeEffects.add(e);}
    
    public void removeEffect(Effect e) {activeEffects.remove(e);}
    
    private void updateFixtureGUI(Fixture changed) {
        List<Fixture> fixture = new ArrayList<>();
        fixture.add(changed);
        updateFixturesGUI(fixture);
    }
    
    private void updateFixturesGUI(Collection<Fixture> changed) {
        FixtureWindow fWindow = (FixtureWindow) Light.getInstance().getCurrentView().getUDA().getUDAElementForClass(FixtureWindow.class);
        if (fWindow!=null) fWindow.getGUI().updateFixtures(changed);
    }
    
    private List<ProfileChannel> getChannelsForEncoderPage() {
        int page = Encoders.getInstance().getCurrentPage();
        if (page==-1||page>=FeatureGroup.values().length) return null;
        return selectedFixtures.get(0).getProfile().getChannelsWithFeatureGroup(FeatureGroup.values()[page]);  
    }
    
    private ProfileChannel getChannelForEncoder(Encoder encoder) {
        List<ProfileChannel> channels = getChannelsForEncoderPage();
        if (channels==null) return null;
        int eI = Encoders.getInstance().getEncoderIndex(encoder);
        if (eI<channels.size()) return channels.get(eI);
        return null;
    }
    
    @Override
    public void encoderChanged(Encoder encoder, double value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'encoderChanged'");
    }
    
    @Override
    public double getEncoderValue(Encoder encoder) {
        ProfileChannel channel = getChannelForEncoder(encoder);
        if (channel==null) return -1;
        double value = get(selectedFixtures.get(0), channel.getAttribute());
        if (value!=DataStore.NONE) return value;
        
        //Programmer doesn't contain value but encoder is valid so need to find min (off) value
        //TODO need some sort of gui color differential to show a zeroed value not present in programmer vs zero present in store
        return channel.getMinValue();
    }
    
    @Override
    public String getEncoderValueText(Encoder encoder) {
        //selectedFixtures.get(0).getProfile().getChannelWithAttribute(getAttributeForEncoder(encoder)).getFunctionForValue(value).getMacroForValue(value).getName();
        return getEncoderValue(encoder)+" ";
    }
    
    @Override
    public String getEncoderTitle(Encoder encoder) {
        List<ProfileChannel> channels = getChannelsForEncoderPage();
        if (channels==null) return "";
        int eI = Encoders.getInstance().getEncoderIndex(encoder);
        if (eI<channels.size()) return channels.get(eI).getAttributeUserName();
        return "";
    }
    
    @Override
    public boolean getEncoderActivation(Encoder encoder) {
        List<ProfileChannel> channels = getChannelsForEncoderPage();
        if (channels==null) return false;
        return Encoders.getInstance().getEncoderIndex(encoder)<channels.size();
    }
    
    @Override
    public String getEncoderCalculatorTitle(Encoder encoder) {
        ProfileChannel channel = getChannelForEncoder(encoder);
        if (channel==null) return "";
        return channel.getAttributeUserName()+" ["+channel.getMinValue()+" .. "+channel.getMaxValue()+"]";
    }
    
    @Override
    public Map<String, Pair<Double, String>> getEncoderCalculatorMacros(Encoder encoder) {
        Map<String, Pair<Double, String>> results = new LinkedHashMap<>();
        ProfileChannel channel =  getChannelForEncoder(encoder);
        if (channel==null) return results;
        
        for (ProfileChannelMacro macro : channel.getAllMacros()) {
            String fileName = null;
            if (macro.hasSlotIndex()) {
                ProfileWheelSlot slot = macro.getFunction().getProfile().getSlot(macro);
                if (slot!=null) fileName = slot.getMediaFileName();
            }
            results.put(macro.getName(), new Pair<Double, String>(macro.getMidDMX(), fileName));
        }
        return results;
    }
    
    @Override
    public List<String> getAllEncoderPageNames() {
        List<String> result = new ArrayList<>();
        for (FeatureGroup fG : FeatureGroup.values()) result.add(fG.toString());
        return result;
    }
    
    @Override
    public int getNumEncoderPages() {
        return FeatureGroup.values().length;
    }
}
