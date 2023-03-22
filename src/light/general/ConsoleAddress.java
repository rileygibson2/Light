package light.general;

public class ConsoleAddress implements Comparable {
    
    private final Class<? extends Addressable> scope;
    private int prefix;
    private int suffix;

    public ConsoleAddress(Class<? extends Addressable> scope, int prefix, int suffix) {
        this.scope = scope;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public int getPrefix() {return prefix;}
    public void setPrefix(int prefix) {this.prefix = prefix;}

    public int getSuffix() {return suffix;}
    public void setSuffix(int suffix) {this.suffix = suffix;}

    public Class<? extends Addressable> getScope() {return this.scope;}

    public boolean matchesScope(ConsoleAddress a) {return this.scope==a.scope;}

    public boolean matchesScope(Class<? extends Addressable> c) {return this.scope==c;}

    public boolean matchesPrefix(ConsoleAddress a) {return this.prefix==a.prefix;}

    public boolean lessThan(ConsoleAddress a) {
        return matchesScope(a)&&matchesPrefix(a)&&this.prefix<a.prefix;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ConsoleAddress)) return -1;
        ConsoleAddress o1 = (ConsoleAddress) o;
        if (o1.prefix!=prefix) return prefix-o1.prefix;
        return suffix-o1.suffix;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConsoleAddress)) return false;
        ConsoleAddress c = (ConsoleAddress) o;
        return c.scope==this.scope&&c.prefix==this.prefix&&c.suffix==this.suffix;
    }

    @Override
    public String toString() {
        return prefix+"."+suffix;
    }

    public class GenericAddressScope extends Addressable {

        public GenericAddressScope(ConsoleAddress address) {
            super(address);
        }

    }
}
