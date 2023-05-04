package light.fixtures.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import light.fixtures.Attribute;
import light.fixtures.Feature;
import light.general.Utils;
import light.stores.Preset.PresetType;

public class ProfileChannel extends ProfileElement {

    private Feature feature;
    private Attribute attribute;
    private String attributeUserName;

    //User values
    private double minValue;
    private double maxValue;
    private double defaultValue;
    private double userSetHighlightValue;

    private PresetType presetType;
    
    private List<ProfileChannelFunction> functions;

    public ProfileChannel() {
        super();
        functions = new ArrayList<ProfileChannelFunction>();
        minValue = 0;
        maxValue = 100;
        userSetHighlightValue = Double.MAX_VALUE;
        defaultValue = 0;
    }
    
    public void addFunction(ProfileChannelFunction f) {
        functions.add(f);
        f.setChannel(this);
        f.setProfile(profile);
        Collections.sort(functions);
    }

    public boolean hasFunction(ProfileChannelFunction f) {return functions.contains(f);}

    public List<ProfileChannelFunction> getFunctions() {
        Collections.sort(functions);
        return functions;
    }

    public ProfileChannelFunction getFunctionForValue(double value) {
        for (ProfileChannelFunction function : functions) {
            if (function.valueInRange(value)) return function;
        }
        return null;
    }

    public ProfileChannelFunction getFunctionForDMX(double dmx) {
        for (ProfileChannelFunction function : functions) {
            if (function.dmxInRange(dmx)) return function;
        }
        return null;
    }

    public List<ProfileChannelMacro> getAllMacros() {
        Collections.sort(functions);
        List<ProfileChannelMacro> macros = new ArrayList<>();
        for (ProfileChannelFunction function : functions) {
            macros.addAll(function.getMacros());
        }
        return macros;
    }
    
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
    public String getAttributeUserName() {
        if (attributeUserName==null) return attribute.getUserName();
        return attributeUserName;
    }

    public void setPresetType(PresetType type) {this.presetType = type;}
    public PresetType getPresetType() {return this.presetType;}

    public void setMinValue(double v) {this.minValue = v;}
    public double getMinValue() {return minValue;}

    public void setMaxValue(double v) {this.maxValue = v;}
    public double getMaxValue() {return maxValue;}

    public void setDefaultValue(double v) {this.defaultValue = v;}
    public double getDefaultValue() {return this.defaultValue;}

    public void setHighlightValue(double d) {this.userSetHighlightValue = d;}
    public double getHighlightValue() {
        if (userSetHighlightValue==Double.MAX_VALUE) return getMaxValue(); //No user set value
        return userSetHighlightValue;
    }

    public boolean valueInRange(double value) {
        return value>=minValue&&value<=maxValue;
    }

    public double dmxToValue(double dmx) {
        if (!Utils.validateDMX(dmx)) return -1;
        for (ProfileChannelFunction function : functions) {
            if (function.dmxInRange(dmx)) return function.dmxToValue(dmx);
        }
        return -1;
    }

    public int valueToDMX(double value) {
        if (!valueInRange(value)) return -1;
        for (ProfileChannelFunction function : functions) {
            if (function.valueInRange(value)) return function.valueToDMX(value);
        }
        return -1;
    }

    public boolean validate() {
        if (profile==null||index==-1||feature==null||attribute==null) return false;
        for (ProfileChannelFunction function : functions) if (!function.validate()) return false;
        return true;
    }

    public String toProfileString(String indent) {
        String result = "[Channel: feature="+feature+", attribute="+attribute+", preset="+presetType+" min_value="+minValue+" maxValue="+maxValue+"]";
        indent += "\t";

        for (ProfileChannelFunction function : functions) result += "\n"+indent+function.toProfileString(indent);
        return result;
    }
}
