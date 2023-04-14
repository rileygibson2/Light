package light.fixtures;

public enum Attribute {
    //Dimmer sub attributes
    DIM(Feature.DIMMER),

    //Shutter sub attributes
    SHUTTER(Feature.SHUTTER),
    STROBE(Feature.SHUTTER),

    //COLORRGB sub attributes
    COLORRGB1(Feature.COLORRGB), //Red
    COLORRGB2(Feature.COLORRGB), //Green
    COLORRGB3((Feature.COLORRGB)), //Blue
    COLORRGB4((Feature.COLORRGB)), //Amber
    COLORRGB5((Feature.COLORRGB)), //White
    COLORRGB6((Feature.COLORRGB)), //Lime
    COLORRGB7((Feature.COLORRGB)), //UV
    COLORRGB8((Feature.COLORRGB)), //Cyan
    COLORRGB9((Feature.COLORRGB)), //Yellow
    COLORRGB10((Feature.COLORRGB)), //Magenta
    
    //Position sub attributes
    PAN(Feature.POSITION),
    TILT(Feature.POSITION),
    
    //Gobo1 sub attributes
    GOBO1_INDEX(Feature.GOBO1),
    GOBO1_ROTATION(Feature.GOBO1),
    GOBO1_SPIN(Feature.GOBO1),

    //Gobo1 sub attributes
    GOBO2_INDEX(Feature.GOBO2),
    GOBO2_ROTATION(Feature.GOBO2),
    GOBO2_SPIN(Feature.GOBO2),

    //Beam sub attributes
    IRIS(Feature.BEAM),
    FROST(Feature.BEAM),
    PRISIM1(Feature.BEAM),
    PRISIM1_ROTATION(Feature.BEAM),

    //Focus sub attributes
    FOCUS(Feature.FOCUS),
    ZOOM(Feature.FOCUS),
    
    //Shaper sub attributes
    SHAPER_1A(Feature.SHAPER),
    SHAPER_1B(Feature.SHAPER),
    SHAPER_2A(Feature.SHAPER),
    SHAPER_2B(Feature.SHAPER),
    SHAPER_3A(Feature.SHAPER),
    SHAPER_3B(Feature.SHAPER),
    SHAPER_4A(Feature.SHAPER),
    SHAPER_4B(Feature.SHAPER);
    
    private Feature parent;
    
    private Attribute(Feature parent) {
        this.parent = parent;
    }
    
    public Feature getParent() {return this.parent;}

    public boolean verify(Feature verify) {return verify==parent;}

    public static Attribute getAttribute(String text) {
        for (Attribute attribute : Attribute.values()) {
            if (attribute.toString().equals(text)) return attribute;
        }
        return null;
    }
}
