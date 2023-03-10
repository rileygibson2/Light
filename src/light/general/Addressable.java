package light.general;

public abstract class Addressable {
    
    private ConsoleAddress address;

    public Addressable(ConsoleAddress address) {
        this.address = address;
    }

    public ConsoleAddress getAddress() {return address;}

    public void setAddress(ConsoleAddress address) {this.address = address;}

    public boolean hasAddress() {return address!=null;}
}
