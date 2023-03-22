package light;

import java.util.ArrayList;
import java.util.List;

import light.general.Addressable;
import light.general.ConsoleAddress;

public class Pool<T extends Addressable> extends Addressable {

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
}
