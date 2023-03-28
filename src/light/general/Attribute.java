package light.general;

import light.persistency.PersistencyCapable;

public enum Attribute implements PersistencyCapable {
    Intensity,

    Red,
    Green,
    Blue,
    Amber,
    UV,
    Lime,
    White,
    Cyan,
    Magenta,
    Yellow,

    Pan,
    PanFine,
    Tilt,
    TiltFine,

    Focus,
    Iris,
    Shutter,
    Strobe,

    Gobo1,
    Gobo1Rotation,
    Gobo2,
    Gobo2Rotation,

    Prisim,
    PrisimRotation,

    Shaper1A,
    Shaper1B,
    Shaper2A,
    Shaper2B,
    Shaper3A,
    Shaper3B,
    Shaper4A,
    Shaper4B;

    public boolean isIntensity() {
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
    }

    @Override
    public byte[] getBytes() {
        return new byte[] {(byte) this.ordinal()};
    }

    @Override
    public void generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
