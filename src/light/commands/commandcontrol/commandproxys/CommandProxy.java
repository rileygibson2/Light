package light.commands.commandcontrol.commandproxys;

import java.util.ArrayList;
import java.util.List;

import light.commands.commandcontrol.CommandFormatException;

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
public abstract class CommandProxy {
    
    protected List<CommandProxy> children;
    
    private boolean isTerminal;
    
    public CommandProxy(boolean isTerminal) {
        children = new ArrayList<CommandProxy>();
    }
    
    public void addChild(CommandProxy child) {
        if (child!=null&&!isTerminal()) children.add(child);
    }
    
    public List<CommandProxy> getChildren() {return children;}
    
    /**
    * If this command proxy is a terminal (i.e will not accept arguments) this method will return true.
    * @return
    */
    public boolean isTerminal() {return isTerminal;}
    
    public boolean subtreeContainsType(Class<? extends CommandProxy> typeClass) {
        if (getClass()==typeClass) return true;
        for (CommandProxy child : getChildren()) {
            if (child.subtreeContainsType(typeClass)) return true;
        }
        return false;
    }

    /**
     * Returns type this proxy will resolve to.
     */
    public abstract Class<?> getResolveType();
    
    /**
     * Attempts to resolve this command proxy into a real object.
     * @return
     * @throws CommandFormatException
     */
    public abstract Object resolve() throws CommandFormatException;
    
    public abstract String getTreeString(String indent);
}
