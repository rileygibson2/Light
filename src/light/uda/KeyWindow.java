package light.uda;

import light.commands.ClearCommand;
import light.commands.Command;
import light.commands.commandcontrol.CommandFormatException;
import light.commands.commandcontrol.CommandLine;
import light.commands.commandcontrol.commandproxys.CommandTypeProxy;
import light.guipackage.cli.CLI;
import light.physical.PhysicalKey;
import light.physical.PhysicalKeyBank;

public class KeyWindow implements UDACapable {
    
    
    public void keyClicked(PhysicalKey key) {
        if (key==null) return;
        CLI.debug(key+" clicked!");

        //Special cases
        if (key==PhysicalKey.PLEASE) {
            try {CommandLine.getInstance().executeCommand();}
            catch (CommandFormatException e) {
                CLI.error("Error while attempting to resolve for command after key press: "+e.toString());
            }
        }
        else if (key==PhysicalKey.CLEAR) new ClearCommand().execute();
        //Command
        else if (key.isInBank(PhysicalKeyBank.COMMAND)) {
            Class<? extends Command> com = key.getCommandClass();
            if (com==null) return;
            CommandLine.getInstance().addToCommand(new CommandTypeProxy(com));
            return;
        }
    }
}
