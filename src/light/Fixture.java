package light;

import java.util.List;

import light.general.Attribute;
import light.general.ConsoleAddress;
import light.general.DMXAddress;

public class Fixture {
    
    private ConsoleAddress id;
    private String name;
    private DMXAddress address;
    private List<Attribute> attributes;

    public Fixture(ConsoleAddress id) {
        this.id = id;
    }

    public void setAddress(DMXAddress ad) {this.address = ad;}
    public DMXAddress getAddress() {return address;}

    public ConsoleAddress getID() {return id;}
    public void setID(ConsoleAddress id) {this.id = id;}

    public void setName(String n) {name = n;}
    public String getName() {return name;}

    public boolean hasAttribute(Attribute attribute) {
        if (attributes==null) return false;
        return attributes.contains(attribute);
    }

    public DMXAddress getAddressForAttribute(Attribute attribute) {
        if (attributes==null||!attributes.contains(attribute)) return null;
        if (address==null) return null;

        DMXAddress result = null;
        int i = address.getAddress();
        for (Attribute a : attributes) {
            if (a==attribute) return new DMXAddress(address.getUniverse(), i);
            i++;
        }
        return null;
    }
}
