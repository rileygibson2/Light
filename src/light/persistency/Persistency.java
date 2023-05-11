package light.persistency;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import light.Light;
import light.Pool;
import light.fixtures.Fixture;
import light.fixtures.PatchManager;
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
        for (Fixture f : PatchManager.getInstance().allFixtureList()) pW.putObject(f);
        
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
        }
    }
    
    public void loadFromFile(String fileName) {
        //Get bytes from file
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Path.of(fileName));
            CLI.debug("read bytes from file:\n"+new String(bytes));
        }
        catch (IOException e) {e.printStackTrace();}

        if (bytes==null) return;

        PersistencyReader pR = new PersistencyReader(bytes);
        //pR.rea
    }
    
    /**
    * Tree structure is used to unpack split codes and delimiteres into a tree of child nodes an element can work with
    * 
    * 
    */
    
    /*public ByteNode generateByteTree(byte[] data) {
        ByteNode root = new ByteNode();
        ArrayDeque<ByteNode> nodeStack = new ArrayDeque<>();
        nodeStack.push(root);
        
        List<Byte> buff = new ArrayList<Byte>();
        
        for (byte b : data) {
            if (b==segmentOpenCode) {
                ByteNode child = new ByteNode();
                nodeStack.peek().addChild(child);
                nodeStack.push(child);
            }
            if (b==segmentCloseCode) {
                nodeStack.pop();
            }
            if (b==delimiter) {
                nodeStack.peek().addChild(new ByteNode((Byte[]) buff.toArray()));
                buff.clear();
            }
            else buff.add(b);
        }
        
        return root;
    }*/
}

class ByteNode {
    Set<ByteNode> children;
    
    Byte[] content;
    
    public ByteNode() {}
    
    public ByteNode(Byte[] content) {
        this.content = content;
    }
    
    public void addChild(ByteNode child) {
        if (children==null) children = new HashSet<ByteNode>();
        children.add(child);
    }
    
    public void addContent(Byte[] content) {this.content = content;}
}
