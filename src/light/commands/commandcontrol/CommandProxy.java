package light.commands.commandcontrol;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import light.commands.Command;
import light.general.ConsoleAddress;
import light.guipackage.cli.CLI;

/**
* 
* Store Cue 1.1
* Store
*      ConsoleAddress(SequenceScope)  
* Resolves to Store(ConsoleAddress)  

Store Preset 2.3
* Store
*      ConsoleAddress(PresetScope)  
* Resolves to Store(ConsoleAddress)  
* 
* Fixture 1 at 100
* Modulate
*    ConsoleAddress(FixtureScope, prefix 0)
*    Value
* Resolves to Modulate(ConsoleAddress, int)
* 
* Fixture 1.1 thru Fixture 8 at 50
* Modulate
*      Thru
*          ConsoleAddress(FixtureScope, prefix 0)
*          ConsoleAddress(FixtureScope, prefix 0)
*      Value
* Resolves to Modulate(Set<ConsoleAddress>, int)
* 
* Fixture 1 at Fixture 2
* Modulate
*      ConsoleAddress(FixtureScope, prefix 0)
*      ConsoleAddress(FixtureScope, prefix 0)
* Resolves to Modulate(ConsoleAddress, ConsoleAddress)
* 
* Copy sequence 1 cue 2 at sequence 1 cue 4
* Copy
*      ConsoleAddress(SequenceScope)
*      ConsoleAddress(SequenceScope)
* Resolves to Copy(ConsoleAddress, ConsoleAddress)
* 
*/
public class CommandProxy {
    
    public enum CommandProxyType {
        Command,
        Operator,
        Address,
        Value;
    }
    
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
    
    private CommandProxyType type;
    private List<CommandProxy> children;
    
    //Potential values
    private Operator operator;
    private Class<? extends Command> commandClass;
    private ConsoleAddress consoleAddress;
    private double value;
    private String valueString;
    
    
    public CommandProxy(Class<? extends Command> commandClass) {
        this.commandClass = commandClass;
        type = CommandProxyType.Command;
        children = new ArrayList<CommandProxy>();
    }
    
    public CommandProxy(Operator operator) {
        this.operator = operator;
        type = CommandProxyType.Operator;
        children = new ArrayList<CommandProxy>();
    }
    
    public CommandProxy(ConsoleAddress consoleAddress) {
        this.consoleAddress = consoleAddress;
        type = CommandProxyType.Address;
        children = new ArrayList<CommandProxy>();
    }
    
    public CommandProxy(double value) {
        this.value = value;
        type = CommandProxyType.Value;
        children = new ArrayList<CommandProxy>();
    }
    
    public CommandProxy(String valueString) {
        this.valueString = valueString;
        type = CommandProxyType.Value;
        children = new ArrayList<CommandProxy>();
    }
    
    public void addChild(CommandProxy child) {
        if (child!=null&&!isTerminal()) children.add(child);
    }
    
    public List<CommandProxy> getChildren() {return children;}
    
    public Object getType() {
        if (type==CommandProxyType.Operator) return operator; 
        return type;
    }
    
    /**
    * If this command proxy is a terminal (i.e will not accept arguments) this method will return true.
    * @return
    */
    public boolean isTerminal() {return type==CommandProxyType.Value||type==CommandProxyType.Address;}
    
    //Returns type this proxy will resolve to
    public Class<?> getResolveType() {
        switch (type) {
            case Address: return ConsoleAddress.class;

            case Value: 
            if (valueString!=null) return String.class;
            else return double.class;

            case Operator:
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
            }
            break;

            case Command: return commandClass;
        }
        return null;
    }
    
    public Object resolve() throws CommandFormatException {
        switch (type) {
            case Command:
            //Find resolve types of all arguments
            List<Class<?>> childTypes = new ArrayList<>();
            for (CommandProxy cP : children) childTypes.add(cP.getResolveType());
            
            //Check command has constructor that can take those params
            try {
                Constructor<?> constructor = commandClass.getConstructor((Class<?>[]) childTypes.toArray());
                
                //Get actual parameters by resolving arguments
                List<Object> params = new ArrayList<>();
                for (CommandProxy cP : children) params.add(cP.resolve());
                
                //Creat command
                return constructor.newInstance(params.toArray());
                
            } catch (Exception e) {
                e.printStackTrace();
                CLI.error(e.toString());
            }
            return null;
            
            case Address: return consoleAddress;
            
            case Value:
            if (valueString!=null) return valueString;
            else return value;
            
            case Operator:
            switch (operator) {
                case PLUS: 
                if (getResolveType()==double.class) return resolveNumberPlus();
                return resolveAddressPlus();
                case THRU: return resolveThru();
                case AT: break;
                case IF: break;
                case MINUS: break;
            }
            break;
        }
        
        return null;
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
    
    public String getTreeString(String indent) {
        String result = "[Proxy: type="+type+" real=";
        switch (type) {
            case Address: result += consoleAddress.getScope().getSimpleName()+" "+consoleAddress.toAddressString(); break;
            case Command: result += commandClass.getSimpleName(); break;
            case Operator: result += operator.toString(); break;
            case Value: result +=  Double.toString(value); break;
        }
        result += "]";

        indent += "     ";
        for (CommandProxy child : children) result += "\n"+indent+child.getTreeString(indent);
        return result;
    }
    
    @Override
    public String toString() {
        String result = "";
        switch (type) {
            case Address:
            result =  consoleAddress.getScope().getSimpleName()+" "+consoleAddress.toAddressString();
            for (CommandProxy child : children) result += " "+child.toString();
            break;

            case Command:
            result = commandClass.getSimpleName();
            for (CommandProxy child : children) result += " "+child.toString();
            break;

            case Value: 
            result =  Double.toString(value);
            for (CommandProxy child : children) result += " "+child.toString();
            break;

            case Operator:
            if (children.size()==0) return operator.getText();
            for (int i=0; i<children.size(); i++) {
                if (i==children.size()-1&&children.size()>1) result += " "+children.get(i).toString();
                else result += " "+children.get(i).toString()+" "+operator.getText();
            }
            break;
        }
        return result;
    }
}
