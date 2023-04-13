package light.fixtures;

import light.persistency.PersistencyCapable;

public enum Feature implements PersistencyCapable {
    //Super attributes
    DIMMER,
    SHUTTER,
    COLORRGB,
    COLOR1,
    COLOR2,
    POSITION,
    GOBO1,
    GOBO2,
    BEAM,
    FOCUS,
    SHAPER,
    CONTROL;
    
    @Override
    public byte[] getBytes() {
        return new byte[] {(byte) this.ordinal()};
    }
    
    @Override
    public void generateFromBytes(byte[] bytes) {}
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
