package guipackage.gui.components.boxes;

import java.awt.Color;
import java.awt.Graphics2D;

import guipackage.general.UnitRectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;

public class SimpleBox extends Component {

	private Color col;
	private boolean filled;
	private boolean border;
	private double borderWidth;
	private Color borderColor;
	private boolean rounded;
	public int arcSize; //Rounded corner sizes
	private int[] roundedCorners;
	private boolean oval;

	private boolean draw;
	
	public SimpleBox(UnitRectangle r) {
		super(r);
		draw = false;
	}
	
	public SimpleBox(UnitRectangle r, Color col) {
		super(r);
		this.col = col;
		filled = true;
		rounded = false;
		arcSize = 10;
		oval = false;
		border = false;
		borderWidth = 1;
		draw = true;
	}
	
	public void setDraw(boolean d) {draw = d;}

	public Color getColor() {return col;}
	public void setColor(Color c) {
		draw = true;
		filled = true;
		col = c;
	}
	
	public void setFilled(boolean f) {filled = f;}
	public boolean isFilled() {return filled;}

	public void setBorder(double width, Color col)  {
		draw = true;
		border = true;
		borderWidth = width;
		borderColor = col;
	}
	public void setBorder(Color col)  {
		draw = true;
		border = true;
		borderColor = col;
	}
	public void removeBorder() {border = false;}
	public boolean hasBorder() {return border;}
	public double getBorderWidth() {return borderWidth;}
	public Color getBorderColor() {return borderColor;}
	
	public void setRounded(boolean r) {rounded = r;}
	public void setRounded(int a) {
		rounded = true;
		arcSize = a;
	}
	public void setRounded(int[] r) {
		rounded = true;
		roundedCorners = r;
	}
	public boolean isRounded() {return rounded;}
	public int[] getRoundedCorners() {return roundedCorners;}
	public void setArcSize(int a) {arcSize = a;}
	public int getArcSize() {return arcSize;}
	
	public void setOval(boolean o) {oval = o;}
	public boolean isOval() {return oval;}
	
	@Override
	public void draw(Graphics2D g) {
		if (draw) GUI.getScreenUtils().drawSimpleBox(g, this);
		super.draw(g);
	}
}
