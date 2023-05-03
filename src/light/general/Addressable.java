package light.general;

public abstract class Addressable implements Comparable<Addressable> {
    
    private ConsoleAddress address;
    private String label;
    private Runnable updateAction; //Action to run on update of address or label

    public Addressable(ConsoleAddress address) {
        this.address = address;
        this.label = "";
    }

    public ConsoleAddress getAddress() {return address;}
    public void setAddress(ConsoleAddress address) {
        this.address = address;
        if (hasUpdateAction()) updateAction.run();
    }
    public boolean hasAddress() {return address!=null;}

    public String getLabel() {return label;}
    public void setLabel(String label) {
        this.label = label;
        if (hasUpdateAction()) updateAction.run();
    }

    public void setUpdateAction(Runnable action) {this.updateAction = action;}
    public boolean hasUpdateAction() {return updateAction!=null;}
    public Runnable getUpdateAction() {return updateAction;}

    @Override
    public int compareTo(Addressable o) {
        return this.address.compareTo(o.getAddress());
    }
}
