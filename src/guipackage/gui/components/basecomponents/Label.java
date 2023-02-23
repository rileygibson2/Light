package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.gui.GUI;
import guipackage.gui.ScreenUtils;
import guipackage.gui.components.Component;

public class Label extends Component {

	private String text;
	public Font font;
	public Color col;
	/**
	 * If set then label will be drawn centered on point, else
	 * will be drawn left centered to point
	 */
	private boolean xCentered;
	private boolean yCentered; 
	
	public Label(Point point, String text, Font font, Color col) {
		super(new Rectangle(point.x, point.y, 0, 0));
		this.text = text;
		this.font = font;
		this.col = col;
		xCentered = false;
		yCentered = false;
	}

	public String getText() {return text;}

	public void setText(String t) {
		text = t;
		r.width = GUI.getScreenUtils().getStringWidthAsPerc(font, text);
	}
	
	public boolean isXCentered() {return xCentered;}

	public boolean isYCentered() {return yCentered;}

	public void setCentered(boolean c) {
		xCentered = c;
		yCentered = c;
	}

	public void setXCentered(boolean c) {xCentered = c;}

	public void setYCentered(boolean c) {yCentered = c;}

	@Override
	public void draw(Graphics2D g) {
		GUI.getInstance().getScreenUtils().drawLabel(g, this);
		super.draw(g);
	}
}
