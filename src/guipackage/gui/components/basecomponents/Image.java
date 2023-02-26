package guipackage.gui.components.basecomponents;

import java.awt.Graphics2D;

import guipackage.general.UnitRectangle;
import guipackage.gui.GUI;

public class Image extends SimpleBox {

	public String src;
	
	public Image(UnitRectangle r, String src) {
		super(r);
		this.src = src;
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		GUI.getScreenUtils().drawImage(g, this);
	}

}
