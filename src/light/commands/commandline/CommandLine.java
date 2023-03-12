package light.commands.commandline;

import java.awt.event.KeyEvent;
import java.util.Map;

import guipackage.cli.CLI;
import guipackage.general.Submitter;
import guipackage.gui.GUI;
import guipackage.gui.IO;
import light.commands.Command;
import light.commands.commandline.CommandProxy.Operator;
import light.uda.guiinterfaces.CommandLineInterface;

public class CommandLine {

    private static CommandLine singleton;

    private CommandLineInterface gui;
    private Map<Class<? extends Command>, String> registeredCommands; //Register of currently active commands that should be parsed
    
    private CommandProxy commandRoot;
    private CommandProxy lastCommandProxy; //Used for building command
    private String workingText; //Used for typed input, will fill up until a command proxy type can be extracted

    private CommandLine() {
        gui = (CommandLineInterface) GUI.getInstance().addToGUI(this);
        commandRoot = null;
        
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

    public void clear() {commandRoot = null;}

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
     * Used to add a part to this command
     * @param cP
     */
    public void addToCommand(CommandProxy cP) {
        
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
        for (CommandProxy child : cP.getArgs()) {
            if (child!=null) s += commandStringDFS(child);
        }
        return s;
    }
}
