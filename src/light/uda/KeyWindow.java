package light.uda;

import light.commands.Command;
import light.commands.commandcontrol.CommandFormatException;
import light.commands.commandcontrol.CommandLine;
import light.commands.commandcontrol.CommandProxy;
import light.guipackage.cli.CLI;
import light.physical.PhysicalKey;
import light.physical.PhysicalKeyBank;

public class KeyWindow implements UDACapable {
    
    
    public void keyClicked(PhysicalKey key) {
        if (key==null) return;

        //Special cases
        if (key==PhysicalKey.PLEASE) {
            try {CommandLine.getInstance().getCommandController().executeCommand();}
            catch (CommandFormatException e) {
                CLI.error("Error while attempting to resolve for command after key press: "+e.toString());
            }
        }
        else if (key==PhysicalKey.CLEAR) {
            CommandLine.getInstance().getCommandController().clear();
        }
        //Command
        else if (key.isInBank(PhysicalKeyBank.COMMAND)) {
            CLI.debug(key+" clicked!");
            Class<? extends Command> com = key.getCommandClass();
            if (com==null) return;
            CommandLine.getInstance().getCommandController().addToCommand(new CommandProxy(com));
            return;
        }
    }
}
