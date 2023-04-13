package light.fixtures.profile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import light.fixtures.Attribute;
import light.fixtures.Feature;
import light.persistency.PersistencyCapable;

/**
 * Profile has collection of ProfileChannels
 * ProfileChannels can be a singular super attribute (as in with dimmer)
 * Or they may be a sub attribute of a super attribute (as in COLORRGB1 is sub to COLORRGB)
 * 
 * ProfileChannels may also have a number of ProfileChannelMacros which define specific things at
 * certain points in the dmx range
 * 
 * But a ProfileChannel will at most ever only represent a single DMX channel.
 * Note: this may be expanded later where another layer of abstraction may be added, hence the min and max
 * DMX fields in the ProfileChannel class
 */
public class Profile implements PersistencyCapable {
    
    private Set<ProfileChannel> channels;
    
    private String name; //Profile name
    private String modeName; //Mode name
    private String manufacturerName;
    
    public Profile() {
        channels = new HashSet<ProfileChannel>();
    }
    
    public void setName(String name) {this.name = name;}
    public String getName() {return this.name;}

    public void setModeName(String name) {this.modeName = name;}
    public String getModeName() {return this.modeName;}

    public void setManufacturerName(String name) {this.manufacturerName = name;}
    public String getManufacturerName() {return this.manufacturerName;}
    
    public boolean hasFeature(Feature feature) {
        if (channels==null) return false;
        for (ProfileChannel c : channels) {
            if (c.getFeature().equals(feature)) return true;
        }
        return false;
    }

    public Set<Feature> getFeatureSet() {
        Set<Feature> features = new HashSet<>();
        for (ProfileChannel c : channels) {
            if (!features.contains(c.getFeature())) features.add(c.getFeature());
        }
        return features;
    }

    public boolean hasAttribute(Attribute attribute) {
        if (channels==null) return false;
        for (ProfileChannel c : channels) {
            if (c.getAttribute().equals(attribute)) return true;
        }
        return false;
    }

    public Set<Attribute> getAttributeSet() {
        Set<Attribute> attributes = new HashSet<>();
        for (ProfileChannel c : channels) attributes.add(c.getAttribute());
        return attributes;
    }

    public ProfileChannel getChannelWithAttribute(Attribute attribute) {
        if (channels==null) return null;
        for (ProfileChannel c : channels) {
            if (c.getAttribute().equals(attribute)) return c;
        }
        return null;
    }
    
    public Set<ProfileChannel> getChannels() {return channels;}
    
    public void addChannel(ProfileChannel c) {channels.add(c);}
    
    public void addChannels(Collection<ProfileChannel> a) {channels.addAll(a);}

    public boolean hasChannel(ProfileChannel c) {return channels.contains(c);}

    @Override
    public String toString() {
        return "["+manufacturerName+" "+name+": "+modeName+"]";
    }
    
    @Override
    public byte[] getBytes() {return null;}
    
    @Override
    public void generateFromBytes(byte[] bytes) {}
}
