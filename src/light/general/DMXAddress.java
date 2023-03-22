package light.general;

public class DMXAddress extends ConsoleAddress {

    public DMXAddress(int universe, int address) {
        super(GenericAddressScope.class, universe, address);
        validate();
    }

    public int getUniverse() {return getPrefix();}
    public void setUniverse(int universe) {
        setPrefix(universe);
        validate();
    }

    public int getAddress() {return getSuffix();}
    public void setAddress(int address) {
        setSuffix(address);
        validate();
    }

    public DMXAddress getBaseUniverseAddress() {return new DMXAddress(getPrefix(), 0);}

    public void validate() {
        if (getPrefix()<0) setPrefix(0);
        if (getSuffix()<0) setSuffix(0);
        if (getSuffix()>512) setSuffix(512);
    }

    @Override
    public String toString() {return getPrefix()+"."+getSuffix();}
}
