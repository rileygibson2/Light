package light.persistency;

import java.util.ArrayList;
import java.util.List;

public class PersistencyWriter {
    
    private List<Byte> buff;

    private boolean segmentOpen;
    private int lastSegmentHeader;
    private int segmentSize;

    public PersistencyWriter() {
        buff = new ArrayList<Byte>();
    }

    public void put(byte b) {
        addInternal(Persistency.delimiter, buff.size());
        buff.add(b);
    }

    public void put(byte[] bArr) {
        addInternal(Persistency.delimiter, buff.size());
        for (byte b : bArr) buff.add(b);
    }

    public void openSegment() {
        if (segmentOpen) closeSegmenet();

        lastSegmentHeader = buff.size()-1;
        addInternal(Persistency.segementHeader, buff.size());
        addInternal(Persistency.segementOpenCode, buff.size());

        segmentOpen = true;
        segmentSize = 0;
    }

    public void closeSegmenet() {
        if (segmentOpen) addInternal(Persistency.segmentCloseCode, buff.size());
        //Add size
        buff.add(lastSegmentHeader+Persistency.segementHeader.length, (byte) segmentSize);
    }

    public void wrapInSegment() {
        List<Byte> nBuff = new ArrayList<>();
        for (byte c : Persistency.segementHeader) nBuff.add(c);
        nBuff.add((byte) buff.size());
        for (byte c : Persistency.segementOpenCode) nBuff.add(c);
        for (byte c : buff) nBuff.add(c);
        for (byte c : Persistency.segmentCloseCode) nBuff.add(c);
        buff = nBuff;
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
