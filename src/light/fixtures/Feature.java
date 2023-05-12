package light.fixtures;

public enum Feature {
    //Super attributes
    DIMMER(FeatureGroup.DIMMER),
    SHUTTER(FeatureGroup.DIMMER),
    COLORRGB(FeatureGroup.COLOR),
    COLOR1(FeatureGroup.COLOR),
    COLOR2(FeatureGroup.COLOR),
    POSITION(FeatureGroup.POSITION),
    GOBO1(FeatureGroup.GOBO),
    GOBO2(FeatureGroup.GOBO),
    GOBO3(FeatureGroup.GOBO),
    BEAM(FeatureGroup.BEAM),
    FOCUS(FeatureGroup.FOCUS),
    SHAPER(FeatureGroup.SHAPERS),
    CONTROL(FeatureGroup.CONTROL);

    private FeatureGroup group;

    private Feature(FeatureGroup group) {
        this.group = group;
    }

    public FeatureGroup getFeatureGroup() {return this.group;}

    public static Feature getFeature(String text) {
        for (Feature feature : Feature.values()) {
            if (feature.toString().equals(text)) return feature;
        }
        return null;
    }
}

/*public boolean isIntensity() {
    return this==Attribute.Intensity;
}

public boolean isColor() {
    return this==Attribute.Red||this==Attribute.Green||this==Attribute.Blue||
    this==Attribute.Amber||this==Attribute.UV||this==Attribute.Lime||this==Attribute.White||
    this==Attribute.Cyan||this==Attribute.Red||this==Attribute.Magenta||this==Attribute.Yellow;
}

public boolean isPosition() {
    return this==Attribute.Pan||this==Attribute.PanFine||this==Attribute.Tilt||this==Attribute.TiltFine;
}

public boolean isBeam() {
    return this==Attribute.Focus||this==Attribute.Iris||this==Attribute.Shutter||this==Attribute.Strobe;
}

public boolean isGobo() {
    return this==Attribute.Gobo1||this==Attribute.Gobo1Rotation||
    this==Attribute.Gobo2||this==Attribute.Gobo2Rotation;
}

public boolean isPrisim() {
    return this==Attribute.Prisim||this==Attribute.PrisimRotation;
}

public boolean isShaper() {
    return this==Attribute.Shaper1A||this==Attribute.Shaper1B||this==Attribute.Shaper2A||
    this==Attribute.Shaper2B||this==Attribute.Shaper3A||this==Attribute.Shaper3B||
    this==Attribute.Shaper4A|| this==Attribute.Shaper4B;
}*/
