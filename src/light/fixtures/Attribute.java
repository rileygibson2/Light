package light.fixtures;

public enum Attribute {
    //Dimmer sub attributes
    DIM(Feature.DIMMER, "Dim"),

    //Shutter sub attributes
    SHUTTER(Feature.SHUTTER, "Shutter"),
    STROBE(Feature.SHUTTER, "Strobe"),

    //COLORRGB sub attributes
    COLORRGB1(Feature.COLORRGB, "Red"), //Red
    COLORRGB2(Feature.COLORRGB, "Green"), //Green
    COLORRGB3(Feature.COLORRGB, "Blue"), //Blue
    COLORRGB4(Feature.COLORRGB, "Amber"), //Amber
    COLORRGB5(Feature.COLORRGB, "White"), //White
    COLORRGB6(Feature.COLORRGB, "Lime"), //Lime
    COLORRGB7(Feature.COLORRGB, "UV"), //UV
    COLORRGB8(Feature.COLORRGB, "Cyan"), //Cyan
    COLORRGB9(Feature.COLORRGB, "Yellow"), //Yellow
    COLORRGB10(Feature.COLORRGB, "Magenta"), //Magenta
    
    //Position sub attributes
    PAN(Feature.POSITION, "Pan"),
    TILT(Feature.POSITION, "Tilt"),
    
    //Gobo1 sub attributes
    GOBO1_INDEX(Feature.GOBO1, "Index"),
    GOBO1_ROTATION(Feature.GOBO1, "Rotate"),
    GOBO1_SPIN(Feature.GOBO1, "Spin"),

    //Gobo1 sub attributes
    GOBO2_INDEX(Feature.GOBO2, "Index"),
    GOBO2_ROTATION(Feature.GOBO2, "Rotate"),
    GOBO2_SPIN(Feature.GOBO2, "Spin"),

    //Beam sub attributes
    IRIS(Feature.BEAM, "Iris"),
    FROST(Feature.BEAM, "Frost"),
    PRISM1(Feature.BEAM, "Prism"),
    PRISM1_ROTATION(Feature.BEAM, "Prism Rotate"),

    //Focus sub attributes
    FOCUS(Feature.FOCUS, "Focus"),
    ZOOM(Feature.FOCUS, "Zoom"),
    
    //Shaper sub attributes
    SHAPER_1A(Feature.SHAPER, "1A"),
    SHAPER_1B(Feature.SHAPER, "1B"),
    SHAPER_2A(Feature.SHAPER, "2A"),
    SHAPER_2B(Feature.SHAPER, "2B"),
    SHAPER_3A(Feature.SHAPER, "3A"),
    SHAPER_3B(Feature.SHAPER, "3B"),
    SHAPER_4A(Feature.SHAPER, "4A"),
    SHAPER_4B(Feature.SHAPER, "4B");
    
    private Feature feature;
    private String userName;
    
    private Attribute(Feature feature, String userName) {
        this.feature = feature;
        this.userName = userName;
    }
    
    public Feature getFeature() {return this.feature;}

    public String getUserName() {return this.userName;}

    public boolean verify(Feature verify) {return verify==feature;}

    public static Attribute getAttribute(String text) {
        for (Attribute attribute : Attribute.values()) {
            if (attribute.toString().equals(text)) return attribute;
        }
        return null;
    }
}
