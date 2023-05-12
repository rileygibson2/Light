package light.persistency;

import light.Light;
import light.Pool;
import light.general.ConsoleAddress;
import light.guipackage.cli.CLI;

public class Persistency {
    
    private static Persistency singleton;
    
    /*public static final byte[] segementHeader = new byte[] {(byte) 1, (byte) 1, (byte) 1, (byte) 1};
    public static final byte[] segementOpenCode = new byte[] {(byte) 2, (byte) '[', (byte) '[', (byte) '['};
    public static final byte[] segmentCloseCode = new byte[] {(byte) ']', (byte) ']', (byte) ']', (byte) ']'};
    public static final byte[] delimiter = new byte[] {(byte) ',', (byte) ',', (byte) ',', (byte) ','};*/
    
    public enum Code {
        SEGMENTOPENCODE((byte) '{'),
        SEGMENTCLOSECODE((byte) '}'),
        DELIMITER((byte) ',');
        
        private byte code;
        private Code(byte c) {this.code = c;}
        public byte getCode() {return code;}
    }
    
    private Persistency() {}
    
    public static Persistency getInstance() {
        if (singleton==null) singleton = new Persistency();
        return singleton;
    }
    
    public static Code getCode(byte b) {
        for (Code c : Code.values()) {
            if (b==c.getCode()) return c;
        }
        return null;
    }
    
    /**
    * Storage order:
    * 
    * Fixtures
    * Groups
    * Presets
    * Sequences
    * Effects
    * Executors
    * Views
    * Settings
    */
    
    public void saveToFile(String fileName) {
        PersistencyWriter pW = new PersistencyWriter();
        Light light = Light.getInstance();
        
        //Fixtures
        /*for (Fixture f : PatchManager.getInstance().allFixtureList()) pW.putObject(f);
        
        //Groups
        //pW.put(light.getGroupPool().getBytes());
        
        //Pools
        for (Pool<?> pool : light.allPoolSet()) {
            for (PersistencyCapable p : pool) pW.putObject(p);
        }
        
        String s = new String(pW.getBytes());
        CLI.debug("Save string:\n"+s);
        
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(pW.getBytes());
            fos.close();
            System.out.println("Byte array has been written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        
        
        /*pW.writeInt(10);
        pW.writeString("hellomynameisriley");
        pW.writeDouble(68.2);
        
        pW.openSegment();
        pW.writeDouble(1042.2992);
        pW.writeString("aaaahello");
        pW.closeSegmenet();
        pW.writeObject(new ConsoleAddress(Pool.class, 15, 2));
        pW.writeObject(light.ge);
        
        //pW.writeString("hellomynameisriley");
        byte[] data = pW.getBytes();
        String r = "";
        for (byte b : data) r += b+" ";
        CLI.debug("data: "+r);
        CLI.debug("data: "+new String(data));
        
        PersistencyReader pR = new PersistencyReader(pW.getBytes());
        try {
            int i = pR.readInt();
            CLI.debug("d: "+i);
            String s = pR.readString();
            CLI.debug("d: "+s);
            double d = pR.readDouble();
            CLI.debug("d: "+d);
            
            byte[] seg = pR.readSegment();
            
            String segs = "";
            for (byte b : seg) segs += b+" ";
            CLI.debug("seg bytes: "+segs);
            
            PersistencyReader pR1 = new PersistencyReader(seg);
            d = pR1.readDouble();
            CLI.debug("dSeg: "+d);
            s = pR1.readString();
            CLI.debug("dSeg: "+s);

            ConsoleAddress cA = ConsoleAddress.generateFromBytes(pR.readSegment());
            CLI.debug("d: "+cA);
        }
        catch (PersistencyReadException e) {CLI.error(e);}*/
    }
    
    public void loadFromFile(String fileName) {
        //Get bytes from file
        /*byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Path.of(fileName));
            CLI.debug("read bytes from file:\n"+new String(bytes));
        }
        catch (IOException e) {e.printStackTrace();}
        
        if (bytes==null) return;
        
        PersistencyReader pR = new PersistencyReader(bytes);
        //pR.rea*/
    }
}
