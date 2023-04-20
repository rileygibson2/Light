package light.fixtures.profile;

public abstract class ProfileElement implements Comparable<ProfileElement> {
    
    protected int index;

    public ProfileElement() {
        index = -1;
    }

    public void setIndex(int index) {this.index = index;}
    public int getIndex() {return index;} 

    public abstract boolean validate(Profile profile);

    @Override
    public int compareTo(ProfileElement o) {
        return this.getIndex()-o.getIndex();
    }
}
