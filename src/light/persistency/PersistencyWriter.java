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
    
    public void putString(String str) {
        if (shouldDelimit()) buff.add(Code.DELIMITER.getCode());
        byte[] encodedStr = str.getBytes();
        for (byte b : encodedStr) buff.add(b);
    }
    
    public void putInt(int i) {
        if (shouldDelimit()) buff.add(Code.DELIMITER.getCode());
        intWorker.clear();
        intWorker.putInt(i);
        CLI.debug("putting byte array for int "+i+": ");
        for (byte b : intWorker.array()) {
            CLI.debug(b);
            buff.add(b);
        }
        //addInternal(intWorker.array(), buff.size());
    }
    
    public void putDouble(double d) {
        if (shouldDelimit()) buff.add(Code.DELIMITER.getCode());
        doubleWorker.clear();
        doubleWorker.putDouble(d);
        CLI.debug("putting byte array for double "+d+": ");
        for (byte b : doubleWorker.array()) {
            CLI.debug(b);
            buff.add(b);
        }
        //addInternal(doubleWorker.array(), buff.size());
    }
    
    public void putObject(PersistencyCapable o) {
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
    
    public boolean shouldDelimit() {
        return !buff.isEmpty()&&Persistency.getCode(buff.get(buff.size()-1))==null;
    }
    
    private void addInternal(byte[] code, int position) {
        int i = position;
        for (byte b : code) {
            buff.add(i, b);
            i++;
        }
    }
    
    public byte[] getBytes() {
        byte[] result = new byte[buff.size()];
        for (int i=0; i<result.length; i++) result[i] = buff.get(i);
        return result;
    }
    
}
