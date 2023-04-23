package light.guipackage.gui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import light.guipackage.cli.CLI;
import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.basecomponents.ScrollBar;
import light.guipackage.gui.components.boxes.FlexBox;

public abstract class Element {
	
	private Element parent;
	private UnitRectangle r; //Current values set by and visible to creator element - these stay the same regardless of positioning
	private UnitRectangle rFunc; //Functional/actual values used by this class to implement positioning
	
	private UnitPoint minSize;
	private UnitPoint maxSize;
	private List<Component> components;
	private Lock componentsLock; //Primitive lock on components list
	
	private Runnable domEntryAction; //Action to run when element is inserted into DOM - allows for logic that requires a place in the dom to run
	private boolean inDOM; //Whether or not this element is in the DOM ie the root is visible from this element
	
	private boolean isRoot; //Set on the root element of the DOM
	
	public enum Position {
		Absolute, //Dimensions taken from top left of parent element
		Relative, //Dimensions taken from top left of last sibling element
		CollumnRelative, //Y Dimensions are taken from bottom of last sibling element
		GlobalFixed //Dimensions taken from top left of screen
	}
	private Position position;
	
	public enum Float {
		Left, //Position is taken from top left of parent (if absolute) and from top right of sibling (if relative)
		Right //Position is taken from top right of parent (if absolute) and from top left of sibling (if relative)
	}
	private Float floatType;
	
	public enum Fill {
		Horizontal, //Element's width fills space between it and the next element beside it (or between it and the edge of the element)
		Vertical, //Element's height fills space between it and the next element below it (or between it and the bottom of the element)
		None
	}
	private Fill fill;
	
	public enum Center {
		xCentered,
		yCentered,
		xyCentered,
		None;
		
		public boolean isXCentered() {return this==Center.xCentered||this==Center.xyCentered;}
		public boolean isYCentered() {return this==Center.xCentered||this==Center.xyCentered;}
	}
	private Center centered;
	
	public enum Overflow {
		Default, //Content overflows bounding element
		Hidden, //Overflowing content is hidden TODO
		Scroll //Overflowing content is hidden and scroll bar is added
	}
	private Overflow overflow;
	
	private ScrollBar scrollBar;
	private boolean hiddenForScroll; //Whether not this element has been hidden to facilitate scrolling
	private Element clipElement; //If an element needs to be clipped rather then hidden for overflow then this variable will have a value
	private UnitValue yScrollOffset; //Used to offset elements during scroll - will be a percentage of parent height
	
	private Object tag;
	public String tagString;
	
	public Element(UnitRectangle r) {
		this.r = r;
		this.rFunc = r.clone();
		this.minSize = new UnitPoint(0, Unit.px, 0, Unit.px);
		this.maxSize = new UnitPoint(GUI.screen.width, Unit.px, GUI.screen.height, Unit.px);
		this.components = new ArrayList<Component>();
		componentsLock = new ReentrantLock();
		position = Position.Absolute;
		floatType = Float.Left;
		fill = Fill.None;
		centered = Center.None;
		overflow = Overflow.Default;
	}
	
	public void setX(UnitValue p) {
		r.x = p;
		doPositioning();
		if (parent!=null) parent.subtreeUpdated();
	}
	public void setY(UnitValue p) {
		r.y = p;
		if (parent!=null) parent.subtreeUpdated();
	}
	public void setWidth(UnitValue p) {
		r.width = p;
		doPositioning();
		if (parent!=null) parent.subtreeUpdated();
	}
	public void setHeight(UnitValue p) {
		r.height = p;
		doPositioning();
		if (parent!=null) parent.subtreeUpdated();
	}
	
	/**
	* Same as setX() but does not trigger an update chain throughout rest of DOM elements - has 
	* no consequences
	* @param p
	*/
	public void setXNQ(UnitValue p) {
		r.x = p;
		doPositioning();
	}
	public void setYNQ(UnitValue p) {
		r.y = p;
		doPositioning();
	}
	public void setWidthNQ(UnitValue p) {
		r.width = p;
		doPositioning();
	}
	public void setHeightNQ(UnitValue p) {
		r.height = p;
		doPositioning();
	}
	
	public void setMinWidth(UnitValue p) {
		minSize.x = p;
		doPositioning();
		if (parent!=null) parent.subtreeUpdated();
	}
	public void setMinHeight(UnitValue p) {
		minSize.y = p;
		doPositioning();
		if (parent!=null) parent.subtreeUpdated();
	}
	public void setMinSize(UnitPoint p) {
		this.minSize = p;
		doPositioning();
		if (parent!=null) parent.subtreeUpdated();
	}
	
	public void setMaxWidth(UnitValue p) {
		maxSize.x = p;
		doPositioning();
		if (parent!=null) parent.subtreeUpdated();
	}
	public void setMaxHeight(UnitValue p) {
		maxSize.y = p;
		doPositioning();
		if (parent!=null) parent.subtreeUpdated();
	}
	public void setMaxSize(UnitPoint p)  {
		this.maxSize = p;
		doPositioning();
		if (parent!=null) parent.subtreeUpdated();
	}
	
	public UnitValue getX() {return r.x;}
	public UnitValue getY() {return r.y;}
	public UnitValue getWidth() {return r.width;}
	public UnitValue getHeight() {return r.height;}
	public UnitValue getFuncX() {return rFunc.x;}
	public UnitValue getFuncY() {return rFunc.y;}
	public UnitValue getFuncWidth() {return rFunc.width;}
	public UnitValue getFuncHeight() {return rFunc.height;}
	public UnitValue getMinWidth() {return minSize.x;}
	public UnitValue getMinHeight() {return minSize.y;}
	public UnitValue getMaxWidth() {return maxSize.x;}
	public UnitValue getMaxHeight() {return maxSize.y;}
	
