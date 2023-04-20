package light.fixtures.profile;

public abstract class ProfileElement implements Comparable<ProfileElement> {
    
    protected Profile profile;
    protected int index;

    public ProfileElement() {
        index = -1;
    }

    public void setProfile(Profile profile) {this.profile = profile;}
    public Profile getProfile() {return profile;}

    public void setIndex(int index) {this.index = index;}
    public int getIndex() {return index;} 

    public abstract boolean validate();

    @Override
    public int compareTo(ProfileElement o) {
        return this.getIndex()-o.getIndex();
    }
}
