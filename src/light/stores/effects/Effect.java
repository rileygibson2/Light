package light.stores.effects;

import java.util.HashMap;

import light.executors.ExecutorCapable;
import light.fixtures.Attribute;
import light.fixtures.Feature;
import light.fixtures.Fixture;
import light.general.ConsoleAddress;
import light.general.DataStore;
import light.persistency.PersistencyCapable;
import light.stores.AbstractStore;

public class Effect extends AbstractStore implements ExecutorCapable, PersistencyCapable {
    
	public static final int defaultSpeed = 5;
	public static final int defaultWidth = 50;
	public static final int defaultBlocks = 0;
	public static final int defaultPhase = 0;

	public static final int maxSpeed = 1000;

    private HashMap<Attribute, EffectLine> lines;

    private int priority;

    public Effect(ConsoleAddress address) {
        super(address);
        lines = new HashMap<Attribute, EffectLine>();
    }

    public int getPriority() {return priority;}

	public void addFixture(Fixture f) {
		//Check there is an effect line for attributes of this fixture
		for (Attribute a : f.getProfile().getAttributeList()) {
			if (!lines.containsKey(a)) {
				lines.put(a, new EffectLine(a, this));
			}
		}

		//Add fixture to all effect lines ???
		for (EffectLine line : lines.values()) line.addFixture(f);
	}

	public void activeLine(Attribute attribute) {
		lines.get(attribute).setActive(true);
	}

    @Override
    public DataStore getOutput() {
        throw new UnsupportedOperationException("Unimplemented method 'getOutput'");
    }

    @Override
    public boolean outputRegistrationCheck() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registrationCheck'");
    }

    @Override
    public void merge(AbstractStore toMerge) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'merge'");
    }

    @Override
    public void replace(AbstractStore toReplace) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'replace'");
    }

	@Override
	public void setMasterValue(int v) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'setMasterValue'");
	}

	@Override
	public void go() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'go'");
	}

	@Override
	public void goBack() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'goBack'");
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'pause'");
	}

	@Override
	public void on() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'on'");
	}

	@Override
	public void off() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'off'");
	}

	@Override
	public void flash() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'flash'");
	}

	@Override
	public void temp() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'temp'");
	}

	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'toggle'");
	}

	@Override
	public void swop() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'swop'");
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'load'");
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
	}

	@Override
	public void generateFromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
	}
}
