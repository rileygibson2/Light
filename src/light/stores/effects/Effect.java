package light.stores.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import light.Fixture;
import light.general.Attribute;
import light.general.ConsoleAddress;
import light.general.DataStore;
import light.output.OutputCapable;
import light.stores.AbstractStore;

public class Effect extends AbstractStore implements OutputCapable {
    
    private Map<Attribute, EffectLine> lines;
    private List<Fixture> fixtures;

    public Effect(ConsoleAddress address) {
        super(address);
        fixtures = new ArrayList<Fixture>();
        lines = new HashMap<Attribute, EffectLine>();
    }

    @Override
    public DataStore getOutput() {
        throw new UnsupportedOperationException("Unimplemented method 'getOutput'");
    }
}