	public void setRec(UnitRectangle r) {
		this.r = r;
		doPositioning();
		if (parent!=null) parent.subtreeUpdated();
	}
	public UnitRectangle getRec() {return r;}
	public UnitRectangle getFunctionalRec() {return rFunc;}
	
	public Position getPosition() {return position;}
	public void setPosition(Position p) {position = p;}
	
	public void setFloat(Float f) {this.floatType = f;}
	public Float getFloat() {return floatType;}
	
	public void setFill(Fill f) {this.fill = f;}
	public Fill getFill() {return fill;}
	
	public void setCentered(Center c) {
		this.centered = c;
		if (c!=Center.None) setPosition(Position.Absolute);
	}
	public Center getCentered() {return centered;}
	
	public void setOverflow(Overflow o) {this.overflow = o;}
	public Overflow getOverflow() {return overflow;}
	
	public boolean isHiddenForScroll() {return hiddenForScroll;}
	protected void setHiddenForScroll(boolean hidden) {this.hiddenForScroll = hidden;}
	
	public boolean hasClippingElement() {return clipElement!=null;}
	protected void setClippingElement(Element element) {this.clipElement = element;}
	
	public boolean hasYScrollOffset() {return yScrollOffset!=null;}
	public UnitValue getYScrollOffset() {return yScrollOffset;}
	protected void setYScrollOffset(UnitValue offset) {this.yScrollOffset = offset;}
	
	public Element getParent() {return parent;}
	public void setParent(Element e) {parent = e;}
	public boolean hasParent() {return parent!=null;}
	
	public void setDOMEntryAction(Runnable r) {domEntryAction = r;}
	public boolean inDOM() {return inDOM;}
	
	public void setRoot() {
		isRoot = true;
		setPosition(Position.GlobalFixed);
	}
	public boolean isRoot() {return isRoot;}
	
	public void setTag(Object t) {tag = t;}
	public boolean hasTag() {return tag!=null;}
	public Object getTag() {return tag;}
	
	public void setTagString(String t) {tagString = t;}
	public boolean hasTagString() {return tagString!=null;}
	public String getTagString() {return tagString;}
	
	public List<Component> getComponents() {
		List<Component> copy = new ArrayList<>();
		componentsLock.lock();
		for (Component c : components) copy.add(c);
		componentsLock.unlock();
		return copy;
	}
	
	public List<Component> getComponents(Class<?> clazz) {
		List<Component> results = new ArrayList<>();
		componentsLock.lock();
		for (Component c : components) {
			if (clazz.isInstance(c)) results.add(c);
		}
		componentsLock.unlock();
		if (!results.isEmpty()) return results;
		return null;
	}
	
	public Component getNthComponent(int n) {
		if (n<0||n>=components.size()) return null;
		componentsLock.lock();
		Component c =  components.get(n);
		componentsLock.unlock();
		return c;
	}
	
	private List<Component> getSortedReversedComponents() {
		List<Component> co = getComponents();
		
		Collections.sort(co, new Comparator<Component>() {
			public int compare(Component c1, Component c2) {
				return c1.getPriority()-c2.getPriority();
			}
		});
		
		Collections.reverse(co);
		return co;
	}
	
	private List<Component> getSortedComponents() {
		List<Component> co = getComponents();
		
		Collections.sort(co, new Comparator<Component>() {
			public int compare(Component c1, Component c2) {
				return c1.getPriority()-c2.getPriority();
			}
		});
		return co;
	}
	
	public int getNumComponents() {
		componentsLock.lock();
		int num = components.size();
		componentsLock.unlock();
		return num;
	}
	
	/**
	* Adds a component to this element
	* @param c
	*/
	public void addComponent(Component c) {
		addComponent(c, getNumComponents());
	}
	
	/**
	* Adds a component as the first child of this element
	* @param c
	*/
	public void addComponentAtFront(Component c) {
		addComponent(c, 0);
	}
	
	/**
	* Adds a component to this element at the specified index
	* @param c
	*/
	public void addComponentAtIndex(Component c, int index) {
		if (index>=0&&index<=getNumComponents()) addComponent(c, index);
	}
	
	private void addComponent(Component c, int index) {
		if (components.contains(c)) return;
		
		c.setParent(this);
		componentsLock.lock();
		components.add(index, c);
		componentsLock.unlock();
		
		c.doPositioning();
		subtreeUpdated();
		
		/*
		* Trigger DOM entry action if this is the root node adding a component
		* Also trigger if this is another element is already in dom
		*/
		if (isRoot()||inDOM) c.triggerDOMEntry();
	}
	
	public void removeComponent(Component c) {
		if (c==null) return;
		componentsLock.lock();
		components.remove(c);
		componentsLock.unlock();
		
		c.triggerDOMExit();
		subtreeUpdated();
	}
	
	public void removeComponents(Collection<Component> toRemove) {
		if (toRemove==null||toRemove.isEmpty()) return;
		componentsLock.lock();
		components.removeAll(toRemove);
		componentsLock.unlock();
		
		for (Component c : toRemove) c.triggerDOMExit();
		subtreeUpdated();
	}
	
	public void clearComponents() {
		componentsLock.lock();
		components.clear();
		componentsLock.unlock();
	}
	
