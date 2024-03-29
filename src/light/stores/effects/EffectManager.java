package light.stores.effects;

import light.Programmer;
import light.fixtures.Attribute;
import light.fixtures.Fixture;
import light.general.ConsoleAddress;

public class EffectManager {
    

    public static Effect createEffect(ConsoleAddress address) {
        Effect effect = new Effect(address);
        
        Programmer prog = Programmer.getInstance();
        for (Fixture f : prog.getSelected()) {
            effect.addFixture(f);

            //Activate effect line for all attributes this fixture has a value for in programmer
            for (Attribute a : prog.get(f).keySet()) effect.activeLine(a);
        }
        return effect;
    }
}
