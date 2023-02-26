package guipackage.general;

import guipackage.general.UnitValue.Unit;

public class UnitPoint {
    
    public UnitValue x;
    public UnitValue y;

    public UnitPoint() {
        x = new UnitValue(0, Unit.pc);
        y = new UnitValue(0, Unit.pc);
    }

    public UnitPoint(Unit u) {
        x = new UnitValue(0, u);
        y = new UnitValue(0, u);
    }

    public UnitPoint(double x, double y, double width, double height) {
        this.x = new UnitValue(x, Unit.pc);
        this.y = new UnitValue(y, Unit.pc);
    }

    public UnitPoint(double x, double y, double width, double height, Unit u) {
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
