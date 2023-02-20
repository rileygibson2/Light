package guipackage.gui.views;

import guipackage.general.Rectangle;
import guipackage.gui.Element;

public abstract class View extends Element {

	protected View(Rectangle r) {
		super(r);
	}
	
	public abstract void enter();
}
