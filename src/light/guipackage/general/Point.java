package light.guipackage.general;

public class Point {
	public double x;
	public double y;
	
	public Point() {}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "["+x+", "+y+"]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point)) return false;
		Point p = (Point) o;
		if (x==p.x&&y==p.y) return true;
		return false;
	}
	
	public Point clone() {
		return new Point(x, y);
	}
}
