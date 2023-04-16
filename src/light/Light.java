package light;

import java.util.HashMap;
import java.util.Map;

import light.commands.Clear;
import light.commands.Copy;
import light.commands.Delete;
import light.commands.Edit;
import light.commands.Label;
import light.commands.Modulate;
import light.commands.Move;
import light.commands.Store;
import light.commands.commandline.CommandLine;
import light.encoders.Encoders;
import light.executors.Executor;
import light.fixtures.Fixture;
import light.fixtures.PatchManager;
import light.fixtures.profile.Profile;
import light.fixtures.profile.ProfileManager;
import light.fixtures.profile.ProfileParseException;
import light.general.Addressable;
import light.general.ConsoleAddress;
import light.guipackage.cli.CLI;
import light.guipackage.general.Rectangle;
import light.guipackage.gui.GUI;
import light.stores.AbstractStore;
import light.stores.Group;
import light.stores.Preset;
import light.stores.Preset.PresetType;
import light.stores.Sequence;
import light.stores.View;
import light.stores.effects.Effect;
import light.uda.UDA;

public class Light {

    static Light singleton;

    //Pools
    private Map<PresetType, Pool<Preset>> presetPools;
    private Pool<Effect> effectPool;
    private Pool<View> viewPool;
    private Pool<Sequence> sequencePool;
    private Pool<Group> groupPool;
    private Pool<Executor> executorPool;

    private View currentView;

    private Light() {
        setup();
    }

    public static Light getInstance() {
        if (singleton==null) singleton = new Light();
        return singleton;
    }

    public Pool<Preset> getPresetPool(PresetType p) {
        if (p==null) return null;
        return presetPools.get(p);
    }

    public Pool<Group> getGroupPool() {return groupPool;}
    public Pool<Sequence> getSequencePool() {return sequencePool;}
    public Pool<Effect> getEffectPool() {return effectPool;}
    public Pool<View> getViewPool() {return viewPool;}
    public Pool<Executor> getExecutorPool() {return executorPool;}

    public Pool<?> getPool(ConsoleAddress address) {
        if (address.matchesScope(Group.class)) return groupPool;
        if (address.matchesScope(Sequence.class)) return sequencePool;
        if (address.matchesScope(Effect.class)) return effectPool;
        if (address.matchesScope(View.class)) return viewPool;
        if (address.matchesScope(Executor.class)) return executorPool;
        if (address.matchesScope(Preset.class)) {
            return getPresetPool(Preset.getTypeFromAddress(address));
        }

        return null;
    }

    /**
     * Will not work for preset pools
     * @param scope
     * @return
     */
    public Pool<? extends AbstractStore> getPoolWithScope(Class<? extends Addressable> scope) {
        if (scope==Group.class) return groupPool;
        if (scope==Sequence.class) return sequencePool;
        if (scope==Effect.class) return effectPool;
        if (scope==View.class) return viewPool;
        return null;
    }

    /**
     * Resolves an address to a real object if it can be
     * 
     * @param address
     * @return
     */
    public Addressable resolveAddress(ConsoleAddress address) {
        if (address.matchesScope(Fixture.class)) return PatchManager.getInstance().getFixture(address);

        if (address.matchesScope(Preset.class)) {
            PresetType t = Preset.getTypeFromAddress(address);
            if (t==null) return null;
            Pool<Preset> pool = getPresetPool(t);
            if (pool==null) return null;

            if (address.equals(pool.getAddress())) return pool;
            return getPresetPool(t).get(address);
        }

        Pool<? extends Addressable> pool = getPoolWithScope(address.getScope());
        if (pool==null) return null;
        
        if (address.equals(pool.getAddress())) return pool;
        return pool.get(address);
    }

    private void setup() {
        GUI.initialise(this, null); //Passing null forces full screen
        
        //Insantiate logic components
        ProfileManager.getInstance();
        PatchManager.getInstance();
        Encoders.getInstance();
        Programmer.getInstance();

        //Make pools
        presetPools = new HashMap<PresetType, Pool<Preset>>();
        for (PresetType pT : PresetType.values()) {
            presetPools.put(pT, new Pool<Preset>(new ConsoleAddress(Preset.class, pT.ordinal()+1, 0)));
        }

        groupPool = new Pool<Group>(new ConsoleAddress(Group.class, 0, 0));
        sequencePool = new Pool<Sequence>(new ConsoleAddress(Sequence.class, 0, 0));
        effectPool = new Pool<Effect>(new ConsoleAddress(Effect.class, 0, 0));
        viewPool = new Pool<View>(new ConsoleAddress(View.class, 0, 0));
        executorPool = new Pool<Executor>(new ConsoleAddress(Executor.class, 0, 0));

        //Setup command line and add commands
        CommandLine commandLine = CommandLine.getInstance();
        commandLine.registerCommand(Store.class);
        commandLine.registerCommand(Move.class);
        commandLine.registerCommand(Label.class);
        commandLine.registerCommand(Delete.class);
        commandLine.registerCommand(Edit.class);
        commandLine.registerCommand(Clear.class);
        commandLine.registerCommand(Copy.class);
        commandLine.registerCommand(Modulate.class, "at");

        //Setup view
        currentView = new View(new UDA());

        mock();
    }

    private void mock() {
        Preset preset = new Preset(PresetType.COLOR.getBaseAddress().setSuffix(2), PresetType.COLOR);
        getPresetPool(PresetType.COLOR).add(preset);

        Profile profile = null;
        try {
            profile = ProfileManager.getInstance().parseProfile("profiletest.xml");
        }
        catch (ProfileParseException e) {CLI.error(e.toString());}

        Fixture fixture = new Fixture(new ConsoleAddress(Fixture.class, 0, 1), profile);
        PatchManager.getInstance().addFixture(fixture);

        Programmer.getInstance().selectFixture(fixture);

        CLI.debug(Programmer.getInstance().getSelectedFixtures());

        currentView.getUDA().createZone(Encoders.class, new Rectangle(0, 7, 10, 2));
    }

    public static void main(String args[]) {
        Light.getInstance();
    }
}
