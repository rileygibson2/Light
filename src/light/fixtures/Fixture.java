package light.fixtures;

import light.fixtures.profile.Profile;
import light.fixtures.profile.ProfileChannel;
import light.general.Addressable;
import light.general.ConsoleAddress;
import light.general.DMXAddress;
import light.persistency.PersistencyCapable;
import light.persistency.PersistencyWriter;

public class Fixture extends Addressable implements PersistencyCapable {
    
    private Profile profile;
    private DMXAddress dmxAddress;

    public Fixture(ConsoleAddress address, Profile profile) {
        super(address);
        this.profile = profile;
    }

    public void setDMXAddress(DMXAddress ad) {this.dmxAddress = ad;}
    public DMXAddress getDMXAddress() {return dmxAddress;}

    public void setProfile(Profile profile) {
        this.profile = profile;
        PatchManager.getInstance().fixtureProfileUpdated(this);
        //TODO: Update all stores containing this fixture
    }

    public Profile getProfile() {return this.profile;}

    public boolean hasAttribute(Attribute a) {return profile.hasAttribute(a);}

    public DMXAddress getAddressForChannel(ProfileChannel channel) {
        if (dmxAddress==null||profile==null||!profile.hasChannel(channel)) return null;

        return new DMXAddress(dmxAddress.getUniverse(), channel.getIndex()+1);
    }

    public DMXAddress getAddressForAttribute(Attribute attribute) {
        if (dmxAddress==null||profile==null||!profile.hasAttribute(attribute)) return null;
       
        ProfileChannel channel = profile.getChannelWithAttribute(attribute);
        if (channel==null) return null;
        return new DMXAddress(dmxAddress.getUniverse(), channel.getIndex()+1);
    }

    @Override
    public String toString() {
        return "\n[Fixture: name="+getLabel()+", dmx="+dmxAddress+", profile="+profile.toString()+"]";
    }

    @Override
    public byte[] getBytes() {
        PersistencyWriter pW = new PersistencyWriter();

        //Name and dmx address
        pW.put(getLabel().getBytes());
        pW.put(dmxAddress.getBytes());

        //Attributes
        pW.openSegment();
        //for (Feature a : profile.getAttributes()) pW.put(a.getBytes());
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
