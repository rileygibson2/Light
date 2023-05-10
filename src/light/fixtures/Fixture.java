package light.fixtures;

import java.util.Set;

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
        if (dmxAddress!=null) pW.put(dmxAddress.getBytes());
        pW.put(profile.getFileName().getBytes());
        pW.wrapInSegment();

        return pW.getBytes();
    }

    @Override
    public void generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((profile == null) ? 0 : profile.hashCode());
        result = prime * result + ((dmxAddress == null) ? 0 : dmxAddress.hashCode());
        return result;
    }
}
