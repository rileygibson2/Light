package light.fixtures.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import light.fixtures.Attribute;
import light.fixtures.Feature;
import light.fixtures.FeatureGroup;
import light.fixtures.FixtureType;
import light.persistency.PersistencyCapable;

/**
 * Profile is collection of ProfileChannels
 * 
 * A ProfileChannel will at most only ever represent one channel of DMX
 * ProfileChannel has a feature and an attribute assignment
 * ProfileChannel contains no solid logic about data though, but has set of ProfileChannelFunctions
 * 
 * ProfileChannelFunction has range of dmx that it is applicable for within the channel
 * ProfileChannelFunction will (at this point) operate on the feature and attribute defined by the parent ProfileChannel
 * ProfileChannelFunction simply defines a specific function as a component of a channel
 * 
 * ProfileChannelFunction can have ProfileChannelMacros which codify specific named components of a function
 * These named component macros are what are seen in the calculator in the encoder bar
 * Therefore a ProfileChannelFunction is mostly an abstract extention to the ProfileChannel - if a channel
 * has a bunch of conceptually related macros like "Strobe rand 1..100" and "Strobe rand 100..0" then these will be grouped
 * under a channel function.
 * 
 * Conversly if a channel does not have multiple functions or coded macros then the channel function is simply a single other
 * layer of abstraction, in the case of a Dimmer channel in which the full dmx range from 0 through 255 all services one specific
 * function.
 */
public class Profile implements PersistencyCapable {
    
    private FixtureType type; //Type of fixture
    private String name; //Profile name
    private String modeName; //Mode name
    private String manufacturerName;
    private String fileName;

    private Set<ProfileChannel> channels;

    //Wheels
    private Set<ProfileWheel> wheels;
    
    public Profile() {
        channels = new HashSet<ProfileChannel>();
        wheels = new HashSet<ProfileWheel>();
    }
    
    public void setName(String name) {this.name = name;}
    public String getName() {return this.name;}

    public void setModeName(String name) {this.modeName = name;}
    public String getModeName() {return this.modeName;}

    public void setManufacturerName(String name) {this.manufacturerName = name;}
    public String getManufacturerName() {return this.manufacturerName;}

    public void setFixtureType(FixtureType type) {this.type = type;}
    public FixtureType getFixtureType() {return type;}

    public void setFileName(String fileName) {this.fileName = fileName;}
    public String getFileName() {return fileName;}
    
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

    public List<Attribute> getAttributeList() {
        List<Attribute> attributes = new ArrayList<>();
        for (ProfileChannel c : channels) attributes.add(c.getAttribute());
        Collections.sort(attributes);
        return attributes;
    }

    public boolean hasAttributeOfFeatureGroup(FeatureGroup group) {
        if (group==null) return false;
        for (ProfileChannel c : channels) {
            if (c.getFeature().getFeatureGroup()==group) return true;
        }
        return false;
    }

    public ProfileChannel getChannelWithAttribute(Attribute attribute) {
        if (channels==null) return null;
        for (ProfileChannel c : channels) {
            if (c.getAttribute().equals(attribute)) return c;
        }
        return null;
    }

    public List<ProfileChannel> getChannelsWithFeatureGroup(FeatureGroup group) {
        if (channels==null) return null;
        List<ProfileChannel> result = new ArrayList<>();

        for (ProfileChannel c : channels) {
            if (c.getAttribute().getFeature().getFeatureGroup()==group) result.add(c);
        }
        Collections.sort(result);
        return result;
    }
    
    public Set<ProfileChannel> getChannels() {return channels;}
    
    public void addChannel(ProfileChannel c) {
        channels.add(c);
        c.setProfile(this);
    }

    public boolean hasChannel(ProfileChannel c) {return channels.contains(c);}

    public void addWheel(ProfileWheel wheel) {
        wheels.add(wheel);
        wheel.setProfile(this);
    }

    public double valueToDMX(Attribute attribute, double value) {
        ProfileChannel channel = getChannelWithAttribute(attribute);
        if (channel==null) return -1;
        ProfileChannelFunction function = channel.getFunctionForValue(value);
        if (function==null) return -1;
        return function.valueToDMX(value);
    }

    public double valueAsPercOfRange(Attribute attribute, double value) {
        ProfileChannel channel = getChannelWithAttribute(attribute);
        if (channel==null) return -1;
        ProfileChannelFunction function = channel.getFunctionForValue(value);
        if (function==null) return -1;
        return function.valueAsPercOfRange(value);
    }

    public boolean hasWheel(int wheelIndex) {
        for (ProfileWheel wheel: wheels) if (wheel.getIndex()==wheelIndex) return true;
        return false;
    }

    public boolean hasWheelAndSlot(int wheelIndex, int slotIndex) {
        for (ProfileWheel wheel: wheels) {
            if (wheel.getIndex()==wheelIndex&&wheel.hasSlot(slotIndex)) return true;
        }
        return false;
    }

    public ProfileWheel getWheel(int wheelIndex) {
        for (ProfileWheel wheel: wheels) if (wheel.getIndex()==wheelIndex) return wheel;
        return null;
    }

    public ProfileWheelSlot getSlot(int wheelIndex, int slotIndex) {
        for (ProfileWheel wheel: wheels) {
            if (wheel.getIndex()==wheelIndex) return wheel.getSlot(slotIndex);
        }
        return null;
    }

    public ProfileWheelSlot getSlot(ProfileChannelMacro macro) {
        if (macro==null||!macro.hasSlotIndex()||!macro.getFunction().hasWheelIndex()) return null;
        return getSlot(macro.getFunction().getWheelIndex(), macro.getSlotIndex());
    }

    /**
     * Validates this profile and all it's subcomponents are valid - i.e they contain all the
     * required nessacary data to function properly as a Profile. Due to the lazy loading of data
     * into this object there is possibility for Profile to be parsed from file and crucial data 
     * not be present in the file, so parser calls this method after finishing parse to check Profile
     * is complete.
     * 
     * @return
     */
    public boolean validate() {
        if (type==null||name==null||modeName==null||manufacturerName==null||fileName==null) return false;
        for (ProfileChannel channel : channels) if (!channel.validate()) return false;
        for (ProfileWheel wheel : wheels) if (!wheel.validate()) return false;
        return true;
    }

    public String toFullString() {
        String result = "\n["+manufacturerName+" "+name+": mode="+modeName+", type="+type+"]";
        for (ProfileChannel channel : channels) result += "\n\t"+channel.toProfileString("\t");
        for (ProfileWheel wheel : wheels) result += "\n\t"+wheel.toProfileString("\t");
        return result;
    }

    @Override
    public String toString() {
        return "["+manufacturerName+" "+name+": mode="+modeName+", type="+type+"]";
    }
    
    @Override
    public byte[] getBytes() {return null;}
    
    public static Profile generateFromBytes(byte[] bytes) {return null;}
}
