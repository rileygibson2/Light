package light.commands.commandcontrol.commandproxys;

import java.util.ArrayList;
import java.util.List;

import light.commands.commandcontrol.CommandFormatException;
import light.general.ConsoleAddress;

public class OperatorTypeProxy extends CommandProxy {
    
    public enum Operator {
        PLUS("+"),
        MINUS("-"),
        AT("at"),
        THRU("thru"),
        IF("if");
        
        private String text;
        
        private Operator(String text) {
            this.text = text;
        }
        
        public String getText() {return this.text;}
    }
    
    private Operator operator;
    
    public OperatorTypeProxy(Operator operator) {
        super(false);
        this.operator = operator;
    }

    public Operator getOperator() {return operator;}
    
    @Override
    public Class<?> getResolveType() {
        switch (operator) {
            case PLUS:  
            if (!children.isEmpty()) { //Check if all args are double type
                boolean allChildrenDouble = true;
                for (CommandProxy cP : children) {
                    if (cP.getResolveType()!=double.class) {
                        allChildrenDouble = false;
                        break;
                    }
                }
                if (allChildrenDouble) return double.class;
                return List.class;
            }
            return List.class;
            
            case THRU: return List.class;
            case AT:
            case IF:
            case MINUS:
            default: return null;
        }
    }
    
    @Override
    public Object resolve() throws CommandFormatException {
        switch (operator) {
            case PLUS: 
            if (getResolveType()==double.class) return resolveNumberPlus();
            return resolveAddressPlus();
            case THRU: return resolveThru();
            case AT:
            case IF:
            case MINUS:
            default: return null;
        }
    }
    
    private double resolveNumberPlus() throws CommandFormatException {
        if (children.size()!=2) throw new CommandFormatException("Number plus operator requires specifically two address arguments");
        
        Object a = children.get(0).resolve();
        if (!(a instanceof Double)) throw new CommandFormatException("First argument of number plus must be double");
        
        Object b = children.get(1).resolve();
        if (!(b instanceof Double)) throw new CommandFormatException("Second argument of numnber plus must be double");
        
        
        return (double) a + (double) b;
    }
    
    private List<ConsoleAddress> resolveAddressPlus() throws CommandFormatException {
        if (children.size()!=2) throw new CommandFormatException("Address plus operator requires specifically two address arguments");
        
        Object a = children.get(0).resolve();
        if (!(a instanceof ConsoleAddress)) throw new CommandFormatException("First argument of address plus must be ConsoleAddress");
        
        Object b = children.get(1).resolve();
        if (!(b instanceof ConsoleAddress)) throw new CommandFormatException("Second argument of address plus must be ConsoleAddress");
        
        List<ConsoleAddress> result = new ArrayList<>();
        result.add((ConsoleAddress) a);
        result.add((ConsoleAddress) b);
        return result;
    }
    
    private List<ConsoleAddress> resolveThru() throws CommandFormatException {
        if (children.size()!=2) throw new CommandFormatException("Thru operator requires two arguments");
        
        Object a = children.get(0).resolve();
        if (!(a instanceof ConsoleAddress)) throw new CommandFormatException("First argument of thru must be ConsoleAddress");
        ConsoleAddress aAd = (ConsoleAddress) a;
        
        Object b = children.get(1).resolve();
        if (!(b instanceof ConsoleAddress)) throw new CommandFormatException("Second argument of thru must be ConsoleAddress");
        ConsoleAddress bAd = (ConsoleAddress) a;
        
        if (!aAd.matchesScope(bAd)||!aAd.matchesPrefix(bAd)) throw new CommandFormatException("First argument address must be same scope and prefix as second argument address");
        if (!aAd.lessThan(bAd)) throw new CommandFormatException("First argument address must be less than second argument address");
        
        //Build address list
        List<ConsoleAddress> result = new ArrayList<>();
        for (int i=aAd.getPrefix(); i<=bAd.getPrefix(); i++) {
            result.add(new ConsoleAddress(aAd.getScope(), aAd.getPrefix(), i));
        }
        return result;
    }
    
    @Override
    public String getTreeString(String indent) {
        String result = "[Proxy: type="+getClass().getSimpleName()+" real=";
        result += operator.toString();
        result += "]";
        
        indent += "     ";
        for (CommandProxy child : children) result += "\n"+indent+child.getTreeString(indent);
        return result;
    }
    
    @Override
    public String toString() {
        String result = "";
        if (children.size()==0) return operator.getText();
        for (int i=0; i<children.size(); i++) {
            if (i==children.size()-1&&children.size()>1) result += " "+children.get(i).toString();
            else result += " "+children.get(i).toString()+" "+operator.getText();
        }
        return result;
    }
}
