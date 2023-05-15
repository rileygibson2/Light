package light.guipackage.gui.components;

import java.util.HashSet;
import java.util.Set;

import light.general.Submitter;
import light.guipackage.general.Point;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.gui.Element;

public abstract class Component extends Element {

	//Static selection
	private static Component selectedComponent;

	private Submitter<Point> onClick;
	private Runnable onClickSimple; //Runnable onclick method
	private Runnable onHover;
	private Runnable onUnhover;
	private Runnable onSelect;
	private Runnable onDeselect;

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
	public boolean hasHoverAction() {return this.onHover!=null;}
	public Runnable getHoverAction() {return this.onHover;}

	public void setUnhoverAction(Runnable r) {this.onUnhover = r;}
	public boolean hasUnhoverAction() {return this.onUnhover!=null;}
	public Runnable getUnhoverAction() {return this.onUnhover;}

	public void setSelectAction(Runnable r) {this.onSelect = r;}
	public boolean hasSelectAction() {return this.onSelect!=null;}
	public Runnable getSelectAction() {return this.onSelect;}

	public void setDeselectAction(Runnable r) {this.onDeselect = r;}
	public boolean hasDeselectAction() {return this.onDeselect!=null;}
	public Runnable getDeselectAction() {return this.onDeselect;}

	public void increasePriority() {this.priority += 1;}
	public void decreasePriority() {this.priority -= 1;}
	public int getPriority() {return priority;}

	public double getOpacity() {return opacity;}
	
	public Component setOpacity(double d) {
		opacity = d;
		for (Component c : getComponents()) c.setOpacity(d); //Recur down
		return this; //To allow for chaining
	}

	public boolean isSelected() {return selectedComponent==this;}

	public boolean hasShadow() {return hasShadow;}
	public void hasShadow(boolean s) {hasShadow = s;} 
	public UnitRectangle getShadowRec() {return shadowR;}
	public void freezeShadow() {freezeShadow = true;}
	public void unfreezeShadow() {freezeShadow = false;}

	public void setVisible(boolean v) {visible = v;}
	@Override
	public boolean isVisible() {
		return super.isVisible()&&visible;
	}
	
	public void removeFromParent() {
		if (getParent()!=null) getParent().removeComponent(this);
	}

	/**
	 * Runs a click action.
	 * If this element has no click action registered then this method will return false
	 * Element super class does job of verifying click area and that all children subtrees don't
	 * have a click action to preform. This is why method returns true if super method says dont preform action.
	 * 
	 * @return Whether this element or an element in the subtree has preformed a click action
	 */
	@Override
	public boolean doClick(Point p) {
		if (!super.doClick(p)) return true; //Super says cannot click as child has clicked so return signal saying this has clicked
		/**
		 * At the point this code is being run we are sure that no child/child subtree has
		 * performed a click action so this element is next in line for a valid click.
		 * With this we can confirm that only one element (or none) will run the following lines
		 * per click event
		 */
		if (onClick!=null) onClick.submit(p);
		if (onClickSimple!=null) onClickSimple.run();

		//Handle selection
		if (selectedComponent!=null&&selectedComponent!=this&&selectedComponent.hasDeselectAction()) {
			selectedComponent.getDeselectAction().run();
		}
		selectedComponent = this;

		return hasClickAction();
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
