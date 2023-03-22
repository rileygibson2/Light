package light.stores.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import light.Fixture;
import light.executors.ExecutorCapable;
import light.general.Attribute;
import light.general.ConsoleAddress;
import light.general.DataStore;
import light.stores.AbstractStore;

public class Effect extends AbstractStore implements ExecutorCapable {
    
    private Map<Attribute, EffectLine> lines;
    private List<Fixture> fixtures;

    private int priority;

    public Effect(ConsoleAddress address) {
        super(address);
        fixtures = new ArrayList<Fixture>();
        lines = new HashMap<Attribute, EffectLine>();
    }

    public int getPriority() {return priority;}

    @Override
    public DataStore getOutput() {
        throw new UnsupportedOperationException("Unimplemented method 'getOutput'");
    }

    @Override
    public boolean registrationCheck() {
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
}
