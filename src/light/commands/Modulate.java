package light.commands;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import light.Fixture;
import light.Light;
import light.Programmer;
import light.general.Attribute;
import light.general.ConsoleAddress;

public class Modulate implements Command {
    
    List<ConsoleAddress> targetFixtures;
    Object source;
    
    public Modulate(List<ConsoleAddress> targetFixtures, double value) {
        this.targetFixtures = targetFixtures;
        this.source = value;
    }
    
    public Modulate(ConsoleAddress targetFixture, double value) {
        targetFixtures = new ArrayList<ConsoleAddress>();
        targetFixtures.add(targetFixture);
        this.source = value;
    }
    
    public Modulate(List<ConsoleAddress> targetFixtures, ConsoleAddress sourceFixture) {
        this.targetFixtures = targetFixtures;
        this.source = sourceFixture;
    }
    
    public Modulate(ConsoleAddress targetFixture, ConsoleAddress sourceFixture) {
        targetFixtures = new ArrayList<ConsoleAddress>();
        targetFixtures.add(targetFixture);
        this.source = sourceFixture;
    }
    
    public Modulate(List<ConsoleAddress> targetFixtures, List<ConsoleAddress> sourceFixtures) {
        this.targetFixtures = targetFixtures;
        this.source = sourceFixtures;
    }
    
    @Override
    public void execute() {
        //Resolve target addresses into fixtures
        List<Fixture> fixtures = Light.getInstance().getFixtures(targetFixtures);
        Programmer prog = Programmer.getInstance();
        prog.clearSelectedFixtures();
        
        if (source instanceof Double) { //Change intensity for target fixture/s
            for (Fixture f : fixtures) prog.set(f, Attribute.Intensity, (Integer) source, true);
        }
        if (source instanceof ConsoleAddress) { //Set all values of target fixture/s to values of source fixture's
            //Resolve source addresses into fixtures
            Fixture sourceFixture = Light.getInstance().getFixture((ConsoleAddress) source);
            Map<Attribute, Integer> attributes = prog.getFixtureValues(sourceFixture);
            //TODO Clone values
            
            for (Fixture f : fixtures) prog.set(f, attributes, true);
        }
        prog.selectFixtures(fixtures);
    }
    
}
