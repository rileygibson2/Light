package light.persistency;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import light.guipackage.cli.CLI;
import light.persistency.Persistency.Code;

public class PersistencyWriter {
    
    private List<Byte> buff;
    
    private ByteBuffer intWorker;
    private ByteBuffer doubleWorker;
    
    public PersistencyWriter() {
        buff = new ArrayList<Byte>();
        intWorker = ByteBuffer.allocate(Integer.BYTES);
        doubleWorker = ByteBuffer.allocate(Double.BYTES);
    }
    
    public void writeString(String str) {
        if (shouldDelimit()) buff.add(Code.DELIMITER.getCode());
        byte[] encodedStr = str.getBytes();
        for (byte b : encodedStr) buff.add(b);
    }
    
    public void writeInt(int i) {
        if (shouldDelimit()) buff.add(Code.DELIMITER.getCode());
        intWorker.clear();
        intWorker.putInt(i);

        String s = "";
        for (byte b : intWorker.array()) s += b+" ";
        CLI.debug("writing int: "+s);

        for (byte b : intWorker.array()) buff.add(b);
    }
    
    public void writeDouble(double d) {
        if (shouldDelimit()) buff.add(Code.DELIMITER.getCode());
        doubleWorker.clear();
        doubleWorker.putDouble(d);
        for (byte b : doubleWorker.array()) buff.add(b);
    }
    
    public void writeObject(PersistencyCapable o) {
        if (shouldDelimit()) buff.add(Code.DELIMITER.getCode());
        for (byte b : o.getBytes()) buff.add(b);
    }
    
    public void openSegment() {
        if (shouldDelimit()) buff.add(Code.DELIMITER.getCode());
        buff.add(Code.SEGMENTOPENCODE.getCode());
    }
    
    public void closeSegmenet() {
        buff.add(Code.SEGMENTCLOSECODE.getCode());
    }
    
    public void wrapInSegment() {
        buff.add(0, Code.SEGMENTOPENCODE.getCode());
        buff.add(Code.SEGMENTCLOSECODE.getCode());
    }
    
    private boolean shouldDelimit() {
        return !buff.isEmpty()&&(Persistency.getCode(buff.get(buff.size()-1))==null||Persistency.getCode(buff.get(buff.size()-1))==Code.SEGMENTCLOSECODE);
    }
    
    public byte[] getBytes() {
        byte[] result = new byte[buff.size()];
        for (int i=0; i<result.length; i++) result[i] = buff.get(i);
        return result;
    }
}
