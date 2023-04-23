package light.guipackage.gui.components.basecomponents;

import java.awt.Color;

import light.guipackage.cli.CLI;
import light.guipackage.general.Point;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.components.boxes.SimpleBox;

public class ScrollBar extends SimpleBox {

	private SimpleBox handle;

	public ScrollBar() {
		super(new UnitRectangle(98, 0, 2, 100));
		setColor(new Color(35, 35, 35));

		handle = new SimpleBox(new UnitRectangle(0, 0, 100, 0));
		handle.setRounded(15);
		handle.setColor(new Color(100, 100, 100));
		addComponent(handle);
	}

	public void updateHandleHeight(UnitValue height) {
		handle.setHeightNQ(height);
	}

	public void resetHandlePosition() {
		handle.setYNQ(new UnitValue(0, Unit.pch));
	}

	@Override
	public void doDrag(Point entry, Point current) {
		double y = scalePoint(current).y*100;

		UnitValue handleHeight = handle.getHeight();
		if (handleHeight.u!=Unit.pch) return; //Saftey
		if (y+handleHeight.v>100) handle.setYNQ(new UnitValue(100-handleHeight.v, Unit.pch));
		else handle.setYNQ(new UnitValue(y, Unit.pch));

		//Prompt parent element to reposition elements for scroll
		if (hasParent()) getParent().moveElementsForScroll(new UnitValue(-handle.getY().v, Unit.pch));
	}
}
