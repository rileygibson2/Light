package light.fixtures.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import light.general.Utils;

public class ProfileChannelFunction {
    
    private ProfileChannel parent;
    private int index;
    private String name; //Name for this subattribute function

    private List<ProfileChannelMacro> macros; //Macros to codify specific functions within this channel's DMX range

    //User values
    private double minValue;
    private double maxValue;
    //DMX values
    private double minDMX;
    private double maxDMX;

    public ProfileChannelFunction() {
        index = -1;
        minDMX = -1;
        maxDMX = -1;
        minValue = 0;
        maxValue = 100;
        macros = new ArrayList<ProfileChannelMacro>();
    }

    public void setParent(ProfileChannel parent) {this.parent = parent;}
    public ProfileChannel getParent() {return parent;}

    public void setIndex(int index) {this.index = index;}
    public int getIndex() {return index;}
    
    public void addMacro(ProfileChannelMacro m) {
        macros.add(m);
        m.setParent(this);
        sortMacros();
    }

    public boolean hasMacro(ProfileChannelMacro m) {return macros.contains(m);}

    public List<ProfileChannelMacro> getMacros() {
        sortMacros();
        return macros;
    }

    private void sortMacros() {
        Collections.sort(macros, new Comparator<ProfileChannelMacro>() {
            @Override
            public int compare(ProfileChannelMacro o1, ProfileChannelMacro o2) {
                return (int) (o1.getIndex()-o2.getIndex());
            }
        });
    }

    public void setName(String name) {this.name = name;}
    public String getName() {
        if (name!=null) return name;
        else return parent.getAttributeUserName();
    }

    public void setMinValue(double v) {this.minValue = v;}
    public double getMinValue() {return minValue;}

    public void setMaxValue(double v) {this.maxValue = v;}
    public double getMaxValue() {return maxValue;}

    public void setMinDMX(double d) {this.minDMX = d;}
    public double getMinDMX() {return minDMX;}

    public void setMaxDMX(double d) {this.maxDMX = d;}
    public double getMaxDMX() {return maxDMX;}

    public boolean valueValidForRange(double value) {
        return value>=minValue&&value<=maxValue;
    }

    public boolean inDMXRange(double dmx) {
        return dmx>=minDMX&&dmx<=maxDMX;
    }

    public double dmxToValue(double dmx) {
        if (!Utils.validateDMX(dmx)) return Double.MAX_VALUE;
        return (dmx/255)*(maxValue-minValue);
    }

    public boolean validate() {
        if (parent==null||index==-1||!Utils.validateDMX(minDMX)||!Utils.validateDMX(maxDMX)
            ||minDMX>maxDMX) return false;
        for (ProfileChannelMacro macro : macros) if (!macro.validate()) return false;
        return true;
    }

    public String toProfileString(String indent) {
        String result = "[Function: name="+name+", min_dmx="+minDMX+", max_dmx="+maxDMX+"]";
        indent += "\t";
        for (ProfileChannelMacro macro : macros) result += "\n"+indent+macro.toProfileString(indent);
        return result;
    }
}
