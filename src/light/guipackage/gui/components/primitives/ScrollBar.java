package light.guipackage.gui.components.primitives;

import java.awt.Color;

import light.guipackage.general.Point;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.components.primitives.boxes.SimpleBox;

public class ScrollBar extends SimpleBox {
	
	public enum ScrollDir {X, Y};
	
	private ScrollDir direction;
	private SimpleBox handle;
	private double entryOffset = 0; //Used to offset for point on handle being dragged
	
	public ScrollBar(ScrollDir direction) {
		super(new UnitRectangle(0, 0, 0, 0));
		this.direction = direction;
		setColor(new Color(35, 35, 35));

		
		handle = new SimpleBox(new UnitRectangle(0, 0, 0, 0));
		handle.setRounded(15);
		handle.setColor(new Color(100, 100, 100));

		if (direction==ScrollDir.Y) {
			setRec(new UnitRectangle(98, 0, 2, 100));
			handle.setWidth(new UnitValue(100, Unit.pcw));
		}
		else {
			setRec(new UnitRectangle(0, 98, 100, 2));
			handle.setHeight(new UnitValue(100, Unit.pch));
		}

		addComponent(handle);
	}
	
	public void updateHandle(UnitValue size) {
		switch (direction) {
			case Y:
			if (size.u!=Unit.pch) return;
			handle.setHeightNQ(size);
			break;
			case X:
			if (size.u!=Unit.pcw) return;
			handle.setWidthNQ(size);
			break;
		}
	}
	
	public void resetHandlePosition() {
		if (direction==ScrollDir.Y) handle.setYNQ(new UnitValue(0, Unit.pch));
		else if (direction==ScrollDir.X) handle.setXNQ(new UnitValue(0, Unit.pcw));
	}
	
	@Override
	public void doDrag(Point entry, Point current) {
		if (direction==ScrollDir.Y) {
			double y = scalePoint(current).y*100;
			
			if (current.equals(entry)) {
				entryOffset = y-handle.getY().v;
			}
			
			UnitValue handleHeight = handle.getHeight();
			y -= entryOffset; //Need to offset for point on handle being dragged
			
			if (y+handleHeight.v>100) handle.setYNQ(new UnitValue(100-handleHeight.v, Unit.pch));
			else if (y<0) handle.setYNQ(new UnitValue(0, Unit.pch));
			else handle.setYNQ(new UnitValue(y, Unit.pch));
			
			//Prompt parent element to reposition elements for scroll
			if (hasParent()) getParent().moveElementsForScroll(new UnitValue(-handle.getY().v, Unit.pch), Overflow.ScrollY);
		}
		else if (direction==ScrollDir.X) {
			double x = scalePoint(current).x*100;
			
			if (current.equals(entry)) {
				entryOffset = x-handle.getX().v;
			}
			
			UnitValue handleWidth = handle.getWidth();
			x -= entryOffset; //Need to offset for point on handle being dragged
			
			if (x+handleWidth.v>100) handle.setXNQ(new UnitValue(100-handleWidth.v, Unit.pcw));
			else if (x<0) handle.setXNQ(new UnitValue(0, Unit.pcw));
			else handle.setXNQ(new UnitValue(x, Unit.pcw));
			
			//Prompt parent element to reposition elements for scroll
			if (hasParent()) getParent().moveElementsForScroll(new UnitValue(-handle.getX().v, Unit.pcw), Overflow.ScrollX);
		}
	}
}
