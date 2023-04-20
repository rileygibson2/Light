package light.guipackage.gui.components.basecomponents;

import java.awt.Graphics2D;

import light.guipackage.general.UnitRectangle;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.boxes.SimpleBox;

public class Image extends SimpleBox {

	private String src;
	private boolean ignoreCache;
	private boolean makeImageTransparent;
	
	public Image(UnitRectangle r, String src) {
		super(r);
		this.src = src;
		this.ignoreCache = false;
		this.makeImageTransparent = false;
	}

	public void setSource(String src) {
		this.src = src;
		ignoreCache = true;
	}
	public String getSource() {return src;}

	public void setIgnoreCache(boolean ignoreCache) {this.ignoreCache = ignoreCache;}
	public boolean getIgnoreCache() {return ignoreCache;}

	public void setMakeImageTransparent(boolean b) {this.makeImageTransparent = b;}
	public boolean getMakeImageTransparent() {return makeImageTransparent;}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		GUI.getScreenUtils().drawImage(g, this);
	}
}