	/**
	* Recursivly triggers DOM entry action for this element and all children
	*/
	protected void triggerDOMEntry() {
		inDOM = true;
		if (domEntryAction!=null) domEntryAction.run();
		for (Component c : components) c.triggerDOMEntry();
	}
	
	protected void triggerDOMExit() {
		inDOM = false;
		for (Component c : components) c.triggerDOMExit();
	}
	
	/**
	* Update hook for when a child of this element has had it's dimensions updated.
	* Will propogate up the DOM tree.
	* Can be overridden to create custom actions for when a child is changed, for example FlexBox uses it
	* to resize itself to accomodate the updated child
	* 
	* This will trigger an update all children which will updated all children, even the one who caused this
	* child updated call, as it is possible that the parent has changed dimensions that would require all children,
	* including the one who caused this to reposition.
	* 
	* At the current stage it is unsafe to change a property of a child inside an overridden version of this method
	* in such a way that the change to the child will happen regardless to the circumstances of the method call
	* This is because the change will cause a parent.childUpdated() call which will cause a recall of the method.
	* 
	* That said it is safe to change an element's own propertys inside an overriden version of this method,
	* as that will simply result in a call of parent.childUpdated() which will propogate up and a call to
	* updateChildren() which will propogate down, but no loop.
	* 
	* To get around this there are public methods which change an elements propertys with no propogation and no consequence.
	*/
	protected void subtreeUpdated() {
		if (parent!=null) parent.subtreeUpdated(); //Propogate upwards
		positionSubtree(); //Propogate downwards
	}
	
	/**
	* Trigger sibling updated hook in all children
	*/
	protected void positionSubtree() {
		doPositioning();
		for (Component c : getComponents()) c.positionSubtree();
	}
	
	/**
	* Positions this element where it should be on the screen.
	* Implements relative, absolute and static positioning, float and fill.
	* Translates the user visible r into rFunc which is used to draw and interact with this element.
	
	* Rules:
	* Elements with position relative are only relative to other relative elements
	* Elements with position relative are only relative to other elements with the same float as them
	* Min max width overrides the fill width
	* Position relative does not work for centered elements
	*/
	protected void doPositioning() {
		rFunc = r.clone(); //This is all needed for absolute and fixed positioning
		checkMinMaxSize();
		
		if (parent==null) return; //Nothing below this will work without a parent
		
		//Do centered
		if (centered.isXCentered()) xCenterElementInParent();
		if (centered.isYCentered()) yCenterElementInParent();
		/*
		* An element with centered set should never be relative and should not respect it's
		* float, fill or overflow property as center overrides all of these so safe to return here
		*/
		if (centered!=Center.None) return;
		
		/*
		* Float right should be done from right end of parent box if position is not relative OR
		* if position is relative but there are no eligable elements added before this element.
		* This is done so that relative chaining can still work. Position relative uses values
		* of last added eligable element as a reference, and so needs the first relative element to still
		* be normally floated right for this to work.
		* 
		* Left float is the default behaviour so no stuff needed for that here
		*/
		
		//Do float
		if (getFloat()==Float.Right&&getPosition()!=Position.Relative) floatRight();
		
		/*
		* Do relative positioning.
		* Case mentioned above when float is right and there ARE other elements is respected within
		* the call below.
		*/
		if (getPosition()==Position.Relative) positionRelatively();
		
		//Do collumn relative positioning
		if (getPosition()==Position.CollumnRelative) positionCollumnRelatively();
		
		//Do fill
		if (getFill()!=Fill.None) fillToNextElement();
		checkMinMaxSize();
		
		//Do overflow
		if (getOverflow()==Overflow.Scroll) doOverlowScroll();
	}
	
	/**
	* Validates size is not smaller than min values
	*/
	protected void checkMinMaxSize() {
		//Check min width
		UnitValue size = translateToUnit(getWidth(), this, getMinWidth().u, this);
		if (size.v<getMinWidth().v) rFunc.width = getMinWidth().clone();
		
		//Check min height
		size = translateToUnit(getHeight(), this, getMinHeight().u, this);
		if (size.v<getMinHeight().v) rFunc.height = getMinHeight().clone();
		
		//Check max width
		size = translateToUnit(getWidth(), this, getMaxWidth().u, this);
		if (size.v>getMaxWidth().v) rFunc.width = getMaxWidth().clone();
		
		//Check max height
		size = translateToUnit(getHeight(), this, getMaxHeight().u, this);
		if (size.v>getMaxHeight().v) rFunc.height = getMaxHeight().clone();
	}
	
