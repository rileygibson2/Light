package light.general;

public class ConsoleAddress {
    
    public int prefix;
    public int suffix;

    public ConsoleAddress(int prefix, int suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return prefix+"."+suffix;
    }
}
