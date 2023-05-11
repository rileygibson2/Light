package light.persistency;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import light.persistency.Persistency.Code;

public class PersistencyReader {
    
    private byte[] data;
    private int i;
    
    private ByteBuffer worker;
    
    public PersistencyReader(byte[] data) {
        this.data = data;
    }
    
    public void reset() {
        i = 0;
    }
    
    public boolean hasNext() {
        return i+1<data.length;
    }
    
    public int readInt() throws NoRelevantElementException {
        byte[] nextBytes = getNextBytes();
        if (nextBytes==null) throw new NoRelevantElementException();

        worker = ByteBuffer.wrap(nextBytes);
        try {return worker.getInt();}
        catch (BufferUnderflowException e) {throw new NoRelevantElementException();}
    }

    public double readDouble() throws NoRelevantElementException {
        byte[] nextBytes = getNextBytes();
        if (nextBytes==null) throw new NoRelevantElementException();

        worker = ByteBuffer.wrap(nextBytes);
        try {return worker.getDouble();}
        catch (BufferUnderflowException e) {throw new NoRelevantElementException();}
    }

    public String readString() throws NoRelevantElementException {
        byte[] nextBytes = getNextBytes();
        if (nextBytes==null) throw new NoRelevantElementException();
        return new String(nextBytes);
    }
    
    public byte readByte() {
        if (!hasNext()) return Byte.MIN_VALUE;
        i++;
        return data[i];
    }
    
    private byte[] getNextBytes() {
        if (!hasNext()) return null;
        List<Byte> res = new ArrayList<>();
        
        while (hasNext()) {
            byte b = readByte();
            Code code = Persistency.getCode(b);
            if (code!=null) res.add(b);
            else break;
        }
        
        byte[] result = new byte[res.size()];
        for (int i=0; i<res.size(); i++) result[i] = res.get(i);
        return result;
    }
}
