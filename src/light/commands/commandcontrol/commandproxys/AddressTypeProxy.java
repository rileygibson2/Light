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
    public Class<?> getResolveType() {
        return ConsoleAddress.class;
    }
    
    @Override
    public Object resolve() throws CommandFormatException {
        return consoleAddress;
    }
    
    @Override
    public String getTreeString(String indent) {
        String result = "[Proxy: type="+getClass().getSimpleName()+" real=";
        result += consoleAddress.getScope().getSimpleName()+" "+consoleAddress.toAddressString();
        result += "]";
        indent += "     ";
        for (CommandProxy child : children) result += "\n"+indent+child.getTreeString(indent);
        return result;
    }
    
    @Override
    public String toString() {
        String result = "";
        result =  consoleAddress.toDisplayString();
        for (CommandProxy child : children) result += " "+child.toString();
        return result;
    }
}
