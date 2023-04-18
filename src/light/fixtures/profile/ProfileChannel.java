package light.fixtures.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

    //User values
    private double minValue;
    private double maxValue;

    private double highlightDMX;
    private PresetType presetType;
    
    private List<ProfileChannelFunction> functions;

    public ProfileChannel() {
        index = -1;
        functions = new ArrayList<ProfileChannelFunction>();
        minValue = 0;
        maxValue = 100;
    }

    public void setParent(Profile parent) {this.parent = parent;}
    public Profile getParent() {return parent;}

    public void setIndex(int index) {this.index = index;}
    public int getIndex() {return index;}
    
    public void addFunction(ProfileChannelFunction f) {
        functions.add(f);
        f.setParent(this);
        sortFunctions();
    }

    public boolean hasFunction(ProfileChannelFunction f) {return functions.contains(f);}

    public List<ProfileChannelFunction> getFunctions() {
        sortFunctions();
        return functions;
    }

    public ProfileChannelFunction getFunctionForValue(double value) {
        for (ProfileChannelFunction function : functions) {
            if (function.valueValidForRange(value)) return function;
        }
        return null;
    }

    public ProfileChannelFunction getFunctionForDMX(double dmx) {
        for (ProfileChannelFunction function : functions) {
            if (function.inDMXRange(dmx)) return function;
        }
        return null;
    }

    private void sortFunctions() {
        Collections.sort(functions, new Comparator<ProfileChannelFunction>() {
            @Override
            public int compare(ProfileChannelFunction o1, ProfileChannelFunction o2) {
                return (int) (o1.getIndex()-o2.getIndex());
            }
        });
    }

    public List<ProfileChannelMacro> getAllMacros() {
        sortFunctions();
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

    public void setHighlightDMX(double d) {this.highlightDMX = d;}
    public double getHighlightDMX() {return highlightDMX;}

    public boolean valueValidForRange(double value) {
        return value>=minValue&&value<=maxValue;
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
