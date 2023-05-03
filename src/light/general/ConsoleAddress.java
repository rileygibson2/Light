package light.general;

import java.io.Console;

import light.persistency.PersistencyCapable;
import light.persistency.PersistencyWriter;

public class ConsoleAddress implements Comparable<ConsoleAddress>, PersistencyCapable {
    
    private final Class<? extends Addressable> scope;
    private int prefix;
    private int suffix;

    public ConsoleAddress(Class<? extends Addressable> scope) {
        this.scope = scope;
        this.prefix = 0;
        this.suffix = 0;
    }

    public ConsoleAddress(Class<? extends Addressable> scope, int prefix, int suffix) {
        this.scope = scope;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public int getPrefix() {return prefix;}

    public ConsoleAddress setPrefix(int prefix) {
        this.prefix = prefix;
        return this;
    }

    public int getSuffix() {return suffix;}

    public ConsoleAddress setSuffix(int suffix) {
        this.suffix = suffix;
        return this;
    }

    public Class<? extends Addressable> getScope() {return this.scope;}

    public boolean matchesScope(ConsoleAddress a) {return this.scope==a.scope;}

    public boolean matchesScope(Class<? extends Addressable> c) {return this.scope==c;}

    public boolean matchesPrefix(ConsoleAddress a) {return this.prefix==a.prefix;}

    public boolean lessThan(ConsoleAddress a) {
        return matchesScope(a)&&matchesPrefix(a)&&this.prefix<a.prefix;
    }

    public ConsoleAddress clone() {
        return new ConsoleAddress(scope, prefix, suffix);
    }

    @Override
    public int compareTo(ConsoleAddress o) {
        if (o.scope!=scope) return -100;
        if (o.prefix!=prefix) return prefix-o.prefix;
        return suffix-o.suffix;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConsoleAddress)) return false;
        ConsoleAddress c = (ConsoleAddress) o;
        return c.scope==this.scope&&c.prefix==this.prefix&&c.suffix==this.suffix;
    }

    public String toAddressString() {return prefix+"."+suffix;}

    public String toDisplayString() {
        return getScope().getSimpleName()+" "+toAddressString();
    }

    @Override
    public String toString() {
        return "["+scope.getSimpleName()+" "+prefix+"."+suffix+"]";
    }

    public static ConsoleAddress getBase(Class<? extends Addressable> scope) {
        return new ConsoleAddress(scope);
    }

    @Override
    public byte[] getBytes() {
        PersistencyWriter pW = new PersistencyWriter();
        pW.put(this.scope.getName().getBytes());
        pW.put((byte) prefix);
        pW.put((byte) suffix);
        pW.wrapInSegment();
        return pW.toArray();
    }

    @Override
    public void generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }

    public class GenericAddressScope extends Addressable {

        public GenericAddressScope(ConsoleAddress address) {
            super(address);
        }

    }
}
