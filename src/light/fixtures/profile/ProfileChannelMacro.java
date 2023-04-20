package light.fixtures.profile;

import light.general.Utils;

public class ProfileChannelMacro extends ProfileElement {
    
    private ProfileChannelFunction parent;
    
    private String name;
    
    //DMX values
    private double fromDMX;
    private double toDMX;
    
    //Wheel values
    private int slotIndex;
    
    public ProfileChannelMacro() {
        super();
        fromDMX = -1;
        toDMX = -1;
        slotIndex = -1;
    }
    
    public void setFunction(ProfileChannelFunction parent) {this.parent = parent;}
    public ProfileChannelFunction getFunction() {return parent;}
    
    public void setName(String name) {this.name = name;}
    public String getName() {return name;}
    
    public void setFromDMX(double d) {this.fromDMX = d;}
    public double getFromDMX() {return fromDMX;}
    
    public void setToDMX(double d) {this.toDMX = d;}
    public double getToDMX() {return toDMX;}

    public double getMidDMX() {return (this.fromDMX+this.toDMX)/2;}
    
    public void setSlotIndex(int slot) {this.slotIndex = slot;}
    public boolean hasSlotIndex() {return slotIndex!=-1;}
    public int getSlotIndex() {return slotIndex;}

    public ProfileWheelSlot getSlotMapping() {
        if (!hasSlotIndex()) return null;
        ProfileWheel wheel = parent.getWheelMapping();
        return wheel==null ? null : wheel.getSlot(slotIndex);
    }

    public boolean dmxInRange(double dmx) {
        return dmx>=fromDMX&&dmx<=toDMX;
    }
    
    public boolean validate() {
        if (profile==null||parent==null||!Utils.validateDMX(fromDMX)||!Utils.validateDMX(toDMX)||fromDMX>toDMX
        ||fromDMX<parent.getMinDMX()||fromDMX>parent.getMaxDMX()
        ||toDMX<parent.getMinDMX()||toDMX>parent.getMaxDMX()) return false;
        if (hasSlotIndex()&&!profile.hasWheelAndSlot(parent.getWheelIndex(), slotIndex)) return false;
        
        return true;
    }
    
    public String toProfileString(String indent) {
        return "[Macro: name="+name+", from_dmx="+fromDMX+", to_dmx="+toDMX+" slot="+slotIndex+"]";
    }
}
