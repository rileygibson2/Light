package light.guipackage.gui.components.basecomponents;

import java.awt.Graphics2D;

import light.guipackage.general.UnitRectangle;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.boxes.SimpleBox;

public class Image extends SimpleBox {

	private String src;
	
	public Image(UnitRectangle r, String src) {
		super(r);
		this.src = src;
	}

	public void setSource(String src) {this.src = src;}
	public String getSource() {return src;}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		GUI.getScreenUtils().drawImage(g, this);
	}
}
