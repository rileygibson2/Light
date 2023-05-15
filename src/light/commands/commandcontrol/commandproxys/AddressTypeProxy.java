package light.commands.commandcontrol.commandproxys;

import light.commands.commandcontrol.CommandFormatException;
import light.general.ConsoleAddress;

public class AddressTypeProxy extends CommandProxy {
    
    private ConsoleAddress consoleAddress;
    
    public AddressTypeProxy(ConsoleAddress consoleAddress) {
        super(true);
        this.consoleAddress = consoleAddress;
    }

    @Override
    public boolean willAcceptProxy(CommandProxy proxy) {
        return false; //This proxy is terminal
    }
    
    @Override
    public Class<?> getResolveType() {
        return ConsoleAddress.class;
    }
    
    @Override
    public Object resolve() throws CommandFormatException {
        return consoleAddress;
    }

    @Override
    public AddressTypeProxy clone() {
        return new AddressTypeProxy(consoleAddress);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AddressTypeProxy&&((AddressTypeProxy) o).consoleAddress==this.consoleAddress; 
    }
    
    @Override
    public String toTreeString(String indent) {
        String result = "[Proxy: type="+getClass().getSimpleName()+" real=";
        result += consoleAddress.getScope().getSimpleName()+" "+consoleAddress.toAddressString();
        result += "]";
        indent += "     ";
        for (CommandProxy child : children) result += "\n"+indent+child.toTreeString(indent);
        return result;
    }

    @Override
    public String toDisplayString() {
        return consoleAddress.toDisplayString();
    }
    
    @Override
    public String toString() {
        String result = "[Proxy: type="+getClass().getSimpleName()+" real=";
        result += consoleAddress.getScope().getSimpleName()+" "+consoleAddress.toAddressString();
        result += "]";
        return result;
    }
}
