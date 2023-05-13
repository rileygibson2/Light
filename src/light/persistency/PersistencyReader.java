package light.persistency;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PersistencyReader {
    
    byte[] data;
    int i;
    
    private ByteBuffer worker;
    private int mark;
    
    public PersistencyReader(byte[] data) {
        this.data = data;
        mark = Integer.MAX_VALUE;
    }
    
    public void reset() {
        i = 0;
        mark = Integer.MAX_VALUE;
    }

    public void mark() {mark = i;}

    public void resetToMark() throws PersistencyReadException {
        if (mark==Integer.MAX_VALUE) throw new PersistencyReadException("Trying to reset to mark when mark has not been set.");
        if (mark>=0&&mark<data.length) i = mark;
    }

    public void clearMark() {mark = Integer.MAX_VALUE;}
    
    public boolean hasNext() {
        return i<data.length;
    }

    public boolean hasNextInt() {
        return i+(Integer.BYTES-1)<data.length;
    }

    public boolean hasNextDouble() {
        return i+(Double.BYTES-1)<data.length;
    }
    
    public int readInt() throws PersistencyReadException {
        worker = ByteBuffer.wrap(readForLength(Integer.BYTES));
        try {return worker.getInt();}
        catch (BufferUnderflowException e) {throw new PersistencyReadException("Data was insufficient to read an int");}
    }

    public double readDouble() throws PersistencyReadException {
        worker = ByteBuffer.wrap(readForLength(Double.BYTES));
        try {return worker.getDouble();}
        catch (BufferUnderflowException e) {throw new PersistencyReadException("Data was insufficient to read a double");}
    }

    public String readString() throws PersistencyReadException {
        return new String(readForLength(readInt())); //Read prefixed length, then data, then convert to string
    }

    public byte[] readObject() throws PersistencyReadException {
        if (!hasNextInt()) throw new PersistencyReadException("Data was insufficient to read an object");
        return readForLength(readInt()); //Read prefixed length and then data
    }

    public byte readByte() throws PersistencyReadException {
        if (!hasNext()) throw new PersistencyReadException("Persistency reader has no more data");
        return data[i++];
    }

    private byte[] readForLength(int length) throws PersistencyReadException {
        List<Byte> res = new ArrayList<>();

        int i = 0;
        while (hasNext()&&i<length) {
            res.add(readByte());
            i++;
        }

        byte[] result = new byte[res.size()];
        for (i=0; i<res.size(); i++) result[i] = res.get(i);
        return result;
    }
}