	private void positionRelatively() {
		List<Component> siblings = parent.getComponents();
		if (siblings==null||siblings.isEmpty()) return;
		
		//Find location of this element in siblings
		int loc = 0;
		for (; loc<siblings.size(); loc++) if (siblings.get(loc)==this) break;
		
		//Get last added eligable relative component
		Component lastRelativeSibling = null;
		for (int i=loc; i>=0; i--) { //Search back from this component
			Component sibling = siblings.get(i);
			if (sibling!=this&&sibling.getPosition()==Position.Relative&&sibling.getFloat()==this.getFloat()) {
				lastRelativeSibling = sibling;
				break;
			}
		}
		
		if (lastRelativeSibling!=null) {
			UnitValue sibX = translateToUnit(lastRelativeSibling.getFuncX(), lastRelativeSibling, getX().u, this);
			UnitValue sibY = translateToUnit(lastRelativeSibling.getFuncY(), lastRelativeSibling, getY().u, this);
			
			//Adjust this elements position values to base off siblings
			if (getFloat()==Float.Left) { //X from top right of sibling
				UnitValue sibWidth = translateToUnit(lastRelativeSibling.getFuncWidth(), lastRelativeSibling, getX().u, this);
				double x = sibX.v+sibWidth.v+getX().v;
				
				//Check if new position will overflow parent width
				UnitValue parentW = translateToUnit(getParent().getFuncWidth(), getParent(), getX().u, this);
				//Special case for flex box - should consider max width not actual width as should still be allowed to resize
				if (getParent() instanceof FlexBox) parentW = translateToUnit(getParent().getMaxWidth(), getParent(), getX().u, this);
				
				if (parentW==null) return; //TODO bodge fix but will be aight because positioning happens so often
				
				UnitValue width = translateToUnit(getFuncWidth(), this, getX().u, this);
				if (x+width.v>parentW.v+2) {
					//Position at left of element but down a 'row'
					UnitValue sibHeight = translateToUnit(lastRelativeSibling.getFuncHeight(), lastRelativeSibling, getY().u, this);
					rFunc.x = getX().clone();
					rFunc.y = new UnitValue(sibY.v+sibHeight.v+getY().v, getY().u);
				}
				else { //Position from sibling
					rFunc.x = new UnitValue(x, getX().u);
					rFunc.y = new UnitValue(getY().v+sibY.v, getY().u);
				}
			}
			else if (getFloat()==Float.Right) { //X backwards from top left of sibling
				UnitValue width = translateToUnit(getFuncWidth(), this, getX().u, this);
				double x = sibX.v-width.v-getX().v;
				
				//Check if new position will overflow parent
				if (x-width.v<0) {
					//Position at right of element but down a 'row'
					UnitValue sibHeight = translateToUnit(lastRelativeSibling.getFuncHeight(), lastRelativeSibling, getY().u, this);
					floatRight();
					rFunc.y = new UnitValue(sibY.v+sibHeight.v+getY().v, getY().u);
				}
				else { //Position from sibling
					rFunc.x = new UnitValue(x, getX().u);
					rFunc.y = new UnitValue(getY().v+sibY.v, getY().u);
				}
			}
		}
		//No eligable relative sibling, still need to handle float as described above
		else if (getFloat()==Float.Right) floatRight();
	}
	
	private void positionCollumnRelatively() {
		List<Component> siblings = parent.getComponents();
		if (siblings==null||siblings.isEmpty()) return;
		
		//Find location of this element in siblings
		int loc = 0;
		for (; loc<siblings.size(); loc++) if (siblings.get(loc)==this) break;
		
		//Get last added eligable relative component
		Component lastSibling = null;
		for (int i=loc; i>=0; i--) { //Search back from this component
			Component sibling = siblings.get(i);
			if (sibling!=this) {
				lastSibling = sibling;
				break;
			}
		}
		if (lastSibling==null) return;
		
		//Position this element vertically down from last sibling
		UnitValue sibY = translateToUnit(lastSibling.getFuncY(), lastSibling, getY().u, this);
		UnitValue sibHeight = translateToUnit(lastSibling.getFuncHeight(), lastSibling, getY().u, this);
		rFunc.y = new UnitValue(sibY.v+sibHeight.v+getY().v, getY().u);
	}
	
	private void floatRight() {
		if (parent==null) return;
		UnitValue parentW = translateToUnit(parent.getFuncWidth(), parent, getX().u, this);
		UnitValue width = translateToUnit(getWidth(), this, getX().u, this);
		rFunc.x = new UnitValue(parentW.v-width.v-getX().v, getX().u);
	}
	
	private void fillToNextElement() {
		List<Component> siblings = parent.getComponents();
		if (siblings==null||siblings.isEmpty()||siblings.size()==1) return;
		
		//Find location of this element in siblings
		int loc = 0;
		for (; loc<siblings.size(); loc++) if (siblings.get(loc)==this) break;
		
		if (loc==siblings.size()-1) {
			//Element is either has no siblings or was last added element so fill to edge of parent
			if (getFill()==Fill.Horizontal) {
				UnitValue parentW = translateToUnit(parent.getFuncWidth(), parent, getWidth().u, this);
				UnitValue posX = translateToUnit(getX(), this, getWidth().u, this);
				rFunc.width = new UnitValue(parentW.v-posX.v, getWidth().u);
			}
			if (getFill()==Fill.Vertical) {
				UnitValue parentH = translateToUnit(parent.getFuncHeight(), parent, getHeight().u, this);
				UnitValue posY = translateToUnit(getY(), this, getHeight().u, this);
				rFunc.height = new UnitValue(parentH.v-posY.v, getHeight().u);
			}
			return;
		}
		
		//Fill to next element
		Component next = siblings.get(loc+1);
		if (getFill()==Fill.Horizontal) {
			UnitValue nextX = translateToUnit(next.getFuncX(), next, getWidth().u, this);
			UnitValue posX = translateToUnit(getX(), this, getWidth().u, this);
			rFunc.width = new UnitValue(nextX.v-posX.v, getWidth().u);
		}
		if (getFill()==Fill.Vertical) {
			UnitValue nextY = translateToUnit(next.getFuncY(), next, getHeight().u, this);
			UnitValue posY = translateToUnit(getX(), this, getHeight().u, this);
			rFunc.height = new UnitValue(nextY.v-posY.v, getHeight().u);
		}
	}
	
