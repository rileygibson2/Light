package light;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import light.commands.Command;
import light.commands.DummyArithmeticCommand;
import light.commands.LabelCommand;
import light.commands.ModulateCommand;
import light.commands.MoveCommand;
import light.commands.commandcontrol.CommandFormatException;
import light.commands.commandcontrol.CommandLine;
import light.commands.commandcontrol.commandproxys.AddressTypeProxy;
import light.commands.commandcontrol.commandproxys.CommandTypeProxy;
import light.commands.commandcontrol.commandproxys.OperatorTypeProxy;
import light.commands.commandcontrol.commandproxys.OperatorTypeProxy.Operator;
import light.commands.commandcontrol.commandproxys.ValueTypeProxy;
import light.encoders.Encoders;
import light.executors.Executor;
import light.fixtures.Attribute;
import light.fixtures.Fixture;
import light.fixtures.PatchManager;
import light.fixtures.profile.Profile;
import light.fixtures.profile.ProfileManager;
import light.fixtures.profile.ProfileParseException;
import light.general.Addressable;
import light.general.ConsoleAddress;
import light.general.DataStore;
import light.guipackage.cli.CLI;
import light.guipackage.general.Rectangle;
import light.guipackage.gui.GUI;
import light.guipackage.gui.IO;
import light.stores.AbstractStore;
import light.stores.Group;
import light.stores.Preset;
import light.stores.Preset.PresetType;
import light.stores.Sequence;
import light.stores.View;
import light.stores.effects.Effect;
import light.uda.FixtureWindow;
import light.uda.KeyWindow;
import light.uda.UDA;
import light.uda.guiinterfaces.GUIInterface;

public class Light {
    
    static Light singleton;
    
    //Pools
    private Map<PresetType, Pool<Preset>> presetPools;
    private Pool<Effect> effectPool;
    private Pool<View> viewPool;
    private Pool<Sequence> sequencePool;
    private Pool<Group> groupPool;
    private Pool<Executor> executorPool;

    //Static (Non-UDA) element GUIs
    private Set<GUIInterface> staticGUIs;
    
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

