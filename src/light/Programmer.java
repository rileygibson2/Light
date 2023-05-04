package light;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import light.encoders.EncoderCapable;
import light.encoders.Encoders;
import light.encoders.Encoders.Encoder;
import light.fixtures.FeatureGroup;
import light.fixtures.Fixture;
import light.fixtures.PatchManager;
import light.fixtures.profile.ProfileChannel;
import light.fixtures.profile.ProfileChannelMacro;
import light.fixtures.profile.ProfileWheelSlot;
import light.general.ConsoleAddress;
import light.general.DataStore;
import light.guipackage.cli.CLI;
import light.guipackage.general.Pair;
import light.output.Output;
import light.output.OutputCapable;
import light.stores.effects.Effect;
import light.uda.UDA;
import light.uda.guiinterfaces.FixtureWindowGUIInterface;
import light.uda.guiinterfaces.GUIInterface;

public class Programmer extends DataStore implements OutputCapable, EncoderCapable {
    
    private static Programmer singleton;
    
    private List<Fixture> selectedFixtures;
    private Set<Effect> activeEffects;
    
    private Programmer() {
        super();
        selectedFixtures = new ArrayList<Fixture>();
        activeEffects = new HashSet<Effect>();
        Output.getInstance().register(this);
        Encoders.getInstance().aquireEncoders(this);
        Encoders.getInstance().setPage(0, this);

        setUpdateAction((s) -> {
            CLI.debug("Programmer updated "+s);
            updateFixturesGUI(s);
        });
    }
    
    public static Programmer getInstance() {
        if (singleton==null) singleton = new Programmer();
        return singleton;
    }
    
    public void select(Fixture fixture) {
        if (fixture!=null) selectedFixtures.add(fixture);
        Encoders.getInstance().update();
        //updateFixtureGUI(Collections.singleton(fixture));
        updateFixturesGUI(Collections.singleton(fixture));
    }
    
    public void select(Collection<Fixture> fixtures) {
        if (fixtures!=null) selectedFixtures.addAll(fixtures);
        Encoders.getInstance().update();
        updateFixturesGUI(fixtures);
    }
    
    public void select(ConsoleAddress address) {
        if (address==null||!address.matchesScope(Fixture.class)) return;
        Fixture fixture = PatchManager.getInstance().getFixture(address);
        if (fixture!=null)  selectedFixtures.add(fixture);
        Encoders.getInstance().update();
        updateFixturesGUI(selectedFixtures);
    }
    
    public void deselect(Fixture fixture) {
        selectedFixtures.remove(fixture);
        Encoders.getInstance().update();
        updateFixturesGUI(Collections.singleton(fixture));
    }
    
    public void deselect(List<Fixture> f) {
        selectedFixtures.removeAll(f);
        Encoders.getInstance().update();
        updateFixturesGUI(f);
    }
    
    public List<Fixture> getSelected() {return selectedFixtures;}

    public boolean hasSelectedFixtures() {return !selectedFixtures.isEmpty();}

    public boolean isSelected(Fixture fixture) {return selectedFixtures.contains(fixture);}
    
    public void clearSelected() {
        selectedFixtures.clear();
        Encoders.getInstance().update();
        updateFixturesGUI(PatchManager.getInstance().allFixtureList()); //Update all fixtures in gui
    }
    
    public void addEffect(Effect e) {activeEffects.add(e);}
    
    public void removeEffect(Effect e) {activeEffects.remove(e);}
    
    private void updateFixturesGUI(Collection<Fixture> changed) {
        for (GUIInterface gui : UDA.getInstance().getGUIInterfacesOfClass(FixtureWindowGUIInterface.class)) {
            ((FixtureWindowGUIInterface) gui).updateFixtures(changed);
        }
    }
    
    private List<ProfileChannel> getChannelsForEncoderPage() {
        int page = Encoders.getInstance().getCurrentPage();
        if (page==-1||page>=FeatureGroup.values().length||!hasSelectedFixtures()) return null;
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
        ProfileChannel channel = getChannelForEncoder(encoder);
        if (channel==null) return;
        set(selectedFixtures.get(0), channel.getAttribute(), value, true);
    }
    
    @Override
    public double getEncoderValue(Encoder encoder) {
        ProfileChannel channel = getChannelForEncoder(encoder);
        if (channel==null) return -1;
        double value = get(selectedFixtures.get(0), channel.getAttribute());
        if (value!=DataStore.NONE) return value;
        
        //Programmer doesn't contain value but encoder is valid so need to find min (off) value
        //TODO need some sort of gui color differential to show a zeroed value not present in programmer vs zero present in store
        return channel.getDefaultValue();
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
            results.put(macro.getName(), new Pair<Double, String>(macro.getFunction().dmxToValue(macro.getMidDMX()), fileName));
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

    @Override
    public DataStore getOutput() {
        return this;
    }

    @Override
    public boolean outputRegistrationCheck() {return true;}
}