	private void xCenterElementInParent() {
		if (getPosition()==Position.GlobalFixed) { //Center from top left
			UnitValue width = translateToUnit(getFuncWidth(), this, Unit.pcw, GUI.getInstance().getCurrentRoot()); 
			rFunc.x = new UnitValue(50-(width.v/2), Unit.pcw);
			return;
		}
		
		UnitValue parentW = translateToUnit(parent.getFuncWidth(), parent, getX().u, this);
		UnitValue width = translateToUnit(getFuncWidth(), this, getX().u, this); 
		rFunc.x = new UnitValue((parentW.v-width.v)/2, getX().u);
	}
	
	/**
	* Note that y centering an element in a flex box will cause flexbox to resize with new position of
	* centered element, causing flexbox to shrink to the value that is not overflowwing above, meaning
	* a little of this element will likely peek out
	*/
	private void yCenterElementInParent() {
		if (getPosition()==Position.GlobalFixed) { //Center from top left
			UnitValue height = translateToUnit(getFuncHeight(), this, Unit.pch, GUI.getInstance().getCurrentRoot()); 
			rFunc.y = new UnitValue(50-(height.v/2), Unit.pch);
			return;
		}
		
		UnitValue parentH = translateToUnit(parent.getFuncHeight(), parent, getY().u, this);
		UnitValue height = translateToUnit(getFuncHeight(), this, getY().u, this);
		rFunc.y = new UnitValue((parentH.v-height.v)/2, getY().u);
	}
	
	private void doOverlowScroll() {
		if (components==null||components.isEmpty()) return;
		
		//Find bounding box to consider
		UnitRectangle box = getFunctionalRec();
		//Special case for flex box - should consider max height
		if (this instanceof FlexBox) box.height = translateToUnit(getMaxHeight(), this, getFuncHeight().u, this);
		Rectangle bounds = getRealRec(box);
		
		//Consider all children
		for (Component child : components) child.investigateYOverflowInSubtree(bounds.y+bounds.height, this);

		implementScrollBar();
	}
	
	private void implementScrollBar() {
		if (getOverflow()!=Overflow.Scroll) return;
		
		//Implement a scrollbar
		if (scrollBar==null) {
			scrollBar = new ScrollBar();
			scrollBar.setParent(this);
		}
		/**
		* Find what percentage of the largest bounding box (i.e the box from the top of this element
		* to the bottom of the lowest overflowing element) the actual bounding box is.
		* 
		* This becomes the size of the scroll bar handle as the whole height of the scroll bar box is
		* meant to represent the whole overflowing box and the handle is meant to represent the height
		* and position of the actual bounding box in that overflowing box.
		*/

		//Find bounding box to consider
		UnitRectangle box = getFunctionalRec();
		//Special case for flex box - should consider max height
		if (this instanceof FlexBox) box.height = translateToUnit(getMaxHeight(), this, getFuncHeight().u, this);
		Rectangle bounds = getRealRec(box);

		double lowestY = 0;
		for (Component child : components) {
			double y = child.getLowestYInSubtree();
			if (y>lowestY) lowestY = y;
		}
		double perc = bounds.height/(lowestY-bounds.y);
		CLI.debug("bounding box is "+perc+" of actual real overflowing box");
		
		//Update scroll bar handle height
		scrollBar.updateHandleHeight(new UnitValue(perc*100, Unit.pch));
	}
	
	/**
	* Implements scroll controls for all elements in the subtree
	* @param yLimit
	* @param clipElement
	*/
	protected void investigateYOverflowInSubtree(double yLimit, Element clipElement) {
		Rectangle r = getRealRec();
		if (r.y>=yLimit) { //Check if all of element is outside bounds
			setHiddenForScroll(true);//No further action needs to be taken
			setClippingElement(null);
		}
		if (r.y+r.height>=yLimit) { //Check if part of element is outside box
			//This element needs to stay visible but will get clipped. Children elements need to be investigated
			setHiddenForScroll(false);
			setClippingElement(clipElement);
			for (Component child : components) child.investigateYOverflowInSubtree(yLimit, clipElement);
		}
	}
	
	protected double getLowestYInSubtree() {
		Rectangle r = getRealRec();
		double lowestY = r.y+r.height;
		for (Component child : components) {
			double y = child.getLowestYInSubtree();
			if (y>lowestY) lowestY = y;
		}
		return lowestY;
	}
	
	/**
	* Should only ever be called by scroll bar or element controlling scroll. Prompts the element
	* to move all of it's children in accordance with the bounds offset.
	* The bounds offset is the amount the bounding box that represents the view port is shifted by.
	*/
	public void moveElementsForScroll(UnitValue boundsOffset) {
		if (getOverflow()!=Overflow.Scroll) return;
		
		for (Component child : components) child.setYScrollOffset(boundsOffset);
		

		//Find bounding box to consider
		UnitRectangle box = getFunctionalRec();
		//Special case for flex box - should consider max height
		if (this instanceof FlexBox) box.height = translateToUnit(getMaxHeight(), this, getFuncHeight().u, this);
		Rectangle bounds = getRealRec(box);
		
		//Consider all children
		for (Component child : components) child.investigateYOverflowInSubtree(bounds.y+bounds.height, this);
	}
	
	/**
	* Resets all scroll control elements for this element and all down stream elements.
	*/
	protected void resetScrollControlInSubtree() {
		hiddenForScroll = false;
		clipElement = null;
		yScrollOffset = null;
		for (Component child : components) child.resetScrollControlInSubtree();
	}
	
