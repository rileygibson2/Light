package light.zones.commandline;

import light.commands.Command;

public class CommandProxy {

    public Command com;
    public Object value;

    public CommandProxy(Command com, Object value) {
        this.com = com;
        this.value = value;
    }
}
