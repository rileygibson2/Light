package light.guipackage.general;

import light.guipackage.general.UnitValue.Unit;

public class UnitRectangle {
    
    public UnitValue x;
    public UnitValue y;
    public UnitValue width;
    public UnitValue height;

    public UnitRectangle() {
        x = new UnitValue(0, Unit.pcw);
        y = new UnitValue(0, Unit.pch);
        width = new UnitValue(0, Unit.pcw);
        height = new UnitValue(0, Unit.pch);
    }

    public UnitRectangle(Unit u) {
        x = new UnitValue(0, u);
        y = new UnitValue(0, u);
        width = new UnitValue(0, u);
        height = new UnitValue(0, u);
    }

    public UnitRectangle(double x, double y, double width, double height) {
        this.x = new UnitValue(x, Unit.pcw);
        this.y = new UnitValue(y, Unit.pch);
        this.width = new UnitValue(width, Unit.pcw);
        this.height = new UnitValue(height, Unit.pch);
    }

    public UnitRectangle(double x, double y, double width, double height, Unit u) {
        this.x = new UnitValue(x, u);
        this.y = new UnitValue(y, u);
        this.width = new UnitValue(width, u);
        this.height = new UnitValue(height, u);
    }

    public UnitRectangle(double x, Unit xU, double y, Unit yU, double width, Unit widthU, double height, Unit heightU) {
        this.x = new UnitValue(x, xU);
        this.y = new UnitValue(y, yU);
        this.width = new UnitValue(width, widthU);
        this.height = new UnitValue(height, heightU);
    }

    public UnitRectangle(double x, double y, double width, double height, Unit xU, Unit yU) {
        this.x = new UnitValue(x, xU);
        this.y = new UnitValue(y, yU);
        this.width = new UnitValue(width, xU);
        this.height = new UnitValue(height, yU);
    }

    public UnitRectangle(UnitValue x, UnitValue y, UnitValue width, UnitValue height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public UnitRectangle(UnitPoint pos, UnitPoint dims) {
        this.x = pos.x;
        this.y = pos.y;
        this.width = dims.x;
        this.height = dims.y;
    }

    public UnitRectangle(UnitPoint pos, UnitValue width, UnitValue height) {
        this.x = pos.x;
        this.y = pos.y;
        this.width = width;
        this.height = height;
    }

    public UnitRectangle(UnitValue x, UnitValue y, UnitPoint dims) {
        this.x = x;
        this.y = y;
        this.width = dims.x;
        this.height = dims.y;
    }

    public void setAllUnits(Unit u) {
        x.u = u;
        y.u = u;
        width.u = u;
        height.u = u;
    }

    public boolean hasUnit(Unit u) {
        if (u==null) return false;
        return x.u==u||y.u==u||width.u==u||height.u==u;
    }

    public boolean allUnitsSame(Unit u) {
        if (u==null) return false;
        return x.u==u&&y.u==u&&width.u==u&&height.u==u;
    }

    public boolean allUnitsReal() {
        return x.u.isReal()&&y.u.isReal()&&width.u.isReal()&&height.u.isReal();
    }

    public boolean allUnitsRelative() {
        return x.u.isRelative()&&y.u.isRelative()&&width.u.isRelative()&&height.u.isRelative();
    }

    public Rectangle toRect() {
        return new Rectangle(x.v, y.v, width.v, height.v);
    }

    @Override
	public String toString() {
		return "["+x.toString()+", "+y.toString()+", "+width.toString()+", "+height.toString()+"]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UnitRectangle)) return false;
		UnitRectangle r = (UnitRectangle) o;
		if (x.equals(r.x)&&y.equals(r.y)&&width.equals(r.width)&&height.equals(r.height)) return true;
		return false;
	}
	
	public UnitRectangle clone() {
		return new UnitRectangle(x.clone(), y.clone(), width.clone(), height.clone());
	}
}
