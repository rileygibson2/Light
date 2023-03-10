package light;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import guipackage.gui.GUI;
import light.general.ConsoleAddress;
import light.stores.Group;
import light.stores.Preset;
import light.stores.Preset.PresetType;
import light.stores.Sequence;
import light.stores.View;
import light.stores.effects.Effect;
import light.uda.UDA;
import light.uda.commandline.CommandLine;

public class Light {

    static Light singleton;

    //Logic
    private Set<Fixture> fixtures;
    private Programmer programmer;


    //Pools
    private Map<PresetType, Pool<Preset>> presetPools;
    private Pool<Effect> effectPool;
    private Pool<View> viewPool;
    private Pool<Sequence> sequencePool;
    private Pool<Group> groupPool;

    private View currentView;

    //Elements
    CommandLine cL;

    private Light() {
        setup();
    }

    public static Light getInstance() {
        if (singleton==null) singleton = new Light();
        return singleton;
    }

    public Pool<Preset> getPresetPool(PresetType p) {
        return presetPools.get(p);
    }

    /**
     * Will not work for preset pools
     * @param scope
     * @return
     */
    public Pool<?> getPoolWithScope(Class<?> scope) {
        if (scope==Group.class) return groupPool;
        if (scope==Sequence.class) return sequencePool;
        if (scope==Effect.class) return effectPool;
        if (scope==View.class) return viewPool;
        return null;
    }

    public Fixture resolveAddressToFixture(ConsoleAddress address) {
        for (Fixture f : fixtures) {
            if (f.getAddress().equals(address)) return f;
        }
        return null;
    }

    public List<Fixture> resolveAddressesToFixtures(List<ConsoleAddress> addresses) {
        List<Fixture> fixtures = new ArrayList<>();

        for (Fixture f : fixtures) {
            if (addresses.contains(f.getAddress())) fixtures.add(f);
        }
        return fixtures;
    }

    private void setup() {
        GUI.initialise(this, null);
        cL = new CommandLine();
        programmer = Programmer.getInstance();

        fixtures = new HashSet<Fixture>();

        presetPools = new HashMap<PresetType, Pool<Preset>>();
        for (PresetType pT : PresetType.values()) {
            presetPools.put(pT, new Pool<Preset>(new ConsoleAddress(Preset.class, pT.ordinal(), 0)));
        }

        groupPool = new Pool<Group>(new ConsoleAddress(Group.class, 0, 0));
        sequencePool = new Pool<Sequence>(new ConsoleAddress(Sequence.class, 0, 0));
        effectPool = new Pool<Effect>(new ConsoleAddress(Effect.class, 0, 0));
        viewPool = new Pool<View>(new ConsoleAddress(View.class, 0, 0));

        currentView = new View(new UDA());
    }

    public static void main(String args[]) {
        Light.getInstance();
    }
}
