package light.persistency;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import light.guipackage.cli.CLI;
import light.persistency.Persistency.Code;

public class PersistencyReader {
    
    private byte[] data;
    private int i;
    
    private ByteBuffer worker;
    private int mark;
    
    public PersistencyReader(byte[] data) {
        this.data = data;
        mark = Integer.MAX_VALUE;
    }
    
    public void reset() {
        i = 0;
        mark = 0;
    }

    public void mark() {mark = i;}

    public void resetToMark() {
        if (mark!=Integer.MAX_VALUE&&mark>=0&&mark<data.length) i = mark;
    }

    public void clearMark() {mark = Integer.MAX_VALUE;}
    
    public boolean hasNext() {
        return i<data.length;
    }
    
    public int readInt() throws PersistencyReadException {
        if (peekNextByte()==Code.DELIMITER.getCode()) readByte(); //Skip delimeter
        byte[] nextBytes = readTillCode(Code.DELIMITER);
        if (nextBytes==null) throw new PersistencyReadException();

        worker = ByteBuffer.wrap(nextBytes);
        try {return worker.getInt();}
        catch (BufferUnderflowException e) {throw new PersistencyReadException("Data was insufficient to read an int");}
    }

    public double readDouble() throws PersistencyReadException {
        if (peekNextByte()==Code.DELIMITER.getCode()) readByte(); //Skip delimeter
        byte[] nextBytes = readTillCode(Code.DELIMITER);
        if (nextBytes==null) throw new PersistencyReadException();

        worker = ByteBuffer.wrap(nextBytes);
        try {return worker.getDouble();}
        catch (BufferUnderflowException e) {throw new PersistencyReadException("Data was insufficient to read a double");}
    }

    public String readString() throws PersistencyReadException {
        if (peekNextByte()==Code.DELIMITER.getCode()) readByte(); //Skip delimeter
        byte[] nextBytes = readTillCode(Code.DELIMITER);
        if (nextBytes==null) throw new PersistencyReadException();
        return new String(nextBytes);
    }

    public byte[] readSegment() throws PersistencyReadException {
        if (peekNextByte()==Code.DELIMITER.getCode()) readByte(); //Skip delimeter
        
        if (Persistency.getCode(peekNextByte())!=Code.SEGMENTOPENCODE) throw new PersistencyReadException("Segment open code was not found in the correct place");
        readByte(); //Skip open code
        byte[] result = readTillCode(Code.SEGMENTCLOSECODE);
        if (Persistency.getCode(peekNextByte())!=Code.SEGMENTCLOSECODE) throw new PersistencyReadException("Segment close code was not found in the correct place");
        readByte(); //Skip close code

        return result;
    }

    public byte readByte() throws PersistencyReadException {
        if (!hasNext()) throw new PersistencyReadException("Persistency reader has no more data");
        return data[i++];
    }
    
    /**
     * Mostly used for checking next byte for code.
     * @return
     * @throws PersistencyReadException
     */
    public byte peekNextByte() throws PersistencyReadException {
        if (!hasNext()) throw new PersistencyReadException("Persistency reader has no more data");
        return data[i];
    }

    private byte[] readTillCode(Code c) throws PersistencyReadException {
        List<Byte> res = new ArrayList<>();

        while (hasNext()) {
            if (peekNextByte()==c.getCode()) break;
            res.add(readByte());
        }

        byte[] result = new byte[res.size()];
        for (int i=0; i<res.size(); i++) result[i] = res.get(i);
        return result;
    }
}
