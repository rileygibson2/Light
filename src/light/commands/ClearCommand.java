package light.commands;

import light.Programmer;
import light.commands.commandcontrol.CommandLine;

public class ClearCommand implements Command {

    public ClearCommand() {}

    @Override
    public void execute() {
        if (!CommandLine.getInstance().getCommandController().isEmpty()) CommandLine.getInstance().getCommandController().clear();
        else Programmer.getInstance().clear();
    }
}
