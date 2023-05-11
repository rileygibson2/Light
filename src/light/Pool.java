package light;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import light.general.Addressable;
import light.general.ConsoleAddress;
import light.guipackage.cli.CLI;
import light.guipackage.gui.components.complexcomponents.PoolGUI;
import light.persistency.PersistencyCapable;
import light.stores.AbstractStore;
import light.stores.View;
import light.uda.UDA;
import light.uda.UDACapable;
import light.uda.guiinterfaces.GUIInterface;
import light.uda.guiinterfaces.PoolGUIInterface;
import light.uda.guiinterfaces.ViewGUIInterface;

public class Pool<T extends Addressable & PersistencyCapable> extends Addressable implements UDACapable, Iterable<T> {

    private List<T> elements;

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
        t.setUpdateAction(() -> updateGUI());
        elements.add(t);
        Collections.sort(elements);
        CLI.debug("adding to pool "+t.getAddress());
        updateGUI();
    }

    public void remove(T t) {
        if (!contains(t)) return;
        if (t instanceof AbstractStore) ((AbstractStore) t).setUpdateAction(null); //Will stop updates to this element updating this pool anymore
        elements.remove(t);
        Collections.sort(elements);
        updateGUI();
    }

    public void remove(ConsoleAddress address) {
        for (T t : elements) {
            if (t.getAddress().equals(address)) elements.remove(t);
        }
        Collections.sort(elements);
        updateGUI();
    }

    public boolean contains(T t) {return elements.contains(t);}

    public boolean contains(ConsoleAddress address) {
        if (!address.matchesScope(getAddress())) return false;

        for (T t : elements) {
            if (t.getAddress().equals(address)) return true;
        }
        return false;
    }

    private void updateGUI() {
        //Check UDA guis
        for (GUIInterface inter : UDA.getInstance().getGUIInterfacesOfClass(PoolGUIInterface.class)) {
            Pool<?> pool = ((PoolGUI) inter).getPool();
            if (pool.equals(this)) ((PoolGUIInterface) inter).update();
        }

        //Special case for view pool as is a static gui element as well as UDA gui element
        if (getAddress().matchesScope(View.class)) {
            GUIInterface inter = Light.getInstance().getStaticGUIElement(ViewGUIInterface.class);
            if (inter!=null) ((ViewGUIInterface) inter).update();
        }
    }

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
