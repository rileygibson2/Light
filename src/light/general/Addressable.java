package light.general;

public abstract class Addressable implements Comparable<Addressable> {
    
    private ConsoleAddress address;
    private String label;

    public Addressable(ConsoleAddress address) {
        this.address = address;
        this.label = "";
    }

    public ConsoleAddress getAddress() {return address;}

    public void setAddress(ConsoleAddress address) {this.address = address;}

    public boolean hasAddress() {return address!=null;}

    public String getLabel() {return label;}

    public void setLabel(String label) {this.label = label;}

    @Override
    public int compareTo(Addressable o) {
        return this.address.compareTo(o.getAddress());
    }
}
