package light.general;

public class DMXAddress extends ConsoleAddress {
    private int universe;
    private int address;

    public DMXAddress(int universe, int address) {
        super(GenericAddressScope.class, universe, address);
        this.universe = universe;
        this.address = address;
        validate();
    }

    public int getUniverse() {return universe;}
    public void setUniverse(int universe) {
        this.universe = universe;
        validate();
    }

    public int getAddress() {return address;}
    public void setAddress(int address) {
        this.address = address;
        validate();
    }

    public void validate() {
        if (universe<0) throw new Error("Universe cannot be less than 0");
        if (address<0||address>512) throw new Error("Invalid address - "+address);
    }

    @Override
    public String toString() {return universe+"."+address;}
}
