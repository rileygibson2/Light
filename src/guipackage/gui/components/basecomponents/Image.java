package guipackage.gui.components.basecomponents;

import java.awt.Graphics2D;

import guipackage.general.UnitRectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;

public class Image extends Component {

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