	/**
	* Gets the rectangle that the drawing of this element should be clipped by.
	* This involves generating a real pixel unit rectangle from the element that this element has
	* been told to clop by.
	* The clipping rectangle is usually set to facilitate overflow functionality
	* @return
	*/
	public Rectangle getClippingRectangle() {
		if (clipElement==null) return null;
		//Find bounding box to conider
		UnitRectangle box = clipElement.getFunctionalRec();
		//Special case for flex box - should consider max height
		if (this instanceof FlexBox) box.height = translateToUnit(clipElement.getMaxHeight(), clipElement, clipElement.getFuncHeight().u, clipElement);
		return clipElement.getRealRec(box);
	}
	
	/**
	* Translates this elements rec to actual size in pixels on the screen.
	*
	* @return
	*/
	public Rectangle getRealRec() {
		UnitRectangle rToReal = rFunc.clone();
		if (hasYScrollOffset()) { //Need to add the offset to rFunc before realing
			rToReal.y.v = rToReal.y.v+translateToUnit(getYScrollOffset(), this, rFunc.y.u, this).v;
		}
		return getRealRec(rToReal);
	}
	
	/**
	* Allows you to translate any rectangle into real pixel values as if it were being processed
	* like this element.
	* 
	* @param r
	* @return
	*/
	protected Rectangle getRealRec(UnitRectangle r) {
		Rectangle rNew = new Rectangle(); //In px but Unit is removed as point of method is to normalise all units
		
		//Fixed positioning
		if (position==Position.GlobalFixed||isRoot()||parent==null) { //Root element should be treated as position fixed
			switch (r.x.u) {
				case pcw:
				case vw: rNew.x = GUI.getScreenUtils().cW(r.x.v); break;
				case pch:
				case vh: rNew.x = GUI.getScreenUtils().cH(r.x.v); break;
				case px: rNew.x = r.x.v; break;
			}
			switch (r.y.u) {
				case pch:
				case vh: rNew.y = GUI.getScreenUtils().cH(r.y.v); break;
				case pcw:
				case vw: rNew.y = GUI.getScreenUtils().cW(r.y.v); break;
				case px: rNew.y = r.y.v; break;
			}
			switch (r.width.u) {
				case pcw:
				case vw: rNew.width = GUI.getScreenUtils().cW(r.width.v); break;
				case pch:
				case vh: rNew.width = GUI.getScreenUtils().cH(r.width.v); break;
				case px: rNew.width = r.width.v; break;
			}
			switch (r.height.u) {
				case pch:
				case vh: rNew.height = GUI.getScreenUtils().cH(r.height.v); break;
				case pcw:
				case vw: rNew.height = GUI.getScreenUtils().cW(r.height.v); break;
				case px: rNew.height = r.height.v; break;
			}
		}
		else { //Absolute and relative positioning
			Rectangle pR = parent.getRealRec();
			switch (r.x.u) {
				case pcw: rNew.x = pR.x+(r.x.v/100d)*pR.width; break;
				case pch: rNew.x = pR.x+(r.x.v/100d)*pR.height; break;
				case vw: rNew.x = pR.x+GUI.getScreenUtils().cW(r.x.v); break;
				case vh: rNew.x = pR.x+GUI.getScreenUtils().cH(r.x.v); break;
				case px: rNew.x = pR.x+r.x.v; break;
			}
			switch (r.y.u) {
				case pcw: rNew.y = pR.y+(r.y.v/100d)*pR.width; break;
				case pch: rNew.y = pR.y+(r.y.v/100d)*pR.height; break;
				case vw: rNew.y = pR.y+GUI.getScreenUtils().cW(r.y.v); break;
				case vh: rNew.y = pR.y+GUI.getScreenUtils().cH(r.y.v); break;
				case px: rNew.y = pR.y+r.y.v; break;
			}
			switch (r.width.u) {
				case pcw: rNew.width = (r.width.v/100d)*pR.width; break;
				case pch: rNew.width = (r.width.v/100d)*pR.height; break;
				case vw: rNew.width = GUI.getScreenUtils().cW(r.width.v); break;
				case vh: rNew.width = GUI.getScreenUtils().cH(r.width.v); break;
				case px: rNew.width = r.width.v; break;
			}
			switch (r.height.u) {
				case pcw: rNew.height = (r.height.v/100d)*pR.width; break;
				case pch: rNew.height = (r.height.v/100d)*pR.height; break;
				case vw: rNew.height = GUI.getScreenUtils().cW(r.height.v); break;
				case vh: rNew.height = GUI.getScreenUtils().cH(r.height.v); break;
				case px: rNew.height = r.height.v; break;
			}
		}
		return rNew;
	}
	
