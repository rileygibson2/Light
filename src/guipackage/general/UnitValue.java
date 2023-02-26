package guipackage.general;

public class UnitValue {

    public enum Unit {
        pc,
        vw,
        vh,
        px
    };

    public double v;
    public Unit u;

    public UnitValue() {}

    public UnitValue(double v) {
        this.v = v;
        this.u = Unit.pc;
    }

    public UnitValue(double v, Unit u) {
        this.v = v;
        this.u = u;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UnitValue)) return false;
        UnitValue u = (UnitValue) o;
        if (u.v==v&&u.u.equals(u)) return true;
        return false;
    }

    @Override
    public UnitValue clone() {
        return new UnitValue(v, u);
    }

    @Override
    public String toString() {
        return v+u.toString();
    }
}