    public Set<Pool<?>> allPoolSet() {
        Set<Pool<?>> pools = new HashSet<>();
        for (Pool<?> pool : presetPools.values()) pools.add(pool);
        pools.add(effectPool);
        pools.add(viewPool);
        pools.add(sequencePool);
        pools.add(groupPool);
        pools.add(executorPool);
        return pools;
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

    public GUIInterface getStaticGUIElement(Class<? extends GUIInterface> clazz) {
        for (GUIInterface inter : staticGUIs) {
            List<Class<?>> interfaces = Arrays.asList(inter.getClass().getInterfaces());
            if (interfaces.contains(clazz)) return inter;
        }
        return null;
    }
    
    private void setup() {
        staticGUIs = new HashSet<GUIInterface>();

        GUI.initialise(this, null); //Passing null forces full screen
        
        //Insantiate logic components
        ProfileManager.getInstance();
        PatchManager.getInstance();
        UDA.getInstance();
        Encoders.getInstance();
        Programmer.getInstance();
        CommandLine.getInstance().setDefaultCommand(ModulateCommand.class);;
        
        //Make pools
        presetPools = new HashMap<PresetType, Pool<Preset>>();
        int i = 0;
        for (PresetType pT : PresetType.values()) {
            i = pT.ordinal()+1;
            presetPools.put(pT, new Pool<Preset>(new ConsoleAddress(Preset.class, i, 0)));
        }
        
        groupPool = new Pool<Group>(new ConsoleAddress(Group.class, i++, 0));
        effectPool = new Pool<Effect>(new ConsoleAddress(Effect.class, i++, 0));
        viewPool = new Pool<View>(new ConsoleAddress(View.class, i++, 0));
        //Sequence and executor pool do not follow other pools in having relevant base prefix numbers
        sequencePool = new Pool<Sequence>(new ConsoleAddress(Sequence.class, 0, 0));
        executorPool = new Pool<Executor>(new ConsoleAddress(Executor.class, 0, 0));


        //Generate static gui elements
        staticGUIs.add(GUI.getInstance().addStaticElementToGUI(Light.class));
        staticGUIs.add(GUI.getInstance().addStaticElementToGUI(CommandLine.class));
        staticGUIs.add(GUI.getInstance().addStaticElementToGUI(UDA.getInstance()));        
        staticGUIs.add(GUI.getInstance().addStaticElementToGUI(View.class));

        IO.getInstance().requestPaint();
    }
    
    private void mock() {
        //Mock profiles
        ProfileManager pro = ProfileManager.getInstance();
        Profile pro1 = null;
        Profile pro2 = null;
        try {
            pro1 = pro.parseProfile("profiletest.xml");
            pro2 = pro.parseProfile("profile2.xml");
        }
        catch (ProfileParseException e) {CLI.error(e.toString());}
        
        //Mock fixtures
        Fixture fixture1 = null;
        Fixture fixture2 = null;
        int i=0;
        for (; i<5; i++) {
            fixture1 = new Fixture(new ConsoleAddress(Fixture.class, 0, i+1), pro1);
            PatchManager.getInstance().addFixture(fixture1);
        }
        for (; i<10; i++) {
            fixture2 = new Fixture(new ConsoleAddress(Fixture.class, 0, i+1), pro2);
            PatchManager.getInstance().addFixture(fixture2);
        }
        
        //Mock prog values
        Programmer prog = Programmer.getInstance();
        prog.select(fixture1);

        prog.set(fixture2, Attribute.DIM, 100d, false);
        //prog.set(fixture, Attribute.COLORRGB1, 100d, false);
        //prog.set(fixture, Attribute.COLORRGB2, 100d, false);
        prog.set(fixture2, Attribute.COLORRGB3, 100d, false);
        prog.set(fixture2, Attribute.COLORRGB4, 70d, false);

        prog.set(fixture1, Attribute.DIM, 30d, false);
        prog.set(fixture1, Attribute.COLORRGB1, 100d, false);
        prog.set(fixture1, Attribute.COLORRGB3, 100d, false);
        prog.set(fixture1, Attribute.GOBO1_INDEX, 20d, false);

        //Mock presets
        Preset preset = new Preset(PresetType.DIMMER.getBaseAddress().setSuffix(2), PresetType.DIMMER);
        DataStore store = new DataStore();
        store.set(fixture2, Attribute.DIM, 0d, true);
        store.set(fixture1, Attribute.DIM, 0d, true);
        preset.setStore(store);
        preset.setLabel("All Off");
        //getPresetPool(PresetType.DIMMER).add(preset);

        preset = new Preset(PresetType.DIMMER.getBaseAddress().setSuffix(3), PresetType.DIMMER);
        store = new DataStore();
        store.set(fixture2, Attribute.DIM, 100d, true);
        store.set(fixture1, Attribute.DIM, 100d, true);
        preset.setStore(store);
        preset.setLabel("All On");
        //getPresetPool(PresetType.DIMMER).add(preset);

        //Mock views
        View view = new View(new ConsoleAddress(View.class, viewPool.getAddress().getPrefix(), 1));
        view.setLabel("Pools");
        int x = 0, y = 0;
        for (PresetType p : PresetType.values()) {
            if (x>UDA.getInstance().getSize().x) {
                x = 0;
                y += 10;
            }
            view.add(getPresetPool(p), new Rectangle(x,y, 3, 10));
            x += 3;
        }
        view.add(groupPool, new Rectangle(0, 10, 6, 3));
        viewPool.add(view);

        view = new View(new ConsoleAddress(View.class, viewPool.getAddress().getPrefix(), 2));
        view.setLabel("Prog");
        view.add(new FixtureWindow(), new Rectangle(0, 0, 8, 7));
        view.add(Encoders.getInstance(), new Rectangle(0, 9, 9, 2));
        view.add(new KeyWindow(), new Rectangle(9, 0, 9, 8));
        view.add(getPresetPool(Preset.PresetType.DIMMER), new Rectangle(9,8, 9, 3));
        viewPool.add(view);

        view = new View(new ConsoleAddress(View.class, viewPool.getAddress().getPrefix(), 3));
        view.setLabel("Effects");
        viewPool.add(view);
        
        //currentView.getUDA().createZone(Encoders.class, new Rectangle(0, 7, 10, 2));
        //currentView.getUDA().createZone(FixtureWindow.class, new Rectangle(0, 0, 11, 7));

        //Persitency mock
        //Persistency.getInstance().saveToFile("output.txt");
        //Persistency.getInstance().loadFromFile("output.txt");

        CommandLine cl = CommandLine.getInstance();
        cl.addToCommand(new CommandTypeProxy(MoveCommand.class));
        cl.addToCommand(new ValueTypeProxy(1));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(2));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(3));
        cl.addToCommand(new OperatorTypeProxy(Operator.AT));
        cl.addToCommand(new ValueTypeProxy(4));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(5));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(8));
        try {cl.resolveForCommand();}
        catch (CommandFormatException e) { e.printStackTrace();}

        cl.clear();

        cl.addToCommand(new CommandTypeProxy(LabelCommand.class));
        cl.addToCommand(new AddressTypeProxy(new ConsoleAddress(Fixture.class)));
        try {cl.resolveForCommand();}
        catch (CommandFormatException e) { e.printStackTrace();}

        cl.clear();

        cl.addToCommand(new ValueTypeProxy(1));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(2));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(3));
        cl.addToCommand(new OperatorTypeProxy(Operator.AT));
        cl.addToCommand(new ValueTypeProxy(4));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(5));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(9));
        try {cl.resolveForCommand();}
        catch (CommandFormatException e) { e.printStackTrace();}

        cl.clear();
        cl.setDefaultCommand(DummyArithmeticCommand.class);

        cl.addToCommand(new ValueTypeProxy(1));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(2));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(3));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(4));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(5));
        cl.addToCommand(new OperatorTypeProxy(Operator.PLUS));
        cl.addToCommand(new ValueTypeProxy(9));
        try {
            Command c = cl.resolveForCommand();
            double d = ((DummyArithmeticCommand) c).getDouble();
            CLI.debug("Result double: "+d);
        }
        catch (CommandFormatException e) { e.printStackTrace();}
    }
    
    public static void main(String args[]) {
        Light light = Light.getInstance();
        light.mock();
    }
}