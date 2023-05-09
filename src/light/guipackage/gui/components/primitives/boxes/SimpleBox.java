package light.guipackage.gui.components.primitives.boxes;

import java.awt.Color;
import java.awt.Graphics2D;

import light.guipackage.general.UnitRectangle;
import light.guipackage.gui.GUI;
import light.guipackage.gui.IO;
import light.guipackage.gui.components.Component;

public class SimpleBox extends Component {

	private Color col;
	private boolean filled;
	private boolean oval;

	private boolean border;
	private double borderWidth;
	private Color borderColor;
	private int[] borderSides;

	private boolean rounded;
	private int arcSize; //Rounded corner sizes
	private int[] roundedCorners;

	private boolean draw;
	
	public SimpleBox(UnitRectangle r) {
		super(r);
		draw = false;
		filled = false;
		rounded = false;
		arcSize = 10;
		oval = false;
		border = false;
		borderWidth = 1;
	}
	
	public void setDraw(boolean d) {
		draw = d;
		if (inDOM()) IO.getInstance().requestPaint();
	}

	public Color getColor() {return col;}
	public void setColor(Color c) {
		draw = true;
		filled = true;
		col = c;
		if (inDOM()) IO.getInstance().requestPaint();
	}
	public boolean hasColor() {return col!=null;}
	
	public void setFilled(boolean f) {
		filled = f;
		if (inDOM()) IO.getInstance().requestPaint();
	}
	public boolean isFilled() {return filled;}

	public void setBorder(double width, Color col)  {
		draw = true;
		border = true;
		borderWidth = width;
		borderColor = col;
		borderSides = null;
		if (inDOM()) IO.getInstance().requestPaint();
	}
	public void setBorder(Color col)  {
		draw = true;
		border = true;
		borderColor = col;
		borderSides = null;
		if (inDOM()) IO.getInstance().requestPaint();
	}
	public void setBorder(int[] sides, Color col) {
		draw = true;
		border = true;
		borderColor = col;
		borderSides = sides;
		if (inDOM()) IO.getInstance().requestPaint();
	}
	public void setBorder(int[] sides, double width, Color col) {
		draw = true;
		border = true;
		borderWidth = width;
		borderColor = col;
		borderSides = sides;
		appearanceUpdated();
	}

	public void removeBorder() {
		border = false;
		appearanceUpdated();
	}
	public boolean hasBorder() {return border;}

	public void setBorderWidth(double width) {
		borderWidth = width;
		appearanceUpdated();
	}
	public double getBorderWidth() {return borderWidth;}
	public void setBorderColor(Color col) {
		borderColor = col;
		appearanceUpdated();
	}

	public Color getBorderColor() {return borderColor;}
	public int[] getBorderSides() {return borderSides;}
	
	public void setRounded(boolean r) {rounded = r;}
	public void setRounded(int a) {
		rounded = true;
		arcSize = a;
		appearanceUpdated();
	}
	public void setRounded(int[] corners) {
		rounded = true;
		roundedCorners = corners;
		appearanceUpdated();
	}
	public void setRounded(int[] corners, int arcSize) {
		rounded = true;
		roundedCorners = corners;
		this.arcSize = arcSize;
		appearanceUpdated();
	}
	public boolean isRounded() {return rounded;}
	public int[] getRoundedCorners() {return roundedCorners;}

	public void setArcSize(int arcSize) {
		this.arcSize = arcSize;
		appearanceUpdated();
	}
	public int getArcSize() {return arcSize;}
	
	public void setOval(boolean o) {
		oval = o;
		appearanceUpdated();
	}
	public boolean isOval() {return oval;}

	protected void appearanceUpdated() {
		if (inDOM()) IO.getInstance().requestPaint();
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (draw) GUI.getScreenUtils().drawSimpleBox(g, this);
		super.draw(g);
	}
}
