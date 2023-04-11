package light.fixtures;

import light.general.Addressable;
import light.general.ConsoleAddress;
import light.general.DMXAddress;
import light.persistency.PersistencyCapable;
import light.persistency.PersistencyWriter;

public class Fixture extends Addressable implements PersistencyCapable {
    
    private FixtureProfile profile;
    private String name;
    private DMXAddress dmxAddress;

    public Fixture(ConsoleAddress address, FixtureProfile profile) {
        super(address);
        this.profile = profile;
    }

    public void setDMXAddress(DMXAddress ad) {this.dmxAddress = ad;}
    public DMXAddress getDMXAddress() {return dmxAddress;}

    public void setName(String n) {name = n;}
    public String getName() {return name;}

    public void setProfile(FixtureProfile profile) {
        this.profile = profile;
        PatchManager.getInstance().fixtureProfileUpdated(this);
        //TODO: Update all stores containing this fixture
    }

    public FixtureProfile getProfile() {return this.profile;}

    public boolean hasAttribute(Attribute a) {return profile.hasAttribute(a);}

    public DMXAddress getAddressForAttribute(Attribute attribute) {
        if (profile==null||!profile.hasAttribute(attribute)) return null;
        if (dmxAddress==null) return null;
        
        int i = dmxAddress.getAddress();
        for (Attribute a : profile.getAttributes()) {
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
        for (Attribute a : profile.getAttributes()) pW.put(a.getBytes());
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
