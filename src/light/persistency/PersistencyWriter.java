package light.persistency;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import light.guipackage.cli.CLI;

public class PersistencyWriter {
    
    private List<Byte> buff;
    
    private ByteBuffer intWorker;
    private ByteBuffer doubleWorker;
    
    public PersistencyWriter() {
        buff = new ArrayList<Byte>();
        intWorker = ByteBuffer.allocate(Integer.BYTES);
        doubleWorker = ByteBuffer.allocate(Double.BYTES);
    }

    public void empty() {
        buff.clear();
    }
    
    public void writeString(String str) {
        writeInt(str.length()); //Prefix length
        byte[] encodedStr = str.getBytes();
        for (byte b : encodedStr) buff.add(b);
    }
    
    public void writeInt(int i) {
        intWorker.clear();
        intWorker.putInt(i);
        for (byte b : intWorker.array()) buff.add(b);
    }
    
    public void writeDouble(double d) {
        doubleWorker.clear();
        doubleWorker.putDouble(d);
        for (byte b : doubleWorker.array()) buff.add(b);
    }
    
    public void writeObject(PersistencyCapable o) {
        byte[] bytes = o.getBytes();
        if (bytes==null||bytes.length==0) return; 
        writeInt(bytes.length); //Prefix length
        for (byte b : o.getBytes()) buff.add(b);
    }

    public void unsafelyWriteBytes(byte[] bytes) {
        for (byte b : bytes) buff.add(b);
    }
    
    public byte[] getBytes() {
        byte[] result = new byte[buff.size()];
        for (int i=0; i<result.length; i++) result[i] = buff.get(i);
        return result;
    }
}
