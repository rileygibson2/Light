package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Graphics2D;

import guipackage.cli.CLI;
import guipackage.general.Rectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;

public class SimpleBox extends Component {

	private Color col;
	private boolean filled;
	private boolean rounded;
	private boolean oval;
	private int[] roundedCorners;

	private boolean draw;
	
	public SimpleBox(Rectangle r) {
		super(r);
		filled = true;
		draw = false;
	}
	
	public SimpleBox(Rectangle r, Color col) {
		super(r);
		this.col = col;
		filled = true;
		rounded = false;
		oval = false;
		draw = true;
	}
	
	public Color getColor() {return col;}
	public void setColor(Color c) {col = c;}
	
	public void setFilled(boolean f) {filled = f;}
	public boolean isFilled() {return filled;}
	
	public void setRounded(boolean r) {rounded = r;}
	public void setRounded(int[] r) {
		rounded = true;
		roundedCorners = r;
	}
	public boolean isRounded() {return rounded;}
	public int[] getRoundedCorners() {return roundedCorners;}
	
	public void setOval(boolean o) {oval = o;}
	public boolean isOval() {return oval;}
	
	@Override
	public void draw(Graphics2D g) {
		if (draw) GUI.getInstance().getScreenUtils().drawSimpleBox(g, this);
		super.draw(g);
	}
}
