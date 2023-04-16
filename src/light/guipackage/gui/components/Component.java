package light.guipackage.gui.components;

import light.guipackage.cli.CLI;
import light.guipackage.general.Point;
import light.guipackage.general.Submitter;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.gui.Element;

public abstract class Component extends Element {

	private boolean selected;
	private boolean hovered;
	private boolean pauseHover; //Hover effects are pause when this is true

	private Submitter<Point> onClick;
	private Runnable onClickSimple; //Runnable onclick method
	private Runnable onHover;
	private Runnable onUnHover;

	//Visual
	private boolean visible;
	private int priority;
	private double opacity;

	private boolean hasShadow;
	private boolean freezeShadow; //Stops shadow being updated with position changes
	private UnitRectangle shadowR;
	private String testName;

	public Component(UnitRectangle r) {
		super (r);
		selected = false;
		hovered = false;
		pauseHover = false;
		hasShadow = false;
		freezeShadow = false;
		shadowR = r.clone();
		visible = true;
		priority = 1;
		opacity = 100;
	}
	
	public String getTestName() {return testName;}
	public void setTestName(String testName) {this.testName = testName;}

	public void setClickAction(Submitter<Point> s) {this.onClick = s;}
	public void setClickAction(Runnable r) {this.onClickSimple = r;}
	public boolean hasClickAction() {return this.onClick!=null||this.onClickSimple!=null;}
	public void setHoverAction(Runnable r) {this.onHover = r;}
	public void setUnHoverAction(Runnable r) {this.onUnHover = r;}

	public void increasePriority() {this.priority += 1;}
	public void decreasePriority() {this.priority -= 1;}
	public int getPriority() {return priority;}

	public double getOpacity() {return opacity;}
	
	public Component setOpacity(double d) {
		opacity = d;
		for (Component c : getComponents()) c.setOpacity(d); //Recur down
		return this; //To allow for chaining
	}

	public boolean isSelected() {return selected;}
	public void setSelected(boolean s) {selected = s;}

	public boolean isHovered() {return hovered;}
	public boolean isHoverPaused() {return pauseHover;}
	public void pauseHover() {pauseHover = true;}
	public void unpauseHover() {pauseHover = false;}

	public boolean hasShadow() {return hasShadow;}
	public void hasShadow(boolean s) {hasShadow = s;} 
	public UnitRectangle getShadowRec() {return shadowR;}
	public void freezeShadow() {freezeShadow = true;}
	public void unfreezeShadow() {freezeShadow = false;}

	public void setVisible(boolean v) {visible = v;}
	public boolean isVisible() {return visible;}
	
	public void removeFromParent() {
		if (getParent()!=null) getParent().removeComponent(this);
	}

	/**
	 * Runs a click action.
	 * If this element has no click action registered then this method will return false
	 * Element super class does job of verifying click area and that all children subtrees don't
	 * have a click action to preform. This is why method returns false if super method says dont preform action.
	 */
	@Override
	public boolean doClick(Point p) {
		if (!super.doClick(p)) return true; //Super says cannot click as child has clicked so return signal saying this has clicked
		selected = true;
		if (onClick!=null) onClick.submit(p);
		if (onClickSimple!=null) onClickSimple.run();
		return hasClickAction();
	}

	public void doDeselect() {selected = false;}

	public void doHover() {
		if (pauseHover) return;
		if (!hovered) {
			hovered = true;
			if (onHover!=null) {
				onHover.run();
			}
		}
	}

	public void doUnhover() {
		if (pauseHover) return;
		if (hovered) {
			hovered = false;
			if (onUnHover!=null) onUnHover.run();
		}
		
		for (Component c : getComponents()) c.doUnhover();
	}

	@Override
	public void setX(UnitValue x) {
		super.setX(x);
		if (!freezeShadow) shadowR.x.v = x.v;
	}

	@Override
	public void setY(UnitValue y) {
		super.setY(y);
		if (!freezeShadow) shadowR.y.v = y.v;
	}

	@Override
	public void setWidth(UnitValue width) {
		super.setWidth(width);
		if (!freezeShadow) shadowR.width = width;
	}

	@Override
	public void setHeight(UnitValue height) {
		super.setHeight(height);
		if (!freezeShadow) shadowR.height = height;
	}
}
