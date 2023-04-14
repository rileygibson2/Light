package light.fixtures.profile;

import java.util.HashSet;
import java.util.Set;

import light.fixtures.Attribute;
import light.fixtures.Feature;
import light.stores.Preset.PresetType;

public class ProfileChannel {
    
    private Profile parent;
    private int index;

    private Feature feature;
    private Attribute attribute;
    private String attributeUserName;

    private double highlightDMX;
    private PresetType presetType;
    
    private Set<ProfileChannelFunction> functions;

    public ProfileChannel() {
        index = -1;
        functions = new HashSet<ProfileChannelFunction>();
    }

    public void setParent(Profile parent) {this.parent = parent;}
    public Profile getParent() {return parent;}

    public void setIndex(int index) {this.index = index;}
    public int getIndex() {return index;}

    public Set<ProfileChannelFunction> getFunctions() {return functions;}
    
    public void addFunction(ProfileChannelFunction f) {
        functions.add(f);
        f.setParent(this);
    }

    public boolean hasFunction(ProfileChannelFunction f) {return functions.contains(f);}
    
    public void setFeature(Feature feature) {
        this.feature = feature;
        if (attribute!=null&&!attribute.verify(feature)) attribute = null;
    }
    public Feature getFeature() {return feature;}

    public boolean setAttribute(Attribute attribute) {
        if (feature==null||!attribute.verify(feature)) return false;
        this.attribute = attribute;
        return true;
    }
    public Attribute getAttribute() {return attribute;}

    public void setAttributeUserName(String name) {this.attributeUserName = name;}
    public String getAttributeUserName() {return attributeUserName;}

    public void setPresetType(PresetType type) {this.presetType = type;}
    public PresetType getPresetType() {return this.presetType;}

    public void setHighlightDMX(double d) {this.highlightDMX = d;}
    public double getHighlightDMX() {return highlightDMX;}

    public ProfileChannelFunction getFunctionForValue(double value) {
        for (ProfileChannelFunction function : functions) {
            if (function.inValueRange(value)) return function;
        }
        return null;
    }

    public ProfileChannelFunction getFunctionForDMX(double dmx) {
        for (ProfileChannelFunction function : functions) {
            if (function.inDMXRange(dmx)) return function;
        }
        return null;
    }

    public boolean validate() {
        if (parent==null||index==-1||feature==null||attribute==null) return false;
        for (ProfileChannelFunction function : functions) if (!function.validate()) return false;
        return true;
    }

    public String toProfileString(String indent) {
        String result = "[Channel: feature="+feature+", attribute="+attribute+", preset="+presetType+"]";
        indent += "\t";

        for (ProfileChannelFunction function : functions) result += "\n"+indent+function.toProfileString(indent);
        return result;
    }
}
