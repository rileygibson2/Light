package light.commands.commandcontrol;

import java.util.ArrayDeque;
import java.util.HashSet;
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
    
    private ArrayDeque<CommandProxy> commandStack; //Working stack used to help build this command
    private Runnable commandUpdatedAction;
    private String workingText; //Used for typed input, will fill up until a command proxy type can be extracted from it
    
    private Class<? extends Command> defaultCommand; //When a proxy resolves without a specified command as the root then this command class will be used as the root command proxy
    
    public CommandController() {
        acceptAllCommands = false;
        acceptedCommands = new HashSet<Class<? extends Command>>();
        commandStack = new ArrayDeque<CommandProxy>();
        workingText = "";
    }
    
    public boolean isEmpty() {return commandStack.isEmpty()&&workingText=="";}
    
    public void clear() {
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
    * Sets the default command type proxy.
    * When a command tree is built from an inputted command and it is found to not have a root
    * CommandTypeProxy then this default proxy will be used instead.
    */
    public void setDefaultCommand(Class<? extends Command> defaultCommand) {this.defaultCommand = defaultCommand;}
    public boolean hasDefaultCommand() {return defaultCommand!=null;}
    
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
        for (CommandProxy proxy : commandStack) {
            if (proxy.getClass()==type) return true;
        }
        return false;
    }

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
        commandStack.push(cP);
        
        if (hasCommandUpdatedAction()) commandUpdatedAction.run();
    }
    
    /**
     * Will attempt to turn the build Command Proxy stack into a tree with a CommandTypeProxy
     * as the root.
     * 
     * Works on a and validates a few assumptions;
     * That commands must be the first element added to the command stack
     * That there may not be more than one command type proxy present in the command
     * That terminals cannot follow each other - there must be at most one terminal in a row.
     * This last point is realised in the fact that an exception will be raised if a terminal is parsed
     * before the last terminal was aquired by another part of the command.
     * 
     * @return The CommandProxy that is the root of the generated tree
     * @throws CommandFormatException if building a tree was unsuccesful due to an error in the command format
    */
    private CommandProxy generateCommandTree() throws CommandFormatException {
        CommandProxy bankedTerminal = null;
        ArrayDeque<CommandProxy> workingStack = new ArrayDeque<>();
        CommandProxy rootProxy = null;
        
        for (CommandProxy current : commandStack) {
            CommandProxy workingHead = workingStack.peek();
            
            if (current instanceof CommandTypeProxy) rootProxy = current;
            else if (current.isTerminal()) {
                //Check if working stack head will take it
                if (workingHead!=null&&workingHead.willAcceptProxy(current)) workingHead.addChild(current);
                else {
                    if (bankedTerminal!=null) throw new CommandFormatException("Banked terminal was not aquired before another terminal was encountered");
                    bankedTerminal = current;
                }
            }
            else {
                /**
                * SPECIAL CASE - operator duplication.
                * If an operator is already working head then this new copy of it shouldn't be considered.
                * Conside 1+2+3 should create a tree with one operator proxy with 3 children.
                */
                if (workingHead!=null&&workingHead.equals(current)) continue;
                
                //Check if cP will take any of terminal stack
                if (bankedTerminal!=null&&current.willAcceptProxy(bankedTerminal)) {
                    current.addChild(bankedTerminal);
                    bankedTerminal = null;
                }
                
                //Check if head of working stack will take cP
                if (workingHead!=null&&workingHead.willAcceptProxy(current)) workingHead.addChild(current);
                //Check if cP will accept working head
                else if (workingHead!=null&&current.willAcceptProxy(workingHead)) {
                    current.addChild(workingHead);
                    workingStack.remove(workingHead);
                    workingStack.push(current);
                }
                else workingStack.push(current);
            }
        }
        
        if (rootProxy==null) { //A command type wasn't found so try default
            if (defaultCommand==null) throw new CommandFormatException("A command stack must resolve to a command");
            rootProxy = new CommandTypeProxy(defaultCommand);
        }
        else { //A command type was found so assert is command and was first in stack
            if (!(rootProxy instanceof CommandTypeProxy)) throw new CommandFormatException("The root proxy must be a command type proxy at this point.");
            if (commandStack.peekLast()!=rootProxy) throw new CommandFormatException("The command type proxy must be the first proxy added to the command");
        }
        //Give root proxy banked terminal and all working stack elements
        if (bankedTerminal!=null&&rootProxy.willAcceptProxy(bankedTerminal)) rootProxy.addChild(bankedTerminal);
        for (CommandProxy workingProxy : workingStack) {
            if (rootProxy.willAcceptProxy(workingProxy)) rootProxy.addChild(workingProxy);
        }

        CLI.debug("Generated command tree:\n"+rootProxy.toTreeString(""));
        return rootProxy;
    }
    
    public void executeCommand() throws CommandFormatException {
        Command c = null;
        c = resolveForCommand();
        if (c!=null) c.execute();
        clear();
    }
    
    public Command resolveForCommand() throws CommandFormatException {
        CommandProxy root = generateCommandTree();
        if (root==null) throw new CommandFormatException("A working command proxy tree could not be generated");
        Object o = root.resolve();
        
        if (o instanceof Command) return ((Command) o);
        else throw new CommandFormatException("Did not resolve to an object of class Command");
    }
    
    /*public String getTreeString() {
        return commandRoot!=null ? commandRoot.getTreeString("") : "Cannot create tree string as no command root";
    }*/
    
    public String getCommandAsString() {
        String s = "";
        for (CommandProxy cP : commandStack) s += " "+cP.toDisplayString();
        return s+" "+workingText;
    }
}
