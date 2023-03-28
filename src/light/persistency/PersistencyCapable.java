package light.persistency;

public interface PersistencyCapable {
    
    public byte[] getBytes();

    public void generateFromBytes(byte[] bytes);
}
