package light.commands.commandcontrol.commandproxys;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import light.commands.Command;
import light.commands.commandcontrol.CommandFormatException;
import light.guipackage.cli.CLI;

public class CommandTypeProxy extends CommandProxy {
    
    private Class<? extends Command> commandClass;
    
    public CommandTypeProxy(Class<? extends Command> commandClass) {
        super(false);
        this.commandClass = commandClass;
    }

    @Override
    public boolean willAcceptProxy(CommandProxy proxy) {
        /**
         * Command type should accept any number of arguments of any type.
         * User should be able to see their entire command sprayed out on CLI.
         * Any problems will be dealt with at resolve time.
         */
        return true;
    }
    
    @Override
    public Class<?> getResolveType() {
        return commandClass;
    }
    
    @Override
    public Object resolve() throws CommandFormatException {
        //Find resolve types of all arguments
        Class<?>[] childTypes = new Class<?>[children.size()];
        int i = 0;
        for (CommandProxy cP : children) {
            childTypes[i] = cP.getResolveType();
            i++;
        }
        
        try { //Check command has constructor that can take those params
            Constructor<?> constructor = commandClass.getConstructor(childTypes);
            //Get actual parameters by resolving arguments
            List<Object> params = new ArrayList<>();
            for (CommandProxy cP : children) params.add(cP.resolve());
            
            //Create command
            return constructor.newInstance(params.toArray());
            
        } catch (Exception e) {CLI.error("While resolving command, a constructor for "+commandClass+" could not be found for args "+Arrays.deepToString(childTypes)+"\nSpecific construction problem: "+e.toString());}
        return null;
    }

    @Override
    public CommandTypeProxy clone() {
        CommandTypeProxy clone = new CommandTypeProxy(commandClass);
        for (CommandProxy child : children) clone.addChild(child);
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CommandTypeProxy&&((CommandTypeProxy) o).commandClass==this.commandClass; 
    }
    
    @Override
    public String toTreeString(String indent) {
        String result = "[Proxy: type="+getClass().getSimpleName()+" real=";
        result += commandClass.getSimpleName();
        result += "]";
        
        indent += "     ";
        for (CommandProxy child : children) result += "\n"+indent+child.toTreeString(indent);
        return result;
    }

    @Override
    public String toDisplayString() {
        return commandClass.getSimpleName().replace("Command", "");
    }
    
    @Override
    public String toString() {
        String result = "[Proxy: type="+getClass().getSimpleName()+" real=";
        result += commandClass.getSimpleName();
        result += "]";
        return result;
    }
}
