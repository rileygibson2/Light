package light.commands.commandcontrol.commandproxys;

import light.commands.commandcontrol.CommandFormatException;

public class ValueTypeProxy extends CommandProxy {
    
    private double value;
    
    
    public ValueTypeProxy(double value) {
        super(true);
        this.value = value;
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
    public String getTreeString(String indent) {
        String result = "[Proxy: type="+getClass().getSimpleName()+" real=";
        result +=  Double.toString(value);
        result += "]";
        
        indent += "     ";
        for (CommandProxy child : children) result += "\n"+indent+child.getTreeString(indent);
        return result;
    }
    
    @Override
    public String toString() {
        String result = "";
        result =  Double.toString(value);
        for (CommandProxy child : children) result += " "+child.toString();
        return result;
    }
    
    /*public CommandProxy(String valueString) {
        this.valueString = valueString;
    }*/
}
