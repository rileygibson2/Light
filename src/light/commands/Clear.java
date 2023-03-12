package light.commands;

import light.Programmer;
import light.commands.commandline.CommandLine;

public class Clear implements Command {

    public Clear() {}

    @Override
    public void execute() {
        if (!CommandLine.getInstance().isClear()) CommandLine.getInstance().clear();
        else Programmer.getInstance().clear();
    }
}