	public UnitValue translateToUnit(UnitValue oldUV, Element oldScope, Unit newUnit, Element newScope) {
		if (oldUV.u.isRelative()&&newUnit.isReal()) {
			//Need to translate old uv in old scope to real value before real to real conversion
			Rectangle oldScopeR;
			if (oldScope.getParent()==null) oldScopeR = oldScope.getRealRec();
			else oldScopeR = oldScope.getParent().getRealRec();
			
			if (oldUV.u==Unit.pcw) oldUV = new UnitValue((oldUV.v/100d)*oldScopeR.width, Unit.px);
			if (oldUV.u==Unit.pch) oldUV = new UnitValue((oldUV.v/100d)*oldScopeR.height, Unit.px);
			return GUI.getScreenUtils().translateRealUnitToRealUnit(oldUV, newUnit);
		}
		
		if (oldUV.u.isRelative()&&newUnit.isRelative()) {
			//Need to translate olduv relative value into this scope relative value
			Rectangle oldScopeR;
			Rectangle newScopeR;
			if (oldScope.getParent()==null) oldScopeR = oldScope.getRealRec();
			else oldScopeR = oldScope.getParent().getRealRec();
			if (newScope.getParent()==null) newScopeR = newScope.getRealRec();
			else newScopeR = oldScope.getParent().getRealRec();
			
			if (oldUV.u==Unit.pcw) {
				if (newUnit==Unit.pcw) return new UnitValue((((oldUV.v/100d)*oldScopeR.width)/newScopeR.width)*100, newUnit);
				if (newUnit==Unit.pch) return new UnitValue((((oldUV.v/100d)*oldScopeR.width)/newScopeR.height)*100, newUnit);
			}
			if (oldUV.u==Unit.pch) {
				if (newUnit==Unit.pcw) return new UnitValue((((oldUV.v/100d)*oldScopeR.height)/newScopeR.width)*100, newUnit);
				if (newUnit==Unit.pch) return new UnitValue((((oldUV.v/100d)*oldScopeR.height)/newScopeR.height)*100, newUnit);
			}
		}
		
		if (oldUV.u.isReal()&&newUnit.isRelative()) {
			//Need to translate real unit to be a relative value for this element
			Rectangle newScopeR;
			if (!newScope.hasParent()) newScopeR = newScope.getRealRec();
			else {
				if (!oldScope.hasParent()) return null;
				newScopeR = oldScope.getParent().getRealRec();
			}
			
			if (newUnit==Unit.pcw) {
				if (oldUV.u==Unit.px) return new UnitValue((oldUV.v/newScopeR.width)*100, newUnit);
				if (oldUV.u==Unit.vw) return new UnitValue((GUI.getScreenUtils().cW(oldUV.v)/newScopeR.width)*100, newUnit);
				if (oldUV.u==Unit.vh) return new UnitValue((GUI.getScreenUtils().cH(oldUV.v)/newScopeR.width)*100, newUnit);
				
			}
			if (newUnit==Unit.pch) {
				if (oldUV.u==Unit.px) return new UnitValue((oldUV.v/newScopeR.height)*100, newUnit);
				if (oldUV.u==Unit.vw) return new UnitValue((GUI.getScreenUtils().cW(oldUV.v)/newScopeR.height)*100, newUnit);
				if (oldUV.u==Unit.vh) return new UnitValue((GUI.getScreenUtils().cH(oldUV.v)/newScopeR.height)*100, newUnit);
				
			}
		}
		
		//Can just convert real unit to another real unit using the screen utils otherwise
		if (oldUV.u.isReal()&&newUnit.isReal()) {
			return GUI.getScreenUtils().translateRealUnitToRealUnit(oldUV, newUnit);
		}
		return null;
	}
	
	public UnitPoint translateToUnit(UnitPoint oldP, Element oldScope, Unit newUnit, Element newScope) {
		UnitPoint pNew = new UnitPoint();
		pNew.x = translateToUnit(oldP.x, oldScope, newUnit, newScope);
		pNew.y = translateToUnit(oldP.y, oldScope, newUnit, newScope);
		return pNew;
	}
	
	public UnitRectangle translateToVP(UnitRectangle oldR, Element oldScope, Element newScope) {
		UnitRectangle rNew = new UnitRectangle();
		rNew.x = translateToUnit(oldR.x, oldScope, Unit.vw, newScope);
		rNew.y = translateToUnit(oldR.y, oldScope, Unit.vh, newScope);
		rNew.width = translateToUnit(oldR.width, oldScope, Unit.vw, newScope);
		rNew.height = translateToUnit(oldR.height, oldScope, Unit.vh, newScope);
		return rNew;
	}
	
	public UnitRectangle translateToPX(UnitRectangle oldR, Element oldScope, Element newScope) {
		UnitRectangle rNew = new UnitRectangle();
		rNew.x = translateToUnit(oldR.x, oldScope, Unit.px, newScope);
		rNew.y = translateToUnit(oldR.y, oldScope, Unit.px, newScope);
		rNew.width = translateToUnit(oldR.width, oldScope, Unit.px, newScope);
		rNew.height = translateToUnit(oldR.height, oldScope, Unit.px, newScope);
		return rNew;
	}
	
	/**
	* Translates a width/height pair created in this elements perspective into a
	* width/height pair of equivilent visual size in the given elements perspective
	* @param e
	* @param r
	* @return
	*/
	public Point translateToElement(Point p, Element e) {
		if (position==Position.Absolute||parent==null) return p;
		Rectangle pR = getRealRec(new UnitRectangle(0, 0, p.x, p.y, Unit.pcw, Unit.pch));
		Rectangle eR = e.getRealRec();
		Point pNew = new Point();
		
		pNew.x = (pR.width/eR.width)*100;
		pNew.y = (pR.height/eR.height)*100;
		return pNew;
	}
	
	/**
	* Checks if a real pixel value point p is over this element.
	* @param p
	* @return
	*/
	public boolean isOver(Point p) {
		Rectangle rS = getRealRec();
		if (p.x>=rS.x && p.x<=rS.x+rS.width &&
		p.y>=rS.y && p.y<=rS.y+rS.height) {
			return true;
		}
		
		/*
		* At this point look at all components down the tree from this element in case
		* a component is setup outside the bounds of this box, but isOver should still
		* trigger so events can reach that component.
		*/
		for (Component c : getComponents()) {
			if (c.isOver(p)) return true;
		}
		return false;
	}
	
