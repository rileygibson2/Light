package light.commands.commandcontrol;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import light.commands.Command;
import light.guipackage.cli.CLI;
import light.guipackage.general.Submitter;
import light.guipackage.gui.IO;

public class CommandLine {

    private static CommandLine singleton;

    private Map<Class<? extends Command>, String> registeredCommands; //Register of currently active commands that should be parsed
    private CommandController controller;

    private CommandLine() {
        registeredCommands = new HashMap<Class<? extends Command>, String>();
        controller = new CommandController();

        IO.getInstance().registerKeyListener(this, new Submitter<KeyEvent>() {
            @Override
            public void submit(KeyEvent s) {keyPressed(s);}
        });
    }

    public static CommandLine getInstance() {
        if (singleton==null) singleton = new CommandLine();
        return singleton;
    }

    public CommandController getCommandController() {return controller;}

    public void executeCommand() {
        Command c = null;
        try {c = controller.resolveForCommand();}
        catch (CommandFormatException e) {
            CLI.error("Error while attempting to resolve for command: "+e.toString());
        }
        if (c!=null) c.execute();
        controller.clear();
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

    private void keyPressed(KeyEvent e) {
        CLI.debug("Commandline recieved "+e.getExtendedKeyCode());
        controller.updateWorkingText(KeyEvent.getKeyText(e.getExtendedKeyCode()));
        
        //Check for command
        for (Map.Entry<Class<? extends Command>, String> c : registeredCommands.entrySet()) {
            if (controller.getWorkingText().toLowerCase().equals(c.getValue().toLowerCase())) {
                controller.addToCommand(new CommandProxy(c.getKey()));
                controller.clearWorkingText();
            }
        }
    }
}
