package light;

import java.util.ArrayList;
import java.util.List;

import light.general.Addressable;
import light.general.ConsoleAddress;
import light.persistency.PersistencyCapable;
import light.persistency.PersistencyWriter;

public class Pool<T extends Addressable & PersistencyCapable> extends Addressable implements PersistencyCapable {

    List<T> elements;

    public Pool(ConsoleAddress address) {
        super(address);
        elements = new ArrayList<T>();
    }

    public T get(ConsoleAddress address) {
        if (!address.matchesScope(getAddress())) return null;

        for (T t : elements) {
            if (t.getAddress().equals(address)) return t;
        }
        return null;
    }

    public void add(T t) {elements.add(t);}

    public void remove(T t) {elements.remove(t);}

    public void remove(ConsoleAddress address) {
        for (T t : elements) {
            if (t.getAddress().equals(address)) elements.remove(t);
        }
    }

    @Override
    public byte[] getBytes() {
        PersistencyWriter pW = new PersistencyWriter();
        for (T t : elements) {
            pW.put(t.getBytes());
        }
        
        pW.wrapInSegment();
        return pW.toArray();
    }

    @Override
    public void generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