	/**
	* Scales a point real point on the screen and turns it into a percentage position in this element.
	* @param p
	* @return
	*/
	public Point scalePoint(Point p) {
		Rectangle r = getRealRec();
		Point pNew = new Point();
		pNew.x = (p.x-r.x)/r.width;
		pNew.y = (p.y-r.y)/r.height;
		return pNew;
	}
	
	public void drawComponents(Graphics2D g) {
		componentsLock.lock();
		
		for (Component c : getSortedComponents()) {
			if (c.isVisible()) c.draw(g);
		}
		componentsLock.unlock();
	}
	
	public void drawComponentShadows(Graphics2D g) {
		componentsLock.lock();
		
		for (Component c : getSortedComponents()) {
			if (c.isVisible()&&c.hasShadow()) GUI.getScreenUtils().drawShadow(g, c);
		}
		componentsLock.unlock();
	}
	
	public void draw(Graphics2D g) {
		//drawComponentShadows(g);
		drawComponents(g);
		if (scrollBar!=null) scrollBar.draw(g);
	}
	
	public void destroy() {
		componentsLock.lock();
		for (Component c : getComponents()) c.destroy();
		componentsLock.unlock();
	}
	
	/**
	* Need to search to leaf before implementing a click action. If a child is able to preform
	* a click action on these coordinates then that should be triggered rather than this. Only one
	* element should be able to do an action for every one click. If no children have succesfully performed
	* a click then this element should perform a click.
	* 
	* Therefore - method searches down child tree. If no child returns true then this element will return true
	* which indicates that it may perform a click action. If a child returns true then that child has performed
	* a click action and this method will return false as this method may perform a click action
	* 
	* 
	* Chain is
	* Element class will check if children will preform a click
	* This is done by calling onclick for all children components. Child call will first go to Component method
	* which will call Element method and verify that child has no clickable children. Then component method will return
	* true if a click action has been registered and run otherwise will return false.
	* 
	* The point of this being that a parent element will preform a click even if click happens over a child element,
	* given that the child element and all it's decendents have no click action registered. Otherwise a click
	* action will be preformed in that descendent.
	* This is opposed to a parent simply not preforming a click action whenever the mouse is over a child, even if that
	* child has no action to actually perform.
	* 
	* @param p
	* @return whether or not this element may perform a click action
	*/
	protected boolean doClick(Point p) {
		componentsLock.lock();
		
		boolean hasClicked = false;
		for (Component c : getSortedReversedComponents()) {
			if (c.isVisible()&&c.isOver(p)) {
				hasClicked = c.doClick(p); //Will recur down
				if (hasClicked) break;
			}
		}
		
		//Deselect all non clicked components
		/*for (Component c : getComponents()) {
			if (clicked==null||c!=clicked) {
				if (c.isVisible()&&c.isSelected()) c.doDeselect();
			}
		}*/
		
		componentsLock.unlock();
		return !hasClicked;
	}
	
	protected void doMove(Point p) {
		/*componentsLock.lock();
		//sortComponents();
		
		for (Component c : getComponents()) {
			if (!c.isVisible()) continue;
			if (c.isOver(p)) {
				c.doHover();
				c.doMove(p); //Will recur down
			}
			else c.doUnhover();
		}
		componentsLock.unlock();*/
	}
	
	protected void doDrag(Point entry, Point current) {
		componentsLock.lock();
		for (Component c : getSortedReversedComponents()) {
			if (c.isVisible()&&c.isOver(current)) {
				c.doDrag(entry, current); //Will recur down
				break;
			}
		}
		
		//Check drag on scrollbar
		if (scrollBar!=null&&scrollBar.isOver(current)) scrollBar.doDrag(entry, current);
	}
	
	protected void doScroll(Point p, int amount) {
		/*componentsLock.lock();
		//sortComponents();
		
		for (Component c : getComponents()) {
			if (c.isVisible()&&c.isOver(p)) {
				c.doScroll(p, amount); //Will recur down
				break;
			}
		}
		componentsLock.unlock();*/
	}
	
	protected void doKeyPress(KeyEvent k) {}; //Doesn't need to recur due to key listener registration in IO
	
	/**
	* Searches up the parent tree from this element and checks if any parent element 
	* is of the specified class.
	*/
	public Element getParentAssignableFrom(Class<?> clazz) {
		if (!hasParent()) return null;
		Element e = getParent();
		
		while (e.hasParent()) {
			if (clazz.isAssignableFrom(e.getClass())) return e;
			e = e.getParent();
		}
		return null;
	}
	
	public String simpleName() {return this.getClass().getSimpleName();}
	
	@Override
	public String toString() {
		String result = "["+CLI.orange+getClass().getSimpleName()+CLI.reset+": "+CLI.blue+"pos"+CLI.reset+": "+getPosition()+" "+CLI.blue+"float"+CLI.reset+": "+getFloat()+" "+CLI.blue+"dom"+CLI.reset+": "+inDOM()+" ";
		if (hasTag()) result +=  CLI.blue+"tag"+CLI.reset+": "+tag.toString();
		if (hasTagString()) result +=  CLI.blue+"tagString"+CLI.reset+": "+tagString;
		result += CLI.blue+"r"+CLI.reset+": "+getRec()+" "+CLI.blue+"rFunc"+CLI.reset+": "+rFunc+" "+CLI.blue+"rL"+CLI.reset+": "+getRealRec()+"]";
		return result;
	}
}
