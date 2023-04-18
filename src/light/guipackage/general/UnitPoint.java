package light.guipackage.general;

import light.guipackage.general.UnitValue.Unit;

public class UnitPoint {
    
    public UnitValue x;
    public UnitValue y;

    public UnitPoint() {
        x = new UnitValue(0, Unit.px);
        y = new UnitValue(0, Unit.px);
    }

    public UnitPoint(Unit u) {
        x = new UnitValue(0, u);
        y = new UnitValue(0, u);
    }

    public UnitPoint(double x, double y) {
        this.x = new UnitValue(x, Unit.pcw);
        this.y = new UnitValue(y, Unit.pch);
    }

    public UnitPoint(double x, double y, Unit u) {
        this.x = new UnitValue(x, u);
        this.y = new UnitValue(y, u);
    }

    public UnitPoint(double x, Unit xU, double y, Unit yU) {
        this.x = new UnitValue(x, xU);
        this.y = new UnitValue(y, yU);
    }

    public UnitPoint(UnitValue x, UnitValue y) {
        this.x = x;
        this.y = y;
    }

    public void setAllUnits(Unit u) {
        x.u = u;
        y.u = u;
    }

    public boolean hasUnit(Unit u) {
        if (u==null) return false;
        return x.u==u||y.u==u;
    }

    public boolean allUnitsSame(Unit u) {
        if (u==null) return false;
        return x.u==u&&y.u==u;
    }

    public boolean allUnitsReal() {
        return x.u.isReal()&&y.u.isReal();
    }

    public boolean allUnitsRelative() {
        return x.u.isRelative()&&y.u.isRelative();
    }

    public Point toPoint() {
        return new Point(x.v, y.v);
    }

    @Override
	public String toString() {
		return "["+x.toString()+", "+y.toString()+"]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UnitRectangle)) return false;
		UnitRectangle r = (UnitRectangle) o;
		if (x.equals(r.x)&&y.equals(r.y)) return true;
		return false;
	}
	
	public UnitPoint clone() {
		return new UnitPoint(x.clone(), y.clone());
	}
}
