package light;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import light.general.Addressable;
import light.general.ConsoleAddress;
import light.persistency.PersistencyCapable;
import light.persistency.PersistencyWriter;
import light.uda.UDACapable;

public class Pool<T extends Addressable & PersistencyCapable> extends Addressable implements PersistencyCapable, UDACapable {

    Set<T> elements;

    public Pool(ConsoleAddress address) {
        super(address);
        elements = new HashSet<T>();
    }

    public T get(ConsoleAddress address) {
        if (!address.matchesScope(getAddress())) return null;

        for (T t : elements) {
            if (t.getAddress().equals(address)) return t;
        }
        return null;
    }

    public int size() {return elements.size();}

    public List<T> getList() {
        List<T> result = new ArrayList<>(elements);
        Collections.sort(result, new Comparator<T>() {
			public int compare(T t1, T t2) {
				return t1.getAddress().compareTo(t2.getAddress());
			}
		});
        return result;
    }

    public void add(T t) {elements.add(t);}

    public void remove(T t) {elements.remove(t);}

    public void remove(ConsoleAddress address) {
        for (T t : elements) {
            if (t.getAddress().equals(address)) elements.remove(t);
        }
    }

    public boolean contains(T t) {return elements.contains(t);}

    public boolean contains(ConsoleAddress address) {
        if (!address.matchesScope(getAddress())) return false;

        for (T t : elements) {
            if (t.getAddress().equals(address)) return true;
        }
        return false;
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
