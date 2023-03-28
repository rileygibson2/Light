package light.commands;

public interface Command {

    public void execute();
}

class InvalidCommandArgumentException extends Exception {
    public InvalidCommandArgumentException() {}

    public InvalidCommandArgumentException(String message) {
        super(message);
    }
}
