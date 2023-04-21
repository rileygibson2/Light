package light.fixtures.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import light.general.Utils;

public class ProfileChannelFunction extends ProfileElement {
    
    private ProfileChannel parent;
    private String name; //Name for this subattribute function
    private List<ProfileChannelMacro> macros; //Macros to codify specific functions within this channel's DMX range

    //User values
    private double minValue;
    private double maxValue;
    //DMX values
    private double minDMX;
    private double maxDMX;
    //Wheel mappings
    private int wheelIndex;

    public ProfileChannelFunction() {
        super();
        minDMX = -1;
        maxDMX = -1;
        minValue = Double.MIN_VALUE;
        maxValue = Double.MAX_VALUE;
        wheelIndex = -1;
        macros = new ArrayList<ProfileChannelMacro>();
    }

    public void setChannel(ProfileChannel parent) {
        this.parent = parent;
        //Set value bounds off of parents if not already defined
        if (minValue==Double.MIN_VALUE) setMinValue(parent.getMinValue());
        if (maxValue==Double.MAX_VALUE) setMaxValue(parent.getMaxValue());
    }
    public ProfileChannel getChannel() {return parent;}
    
    public void addMacro(ProfileChannelMacro m) {
        macros.add(m);
        m.setFunction(this);
        m.setProfile(profile);
        Collections.sort(macros);
    }

    public boolean hasMacro(ProfileChannelMacro m) {return macros.contains(m);}

    public List<ProfileChannelMacro> getMacros() {
        Collections.sort(macros);
        return macros;
    }

    public ProfileChannelMacro getMacroForDMX(double dmx) {
        if (!Utils.validateDMX(dmx)||!dmxInRange(dmx)) return null;
        for (ProfileChannelMacro macro : macros) if (macro.dmxInRange(dmx)) return macro;
        return null;
    }

    public ProfileChannelMacro getMacroForValue(double value) {
        if (!valueInRange(value)) return null;
        double dmx = valueToDMX(value);
        for (ProfileChannelMacro macro : macros) if (macro.dmxInRange(dmx)) return macro;
        return null;
    }

    public void setName(String name) {this.name = name;}
    public String getName() {
        if (name!=null) return name;
        else return ((ProfileChannel) parent).getAttributeUserName();
    }

    public void setMinValue(double v) {this.minValue = v;}
    public double getMinValue() {return minValue;}

    public void setMaxValue(double v) {this.maxValue = v;}
    public double getMaxValue() {return maxValue;}

    public void setMinDMX(double d) {this.minDMX = d;}
    public double getMinDMX() {return minDMX;}

    public void setMaxDMX(double d) {this.maxDMX = d;}
    public double getMaxDMX() {return maxDMX;}

    public void setWheelIndex(int wheel) {this.wheelIndex = wheel;}
    public boolean hasWheelIndex() {return wheelIndex!=-1;}
    public int getWheelIndex() {return wheelIndex;}

    public ProfileWheel getWheelMapping() {
        return hasWheelIndex() ? parent.getProfile().getWheel(wheelIndex) : null;
    }

    public boolean valueInRange(double value) {
        return value>=minValue&&value<=maxValue;
    }

    public boolean dmxInRange(double dmx) {
        return dmx>=minDMX&&dmx<=maxDMX;
    }

    public double dmxToValue(double dmx) {
        if (!Utils.validateDMX(dmx)||!dmxInRange(dmx)) return -1;
        return minValue+(dmx/255)*(maxValue-minValue);
    }

    public int valueToDMX(double value) {
        if (!valueInRange(value)) return Integer.MAX_VALUE;
        double perc = (value-minValue)/(maxValue-minValue);
        int dmx = (int) (minDMX+perc*(maxDMX-minDMX));
        return Utils.validateDMX(dmx) ? dmx : -1;
    }

    /**
     * Returns the specified value as a percentage of the total value range of this function
     * @param value
     * @return
     */
    public double valueAsPercOfRange(double value) {
        return !valueInRange(value) ? 0 : (value-minValue)/(maxValue-minValue);
    }

    public boolean validate() {
        if (profile==null||parent==null||index==-1||!Utils.validateDMX(minDMX)||!Utils.validateDMX(maxDMX)
            ||minDMX>maxDMX) return false;
        for (ProfileChannelMacro macro : macros) if (!macro.validate()) return false;
        if (hasWheelIndex()&&!profile.hasWheel(wheelIndex)) return false;
        return true;
    }

    public String toProfileString(String indent) {
        String result = "[Function: name="+name+", min_dmx="+minDMX+", max_dmx="+maxDMX+" min_value="+minValue+" maxValue="+maxValue+" wheel="+wheelIndex+"]";
        indent += "\t";
        for (ProfileChannelMacro macro : macros) result += "\n"+indent+macro.toProfileString(indent);
        return result;
    }
}
