package light.commands.commandcontrol.commandproxys;

import light.commands.commandcontrol.CommandFormatException;

public class ValueTypeProxy extends CommandProxy {
    
    private double value;
    
    public ValueTypeProxy(double value) {
        super(true);
        this.value = value;
    }

    @Override
    public boolean willAcceptProxy(CommandProxy proxy) {
        return false; //This proxy is terminal
    }
    
    @Override
    public Class<?> getResolveType() {
        return double.class;
    }
    
    
    @Override
    public Object resolve() throws CommandFormatException {
        return value;
    }

    @Override
    public ValueTypeProxy clone() {
        return new ValueTypeProxy(value);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ValueTypeProxy&&((ValueTypeProxy) o).value==this.value; 
    }
    
    @Override
    public String toTreeString(String indent) {
        String result = "[Proxy: type="+getClass().getSimpleName()+" real=";
        result +=  Double.toString(value);
        result += "]";
        
        indent += "     ";
        for (CommandProxy child : children) result += "\n"+indent+child.toTreeString(indent);
        return result;
    }

    @Override
    public String toDisplayString() {
        return Double.toString(value);
    }
    
    @Override
    public String toString() {
        String result = "[Proxy: type="+getClass().getSimpleName()+" real=";
        result +=  Double.toString(value);
        result += "]";
        return result;
    }
}
