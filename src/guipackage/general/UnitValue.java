package guipackage.general;

public class UnitValue {

    public enum Unit {
        pcw, //Percentage width (Relative unit)
        pch, //Percentage height (Relative unit)
        vw, //Viewport width (Real unit)
        vh, //Viewport height (Real unit)
        px; //Pixels (Real Unit)

        public boolean isRelative() {return this==Unit.pcw||this==Unit.pch;}
        public boolean isReal() {return this!=Unit.pcw&&this!=Unit.pch;}
    };

    public double v;
    public Unit u;

    public UnitValue() {
        this.v = 0;
        this.u = Unit.px;
    }

    public UnitValue(double v) {
        this.v = v;
        this.u = Unit.px;
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
