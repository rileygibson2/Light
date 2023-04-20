package light.fixtures.profile;

public class ProfileWheelSlot extends ProfileElement {
    
    private ProfileWheel parent;
    private String mediaFileName;
    private String mediaName;

    public ProfileWheelSlot() {
        super();
    }

    public void setWheel(ProfileWheel parent) {this.parent = parent;}
    public ProfileWheel getWheel() {return parent;}

    public void setMediaName(String name) {this.mediaName = name;}
    public boolean hasMediaName() {return mediaName!=null;}
    public String getMediaName() {return mediaName;}

    public void setMediaFileName(String fileName) {this.mediaFileName = fileName;}
    public boolean hasMediaFileName() {return mediaFileName!=null;}
    public String getMediaFileName() {return mediaFileName;}

    public boolean validate() {
        return profile!=null||parent!=null&&index!=-1;
    }

    public String toProfileString(String indent) {
        return "[Slot: index="+index+", media_name="+mediaName+", media_file_name="+mediaFileName+"]";
    }
}
