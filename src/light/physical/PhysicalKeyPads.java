package light.physical;

public class PhysicalKeyPads {
    
    public static final PhysicalKey[][] numPad = new PhysicalKey[][] {
        {PhysicalKey.NUM7, PhysicalKey.NUM8, PhysicalKey.NUM9, PhysicalKey.PLUS},
        {PhysicalKey.NUM4, PhysicalKey.NUM5, PhysicalKey.NUM6, PhysicalKey.THRU},
        {PhysicalKey.NUM1, PhysicalKey.NUM2, PhysicalKey.NUM3, PhysicalKey.MINUS},
        {PhysicalKey.NUM0, PhysicalKey.DOT, PhysicalKey.IF, PhysicalKey.AT},
        {PhysicalKey.PLEASE, null, null, null}
    };

    public static final PhysicalKey[][] storePad = new PhysicalKey[][] {
        {PhysicalKey.VIEW, PhysicalKey.EFFECT, PhysicalKey.PAGE},
        {PhysicalKey.MACRO, PhysicalKey.PRESET, PhysicalKey.SEQUENCE},
        {PhysicalKey.CUE, PhysicalKey.EXECUTOR, PhysicalKey.FIXTURE},
        {PhysicalKey.GROUP, null, null}
    };

    public static final PhysicalKey[][] topRightCommandPad = new PhysicalKey[][] {
        {PhysicalKey.LABEL},
        {PhysicalKey.COPY},
        {PhysicalKey.MOVE}
    };

    public static final PhysicalKey[][] modulatePad = new PhysicalKey[][] {
        {PhysicalKey.FULL, PhysicalKey.HIGHLIGHT, PhysicalKey.SOLO}
    };

    public static final PhysicalKey[][] topCommandPad = new PhysicalKey[][] {
        {PhysicalKey.FIX, PhysicalKey.SELECT, PhysicalKey.OFF},
        {PhysicalKey.TEMP, PhysicalKey.TOP, PhysicalKey.ON},
        {PhysicalKey.SPEEDDOWN, PhysicalKey.LEARN, PhysicalKey.SPEEDUP},
        {PhysicalKey.GOMINUS, PhysicalKey.PAUSE, PhysicalKey.GOPLUS}
    };


    public static final PhysicalKey[][] leftCommandPad = new PhysicalKey[][] {
        {PhysicalKey.TIME, PhysicalKey.ESCAPE},
        {PhysicalKey.EDIT, PhysicalKey.OOPS},
        {PhysicalKey.UPDATE, PhysicalKey.CLEAR},
        {PhysicalKey.STORE, null}
    };

    public static final PhysicalKey[][] pointerControlPad = new PhysicalKey[][] {
        {null, PhysicalKey.UP, null},
        {PhysicalKey.PREVIOUS, PhysicalKey.SET, PhysicalKey.NEXT},
        {null, PhysicalKey.DOWN, null}
    };
}
