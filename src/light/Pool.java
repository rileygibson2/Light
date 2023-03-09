package light;

import java.util.ArrayList;
import java.util.List;

import light.general.ConsoleAddress;

public class Pool<T> {
    
    public ConsoleAddress address;

    List<T> elements;

    public Pool() {
        elements = new ArrayList<T>();
    }

    public void addElement(T t) {elements.add(t);}
}
