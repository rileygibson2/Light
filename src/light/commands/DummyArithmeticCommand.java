package light.commands;

public class DummyArithmeticCommand implements Command {

    private double d;

    public DummyArithmeticCommand(double d) {
        this.d = d;
    }

    @Override
    public void execute() {}
    
    public double getDouble() {return d;}

}
