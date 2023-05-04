package light.commands.commandcontrol;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import light.commands.Command;
import light.commands.commandcontrol.commandproxys.CommandProxy;
import light.commands.commandcontrol.commandproxys.CommandTypeProxy;
import light.commands.commandcontrol.commandproxys.OperatorTypeProxy;
import light.commands.commandcontrol.commandproxys.OperatorTypeProxy.Operator;
import light.commands.commandcontrol.commandproxys.ValueTypeProxy;
import light.guipackage.cli.CLI;
import light.guipackage.gui.IO;

public class CommandController {
    
    private Set<Class<? extends Command>> acceptedCommands; //Register of commands this controller will accept
    private boolean acceptAllCommands;
    
    private CommandProxy commandRoot;
    private ArrayDeque<CommandProxy> commandStack; //Working stack of all parts of this command
    private Runnable commandUpdatedAction;
    private String workingText; //Used for typed input, will fill up until a command proxy type can be extracted from it
    
    private boolean allowLeadingTerminals; //When set the controller will allow the leading proxy to be a terminal
    
    public CommandController() {
        acceptAllCommands = false;
        acceptedCommands = new HashSet<Class<? extends Command>>();
        commandRoot = null;
        commandStack = new ArrayDeque<CommandProxy>();
        workingText = "";
    }
    
    public boolean isEmpty() {return commandRoot==null&&workingText=="";}
    
    public void clear() {
        commandRoot = null;
        commandStack.clear();
        workingText = "";
        if (hasCommandUpdatedAction()) commandUpdatedAction.run();
    }
    
    public void clearWorkingText() {workingText = "";}
    
    public String getWorkingText() {return workingText;}
    
    public void destroy() {
        IO.getInstance().deregisterKeyListener(this);
    }
    
    public void setCommandUpdatedAction(Runnable a) {this.commandUpdatedAction = a;}
    public boolean hasCommandUpdatedAction() {return commandUpdatedAction!=null;}
    
    /**
    * Used to add a component to this command
    * @param cP
    */
    public void addToCommand(CommandProxy cP) {
        if (cP==null) return;
        //Check (if proxy is command) if command is accepted
        if (cP instanceof CommandTypeProxy&&!commandAccepted((Class<? extends Command>) cP.getResolveType())) {
            CLI.error("This command controller cannot accept the command class "+cP.getResolveType());
            return;
        }
        
        if (commandRoot==null) { //Start new command
            commandRoot = cP;
            commandStack.push(cP);
        }
        else { //Continue current command
            //Work back through command stack to find last non-terminal proxy
            CommandProxy lastNonTerminal = null;
            Iterator<CommandProxy> i = commandStack.iterator();
            
            while (i.hasNext()) {
                CommandProxy next = i.next();
                if (!next.isTerminal()) {
                    lastNonTerminal = next;
                    break;
                }
            }
            
            if (lastNonTerminal==null) { //This only occurs in one of the below edge cases
                //plus and thru edge case
                if (cP instanceof OperatorTypeProxy && ((OperatorTypeProxy) cP).getOperator()==Operator.PLUS||((OperatorTypeProxy) cP).getOperator()==Operator.THRU) {
                    CommandProxy prevProxy = commandStack.pop();
                    cP.addChild(prevProxy);
                    //Need to check if previous was root because if so then cP needs to become root
                    if (commandRoot==prevProxy) commandRoot = cP;
                    commandStack.push(cP);
                }
            }
            else { //General case
                lastNonTerminal.addChild(cP);
                commandStack.push(cP);
            }
        }
        
        if (hasCommandUpdatedAction()) commandUpdatedAction.run();
    }
    
    /**
    * Simply updates the local working text store.
    * DOES NOT parse the working text upon update, this must be called manually.
    * This is so working text can include numbers being built as keys are pressed. If working text 
    * tried to parse for a command every time a number key was pressed then you would never be able
    * to enter more than a 1 digit number via keys.
    * @param update
    */
    public void updateWorkingText(String update) {
        workingText += update;
        if (hasCommandUpdatedAction()) commandUpdatedAction.run();
    }
    
    /**
    * Parses the working text for a useable command proxy. If a command proxy is found it is added
    * to the command tree and the working text is cleared.
    * 
    * @return whether or not a workable command proxy is parsed
    */
    public boolean parseWorkingText() {
        //Parse working text to see if command proxy can be formed
        CommandProxy parsed = null;
        
        //Check for operator
        for (Operator o : Operator.values()) {
            if (workingText.toLowerCase().equals(o.getText().toLowerCase())) {
                parsed = new OperatorTypeProxy(o);
            }
        }
        
        //Check for value
        Double d;
        try {
            d = Double.parseDouble(workingText);
            parsed = new ValueTypeProxy(d);
        }
        catch (NumberFormatException ex) {}
        
        if (parsed!=null) {
            //Add to command tree
            addToCommand(parsed);
            clearWorkingText();
            return true;
        }
        return false;
    }
    
    public void acceptAllCommands(boolean accept) { acceptAllCommands = accept;}
    
    /**
    * Register command class with command line using the class's simple name as the name mapping
    * @param s
    * @param c
    */
    public void registerCommand(Class<? extends Command> c) {acceptedCommands.add(c);}
    
    public void deregisterCommand(Class<? extends Command> c) {acceptedCommands.remove(c);}
    
    public boolean commandAccepted(Class<? extends Command> c) {
        return (acceptAllCommands) ? true : acceptedCommands.contains(c);
    }
    
    public Set<Class<? extends Command>> acceptedCommands() {return acceptedCommands;}

    public boolean containsProxyOfType(Class<? extends CommandProxy> type) {
        if (commandRoot==null) return false;
        return commandRoot.subtreeContainsType(type);
    }

    public void executeCommand() throws CommandFormatException {
        Command c = null;
        c = resolveForCommand();
        if (c!=null) c.execute();
        clear();
    }
    
    public Command resolveForCommand() throws CommandFormatException {
        if (commandRoot==null) throw new CommandFormatException("Cannot resolve command - no command root");
        Object o = commandRoot.resolve();
        
        if (o instanceof Command) return ((Command) o);
        else throw new CommandFormatException("Did not resolve to an object of class Command");
    }
    
    public Double resolveForDouble() throws CommandFormatException {
        if (commandRoot==null) throw new CommandFormatException("Cannot resolve command - no command root");
        Object o = commandRoot.resolve();
        
        if (o instanceof Double) return ((Double) o);
        else throw new CommandFormatException("Did not resolve to an object of class double");
    }
    
    public String getTreeString() {
        return commandRoot!=null ? commandRoot.getTreeString("") : "Cannot create tree string as no command root";
    }
    
    public String getCommandAsString() {
        if (commandRoot==null) return workingText;
        return commandRoot.toString()+" "+workingText;
    }
}
