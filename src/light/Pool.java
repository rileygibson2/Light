package light;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import light.general.Addressable;
import light.general.ConsoleAddress;
import light.persistency.PersistencyCapable;
import light.persistency.PersistencyWriter;
import light.uda.UDACapable;
import light.uda.guiinterfaces.GUIInterface;

public class Pool<T extends Addressable & PersistencyCapable> extends Addressable implements PersistencyCapable, UDACapable, Iterable<T> {

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

    public int size() {return elements.size();}

    public void add(T t) {
        if (!t.getAddress().matchesScope(getAddress())) return;
        elements.add(t);
        Collections.sort(elements);
    }

    public void remove(T t) {
        elements.remove(t);
        Collections.sort(elements);
    }

    public void remove(ConsoleAddress address) {
        for (T t : elements) {
            if (t.getAddress().equals(address)) elements.remove(t);
        }
        Collections.sort(elements);
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

    @Override
    public void setGUI(GUIInterface gui) {}

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int current = 0;
            @Override
            public boolean hasNext() {return current<elements.size();}
            @Override
            public T next() {return hasNext() ? elements.get(current++) : null;}
        };
    }

    @Override
    public String toString() {
        return "[Pool "+elements.toString()+"]";
    }
}
