package light.commands.commandcontrol;

import java.awt.event.KeyEvent;

import light.Light;
import light.general.Submitter;
import light.guipackage.cli.CLI;
import light.guipackage.gui.IO;
import light.uda.guiinterfaces.CommandLineGUIInterface;
import light.uda.guiinterfaces.GUIInterface;

public class CommandLine extends CommandController {

    private static CommandLine singleton;

    private CommandLine() {
        acceptAllCommands(true);
        setCommandUpdatedAction(() -> {
            GUIInterface gui = Light.getInstance().getStaticGUIElement(CommandLineGUIInterface.class);
            if (gui!=null) {
                ((CommandLineGUIInterface) gui).setCommandString(getCommandAsString());
            }
        });

        IO.getInstance().registerKeyListener(this, new Submitter<KeyEvent>() {
            @Override
            public void submit(KeyEvent s) {keyPressed(s);}
        });
    }

    public static CommandLine getInstance() {
        if (singleton==null) singleton = new CommandLine();
        return singleton;
    }

    private void keyPressed(KeyEvent e) {
        CLI.debug("Commandline recieved "+e.getExtendedKeyCode());
        updateWorkingText(KeyEvent.getKeyText(e.getExtendedKeyCode()));
        
        //Check for command
        /*for (Map.Entry<Class<? extends Command>, String> c : registeredCommands.entrySet()) {
            if (controller.getWorkingText().toLowerCase().equals(c.getValue().toLowerCase())) {
                controller.addToCommand(new CommandProxy(c.getKey()));
                controller.clearWorkingText();
            }
        }*/
    }
}
