package light.fixtures.profile;

public class ProfileChannelMacro {
    
    private ProfileChannel parent;
    private String name;
    private double fromDMX;
    private double toDMX;

    public ProfileChannelMacro(ProfileChannel parent) {
        this.parent = parent;
    }

    public ProfileChannel getParent() {return parent;}

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}
    
    public void setFromDMX(double d) {this.fromDMX = d;}
    public double getFromDMX() {return fromDMX;}

    public void setToDMX(double d) {this.toDMX = d;}
    public double getToDMX() {return toDMX;}
}
