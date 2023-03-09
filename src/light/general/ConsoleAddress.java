package light.general;

import java.io.Console;

public class ConsoleAddress {
    
    public int prefix;
    public int suffix;

    public ConsoleAddress(int prefix, int suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConsoleAddress)) return false;
        ConsoleAddress c = (ConsoleAddress) o;
        return c.prefix==this.prefix&&c.suffix==this.suffix;
    }

    @Override
    public String toString() {
        return prefix+"."+suffix;
    }
}
