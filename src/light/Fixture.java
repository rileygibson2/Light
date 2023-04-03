package light;

import java.util.List;

import light.general.Addressable;
import light.general.Attribute;
import light.general.ConsoleAddress;
import light.general.DMXAddress;
import light.persistency.Persistency;
import light.persistency.PersistencyCapable;
import light.persistency.PersistencyWriter;

public class Fixture extends Addressable implements PersistencyCapable {
    
    private String name;
    private DMXAddress dmxAddress;
    private List<Attribute> attributes;

    public Fixture(ConsoleAddress address) {
        super(address);
    }

    public void setDMXAddress(DMXAddress ad) {this.dmxAddress = ad;}
    public DMXAddress getDMXAddress() {return dmxAddress;}

    public void setName(String n) {name = n;}
    public String getName() {return name;}

    public boolean hasAttribute(Attribute attribute) {
        if (attributes==null) return false;
        return attributes.contains(attribute);
    }

    public List<Attribute> getAttributes() {return attributes;}

    public DMXAddress getAddressForAttribute(Attribute attribute) {
        if (attributes==null||!attributes.contains(attribute)) return null;
        if (dmxAddress==null) return null;
        
        int i = dmxAddress.getAddress();
        for (Attribute a : attributes) {
            if (a==attribute) return new DMXAddress(dmxAddress.getUniverse(), i);
            i++;
        }
        return null;
    }

    @Override
    public byte[] getBytes() {
        PersistencyWriter pW = new PersistencyWriter();

        //Name and dmx address
        pW.put(getName().getBytes());
        pW.put(dmxAddress.getBytes());

        //Attributes
        pW.openSegment();
        for (Attribute a : attributes) {
            pW.put(a.getBytes());
        }
        pW.closeSegmenet();
        pW.wrapInSegment();

        return pW.toArray();
        
    }

    @Override
    public void generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
