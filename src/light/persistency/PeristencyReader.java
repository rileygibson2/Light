package light.persistency;

public class PeristencyReader {
    
    private byte[] data;
    private int i;

    public PeristencyReader(byte[] data) {
        this.data = data;
    }

    public void reset() {
        i = 0;
    }
}
