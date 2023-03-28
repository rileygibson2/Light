package light.persistency;

import java.util.ArrayList;
import java.util.List;

public class PersistencyWriter {
    
    List<Byte> buff;

    public PersistencyWriter() {
        buff = new ArrayList<Byte>();
    }

    public void put(byte b) {
        if (shouldDelimit()) buff.add(Persistency.delimiter);
        buff.add(b);
    }

    public void put(byte[] bArr) {
        if (shouldDelimit()) buff.add(Persistency.delimiter);
        for (byte b : bArr) buff.add(b);
    }

    public void openSegment() {
        if (shouldDelimit()) buff.add(Persistency.delimiter);
        buff.add(Persistency.segmentOpenCode);
    }

    public void closeSegmenet() {
        buff.add(Persistency.segmentCloseCode);
    }

    public void wrapInSegment() {
        buff.add(0, Persistency.segmentOpenCode);
        buff.add(Persistency.segmentCloseCode);
    }

    private boolean shouldDelimit() {
        return !buff.isEmpty()&&!Persistency.isCode(buff.get(buff.size()-1));
    }

    public byte[] toArray() {
        byte[] result = new byte[buff.size()];
        for (int i=0; i<result.length; i++) result[i] = buff.get(i);
        return result;
    }

}
