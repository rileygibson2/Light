package light.fixtures.profile;

import java.util.HashSet;
import java.util.Set;

import light.fixtures.Attribute;
import light.fixtures.Feature;
import light.general.Utils;
import light.stores.Preset.PresetType;

public class ProfileChannel {
    
    private int index;

    private Feature feature;
    private Attribute attribute;
    private String attributeUserName;

    private Set<ProfileChannelMacro> macros; //Macros to codify specific functions within this channel's DMX range

    private PresetType presetType;
    //User values
    private double minValue;
    private double maxValue;
    //DMX values
    private double minDMX;
    private double maxDMX;
    private double highlightDMX;
    
    public ProfileChannel() {
        macros = new HashSet<ProfileChannelMacro>();
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
        if (!attribute.verify(feature)) attribute = null;
    }
    public Feature getFeature() {return feature;}

    public boolean setAttribute(Attribute attribute) {
        if (!attribute.verify(feature)) return false;
        this.attribute = attribute;
        return true;
    }
    public Attribute getAttribute() {return attribute;}

    public void addChannelMacro(ProfileChannelMacro macro) {
        macros.add(macro);
    }

    public void setIndex(int index) {this.index = index;}
    public int getIndex() {return index;}

    public void setAttributeUserName(String name) {this.attributeUserName = name;}
    public String getAttributeUserName() {return attributeUserName;}

    public void setPresetType(PresetType type) {this.presetType = type;}
    public PresetType getPresetType() {return this.presetType;}

    public void setMinValue(double v) {this.minValue = v;}
    public double getMinValue() {return minValue;}

    public void setMaxValue(double v) {this.maxValue = v;}
    public double getMaValue() {return maxValue;}

    public void setMinDMX(double d) {this.minDMX = d;}
    public double getMinDMX() {return minDMX;}

    public void setMaxDMX(double d) {this.maxDMX = d;}
    public double getMaxDMX() {return maxDMX;}

    public void setHighlightDMX(double d) {this.highlightDMX = d;}
    public double getHighlightDMX() {return highlightDMX;}

    public double dmxToValue(double dmx) {
        if (!Utils.validateDMX(dmx)) return Double.MAX_VALUE;
        return (dmx/255)*(maxValue-minValue);
    }
}
