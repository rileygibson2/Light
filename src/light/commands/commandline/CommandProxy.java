package light.commands.commandline;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import guipackage.cli.CLI;
import light.commands.Command;
import light.general.ConsoleAddress;

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
        Plus,
        Minus,
        At,
        Thru,
        If;

        @Override
        public String toString() {
            if (this==Operator.At) return "at";
            if (this==Operator.Thru) return "thru";
            if (this==Operator.Plus) return "+";
            if (this==Operator.Minus) return "-";
            if (this==Operator.If) return "if";
            return "";
        }
    }
    
    private CommandProxyType type;
    private List<CommandProxy> args;
    
    //Potential values
    private Operator operator;
    private Class<? extends Command> commandClass;
    private ConsoleAddress consoleAddress;
    private double value;
    private String valueString;
    
    
    public CommandProxy(Class<? extends Command> commandClass) {
        this.commandClass = commandClass;
        type = CommandProxyType.Command;
        args = new ArrayList<CommandProxy>();
    }
    
    public CommandProxy(Operator operator) {
        this.operator = operator;
        type = CommandProxyType.Operator;
        args = new ArrayList<CommandProxy>();
    }
    
    public CommandProxy(ConsoleAddress consoleAddress) {
        this.consoleAddress = consoleAddress;
        type = CommandProxyType.Address;
        args = new ArrayList<CommandProxy>();
    }
    
    public CommandProxy(double value) {
        this.value = value;
        type = CommandProxyType.Value;
        args = new ArrayList<CommandProxy>();
    }

    public CommandProxy(String valueString) {
        this.valueString = valueString;
        type = CommandProxyType.Value;
        args = new ArrayList<CommandProxy>();
    }
    
    public CommandProxyType getType() {return type;}

    public List<CommandProxy> getArgs() {return args;}
    
    //Returns type this proxy will resolve to
    public Class<?> getResolveType() {
        switch (type) {
            case Address: return ConsoleAddress.class;
            case Value: 
                if (valueString!=null) return String.class;
                else return double.class;
            case Operator:
            switch (operator) {
                case Plus: return List.class;
                case Thru: return List.class;
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
                List<Class<?>> argTypes = new ArrayList<>();
                for (CommandProxy cP : args) argTypes.add(cP.getResolveType());
                
                //Check command has constructor that can take those params
                try {
                    Constructor<?> constructor = commandClass.getConstructor((Class<?>[]) argTypes.toArray());
                    
                    //Get actual parameters by resolving arguments
                    List<Object> params = new ArrayList<>();
                    for (CommandProxy cP : args) params.add(cP.resolve());
                
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
                case Plus: return resolvePlus();
                case Thru: return resolveThru();
                case At: break;
                case If: break;
                case Minus: break;
            }
            break;
        }

        return null;
    }
    
    private List<ConsoleAddress> resolvePlus() throws CommandFormatException {
        if (args.size()!=2) throw new CommandFormatException("Plus operator requires two arguments");
        
        Object a = args.get(0).resolve();
        if (!(a instanceof ConsoleAddress)) throw new CommandFormatException("First argument of plus must be ConsoleAddress");
        
        Object b = args.get(1).resolve();
        if (!(b instanceof ConsoleAddress)) throw new CommandFormatException("Second argument of plus must be ConsoleAddress");
        
        List<ConsoleAddress> result = new ArrayList<>();
        result.add((ConsoleAddress) a);
        result.add((ConsoleAddress) b);
        return result;
    }
    
    private List<ConsoleAddress> resolveThru() throws CommandFormatException {
        if (args.size()!=2) throw new CommandFormatException("Thru operator requires two arguments");
        
        Object a = args.get(0).resolve();
        if (!(a instanceof ConsoleAddress)) throw new CommandFormatException("First argument of thru must be ConsoleAddress");
        ConsoleAddress aAd = (ConsoleAddress) a;
        
        Object b = args.get(1).resolve();
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
    public String toString() {
        switch (type) {
            case Address: return consoleAddress.toString();
            case Command: return commandClass.getSimpleName();
            case Operator: return operator.toString();
            case Value: return Double.toString(value);
        }
        return "";
    }
}
