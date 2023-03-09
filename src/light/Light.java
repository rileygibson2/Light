package light;

import java.util.HashSet;
import java.util.Set;

import guipackage.gui.GUI;
import light.stores.Effect;
import light.stores.Preset;
import light.stores.Preset.PresetType;
import light.stores.Sequence;
import light.stores.View;
import light.uda.UDA;
import light.uda.commandline.CommandLine;

public class Light {

    static Light singleton;

    //Logic
    private Set<Fixture> fixtures;
    private Programmer programmer;


    //Pools
    private Pool<Preset> dimmerPool;
    public Pool<Preset> colorPool;
    private Pool<Preset> positionPool;
    private Pool<Preset> focusPool;
    private Pool<Preset> beamPool;
    private Pool<Preset> goboPool;
    private Pool<Preset> prisimPool;
    private Pool<Preset> shaperPool;

    private Pool<Effect> effectPool;
    private Pool<View> viewPool;
    private Pool<Sequence> sequencePool;

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
        switch (p) {
            case Beam: return beamPool;
            case Color: return colorPool;
            case Dimmer: return dimmerPool;
            case Focus: return focusPool;
            case Gobo: return goboPool;
            case Position: return positionPool;
            case Shaper: return shaperPool;
            case Prisim: return prisimPool;
            default: return null;
        }
    }

    private void setup() {
        GUI.initialise(this, null);
        cL = new CommandLine();
        programmer = Programmer.getInstance();

        fixtures = new HashSet<Fixture>();

        dimmerPool = new Pool<Preset>();
        colorPool = new Pool<Preset>();
        positionPool = new Pool<Preset>();
        focusPool = new Pool<Preset>();
        beamPool = new Pool<Preset>();
        goboPool = new Pool<Preset>();
        prisimPool = new Pool<Preset>();
        shaperPool = new Pool<Preset>();

        sequencePool = new Pool<Sequence>();
        effectPool = new Pool<Effect>();
        viewPool = new Pool<View>();

        currentView = new View(new UDA());
    }

    public static void main(String args[]) {
        Light.getInstance();
    }
}
