package light.commands.commandline;

import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import light.commands.Command;
import light.commands.commandline.CommandProxy.Operator;
import light.guipackage.cli.CLI;
import light.guipackage.general.Submitter;
import light.guipackage.gui.GUI;
import light.guipackage.gui.IO;
import light.uda.guiinterfaces.CommandLineGUIInterface;

public class CommandLine {

    private static CommandLine singleton;

    private CommandLineGUIInterface gui;
    private Map<Class<? extends Command>, String> registeredCommands; //Register of currently active commands that should be parsed
    
    private CommandProxy commandRoot;
    private ArrayDeque<CommandProxy> commandStack; //Working stack of all parts of this command
    private String workingText; //Used for typed input, will fill up until a command proxy type can be extracted from it

    private CommandLine() {
        gui = (CommandLineGUIInterface) GUI.getInstance().addToGUI(this);
        commandRoot = null;
        commandStack = new ArrayDeque<CommandProxy>();
        registeredCommands = new HashMap<Class<? extends Command>, String>();
        
        IO.getInstance().registerKeyListener(this, new Submitter<KeyEvent>() {
            @Override
            public void submit(KeyEvent s) {keyPressed(s);}
        });
    }

    public static CommandLine getInstance() {
        if (singleton==null) singleton = new CommandLine();
        return singleton;
    }

    public boolean isClear() {return commandRoot==null;}

    public void clear() {
        commandRoot = null;
        commandStack.clear();
    }

    /**
     * Register command class with command line using the class's simple name as the name mapping
     * @param s
     * @param c
     */
    public void registerCommand(Class<? extends Command> c) {registeredCommands.put(c, c.getSimpleName());}

    /**
     * Register command class with command line with a custom mapping name
     * @param s
     * @param c
     */
    public void registerCommand(Class<? extends Command> c, String s) {registeredCommands.put(c, s);}

    public void deregisterCommand(Class<? extends Command> c) {registeredCommands.remove(c);}

    /**
     * Used to add a component to this command
     * @param cP
     */
    public void addToCommand(CommandProxy cP) {
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
        
        if (lastNonTerminal==null) { //Start new command
            commandRoot = cP;
            commandStack.push(cP);
            return;
        }

        //+ and thru edge case
        if (cP.getResolveType()==List.class) {
            CommandProxy firstArg = commandStack.peek();
            cP.addArgument(firstArg);
            if (!cP.isTerminal()) commandStack.push(cP);
        }
        else { //General case
            lastNonTerminal.addArgument(cP);
            commandStack.push(cP);
        }
    }

    public void executeCommand() {
        Object o = null;
        try {o = commandRoot.resolve();}
        catch (CommandFormatException e) {
            CLI.error(e.toString());
            return;
        }

        if (o instanceof Command) ((Command) o).execute();
        else CLI.error("Root command proxy did not resolve to a command");
    }

    private void keyPressed(KeyEvent e) {
        CLI.debug("AA "+e.getExtendedKeyCode());
        workingText += KeyEvent.getKeyText(e.getExtendedKeyCode());

        //Parse working text to see if command proxy can be formed
        CommandProxy parsed = null;

        //Check for command
        for (Map.Entry<Class<? extends Command>, String> c : registeredCommands.entrySet()) {
            if (workingText.toLowerCase().equals(c.getValue().toLowerCase())) {
                parsed = new CommandProxy(c.getKey());
            }
        }

        //Check for operator
        for (Operator o : Operator.values()) {
            if (workingText.toLowerCase().equals(o.toString().toLowerCase())) {
                parsed = new CommandProxy(o);
            }
        }

        //Check for value
        Double d;
        try {
            d = Double.parseDouble(workingText);
            parsed = new CommandProxy(d);
        }
        catch (NumberFormatException ex) {}

        if (parsed!=null) {
            //Add to tree
            addToCommand(parsed);
        }
    }

    @Override
    public String toString() {
        if (commandRoot==null) return "";
        String s = commandStringDFS(commandRoot);
        return s+workingText;
    }

    private String commandStringDFS(CommandProxy cP) {
        String s = cP.toString();
        for (CommandProxy child : cP.getArguments()) {
            if (child!=null) s += commandStringDFS(child);
        }
        return s;
    }
}
