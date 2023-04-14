package light.fixtures.profile;

import light.general.Utils;

public class ProfileChannelMacro {
    
    private ProfileChannelFunction parent;
    private int index;

    private String name;
    private double fromDMX;
    private double toDMX;

    public ProfileChannelMacro() {
        fromDMX = -1;
        toDMX = -1;
    }

    public void setParent(ProfileChannelFunction parent) {this.parent = parent;}
    public ProfileChannelFunction getParent() {return parent;}

    public void setIndex(int index) {this.index = index;}
    public int getIndex() {return index;}

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}
    
    public void setFromDMX(double d) {this.fromDMX = d;}
    public double getFromDMX() {return fromDMX;}

    public void setToDMX(double d) {this.toDMX = d;}
    public double getToDMX() {return toDMX;}

    public boolean validate() {
        return parent!=null&&Utils.validateDMX(fromDMX)&&Utils.validateDMX(toDMX);
    }

    public String toProfileString(String indent) {
        return "[Macro: name="+name+", from_dmx="+fromDMX+", to_dmx="+toDMX+"]";
    }
}
