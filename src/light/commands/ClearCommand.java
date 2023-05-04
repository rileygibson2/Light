package light.commands;

import light.Programmer;
import light.commands.commandcontrol.CommandLine;

public class ClearCommand implements Command {

    public ClearCommand() {}

    @Override
    public void execute() {
        if (!CommandLine.getInstance().isEmpty()) CommandLine.getInstance().clear();
        else if (Programmer.getInstance().hasSelectedFixtures()) Programmer.getInstance().clearSelected();
        else Programmer.getInstance().clear();
    }
}
