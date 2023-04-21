package light.commands;

import light.Programmer;
import light.commands.commandcontrol.CommandLine;

public class Clear implements Command {

    public Clear() {}

    @Override
    public void execute() {
        if (!CommandLine.getInstance().getCommandController().isEmpty()) CommandLine.getInstance().getCommandController().clear();
        else Programmer.getInstance().clear();
    }
}
