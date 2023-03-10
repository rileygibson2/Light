package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import guipackage.general.UnitRectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.boxes.SimpleBox;

public class Label extends SimpleBox {

	private String text;
	public Font font;
	public Color col;
	/**
	 * If set then label will be drawn centered on point, else
	 * will be drawn left centered to point
	 */
	private boolean xCentered;
	private boolean yCentered; 
	
	public Label(UnitRectangle r, String text, Font font, Color col) {
		super(r);
		this.text = text;
		this.font = font;
		this.col = col;
	}

	public String getText() {return text;}

	public void setText(String t) {text = t;}

	public void setTextColor(Color col) {this.col = col;}
	
	public boolean isTextXCentered() {return xCentered;}

	public boolean isTextYCentered() {return yCentered;}

	public void setTextCentered(boolean c) {
		xCentered = c;
		yCentered = c;
	}

	public void setTextXCentered(boolean c) {xCentered = c;}

	public void setTextYCentered(boolean c) {yCentered = c;}

	/**
	 * Will change the font size so that it fits to the label's height
	 * MUST be called after component has been inserted into DOM
	 */
	public void fitFont() {
		setDOMEntryAction(() -> font = GUI.getScreenUtils().getMaxFontForRect(font, getRealRec(), text));
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		GUI.getScreenUtils().drawLabel(g, this);
	}
}
