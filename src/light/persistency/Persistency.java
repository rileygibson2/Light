package light.persistency;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import light.Light;
import light.fixtures.Fixture;
import light.fixtures.PatchManager;

public class Persistency {
    
    private static Persistency singleton;
    
    public static final byte[] segementHeader = new byte[] {(byte) 1, (byte) 1, (byte) 1, (byte) 1};
    public static final byte[] segementOpenCode = new byte[] {(byte) 2, (byte) '[', (byte) '[', (byte) '['};
    public static final byte[] segmentCloseCode = new byte[] {(byte) ']', (byte) ']', (byte) ']', (byte) ']'};
    public static final byte[] delimiter = new byte[] {(byte) ',', (byte) ',', (byte) ',', (byte) ','};;
    
    private Persistency() {
        
    }
    
    public static Persistency getInstance() {
        if (singleton==null) singleton = new Persistency();
        return singleton;
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

    public byte[] generateBytes() {
        PersistencyWriter pW = new PersistencyWriter();
        Light light = Light.getInstance();
        
        //Fixtures
        pW.openSegment();
        for (Fixture f : PatchManager.getInstance().allFixtureList()) pW.put(f.getBytes());
        pW.closeSegmenet();

        //Groups
        //pW.put(light.getGroupPool().getBytes());

        return pW.getBytes();
    }

    /*public static byte[] combineAndDelimit(byte... args) {
		byte[] result = new byte[(args.length*2)-1];

		for (int i=0; i<result.length; i+=2) {
            result[i] = args[i];
            if (i+1<result.length) result[i+1] = delimiter;
        }
		return result;
	}

    public static byte[] combineAndDelimit(byte[]... args) {
        int l = 0;
		for (byte[] b : args) l += b.length;
        l += args.length-1; //Add space for delimiters

		ByteBuffer buff = ByteBuffer.allocate(l);
        for (int i=0; i<args.length; i++) {
            buff.put(args[i]);
            if (i+1<args.length) buff.put(new byte[] {delimiter});
        }
        return buff.array();
    }

    public static byte[] addSegments(byte[] toSegment) {
        byte[] result = new byte[toSegment.length+2];
        //Copy array
        System.arraycopy(toSegment, 0, result, 1, toSegment.length);
        
        //Add segement codes
        result[0] = segmentOpenCode;
        result[result.length-1] = segmentCloseCode;
        return result;
    }*/

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
