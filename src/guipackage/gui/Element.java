package guipackage.gui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import guipackage.cli.CLI;
import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.general.UnitPoint;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.components.Component;

public abstract class Element {
	
	private Element parent;
	private UnitRectangle r; //Current values set by and visible to creator
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
		Fixed //Dimensions taken from top left of screen
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

	private boolean centered; //Sets element to be centered in parent element. This requires position absolute
	
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
	}
	
	public void setX(UnitValue p) {
		r.x = p;
		doPositioning();
		if (parent!=null) {
			parent.childUpdated();
			parent.updateSiblings();
		}
	}
	public void setY(UnitValue p) {
		r.y = p;
		doPositioning();
		if (parent!=null) {
			parent.childUpdated();
			parent.updateSiblings();
		}
	}
	public void setWidth(UnitValue p) {
		r.width = p;
		doPositioning();
		if (parent!=null) {
			parent.childUpdated();
			parent.updateSiblings();
		}
	}
	public void setHeight(UnitValue p) {
		r.height = p;
		doPositioning();
		if (parent!=null) {
			parent.childUpdated();
			parent.updateSiblings();
		}
	}
	public void setMinWidth(UnitValue p) {
		minSize.x = p;
		doPositioning();
		if (parent!=null) {
			parent.childUpdated();
			parent.updateSiblings();
		}
	}
	public void setMinHeight(UnitValue p) {
		minSize.y = p;
		doPositioning();
		if (parent!=null) {
			parent.childUpdated();
			parent.updateSiblings();
		}
	}
	public void setMaxWidth(UnitValue p) {
		maxSize.x = p;
		doPositioning();
		if (parent!=null) {
			parent.childUpdated();
			parent.updateSiblings();
		}
	}
	public void setMaxHeight(UnitValue p) {
		maxSize.y = p;
		doPositioning();
		if (parent!=null) {
			parent.childUpdated();
			parent.updateSiblings();
		}
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
		if (parent!=null) {
			parent.childUpdated();
			parent.updateSiblings();
		}
	}
	public UnitRectangle getRec() {return r;}
	public UnitRectangle getFunctionalRec() {return rFunc;}
	
	public void setMinSize(UnitPoint p) {this.minSize = p;}
	public void setMaxSize(UnitPoint p) {this.maxSize = p;}
	
	public Position getPosition() {return position;}
	public void setPosition(Position p) {position = p;}
	
	public void setFloat(Float f) {this.floatType = f;}
	public Float getFloat() {return floatType;}
	
	public void setFill(Fill f) {this.fill = f;}
	public Fill getFill() {return fill;}

	public void setCentered(boolean c) {
		this.centered = c;
		if (centered) setPosition(Position.Absolute);
	}
	public boolean isCentered() {return centered;}
	
	public Element getParent() {return parent;}
	public void setParent(Element e) {parent = e;}
	
	public void setDOMEntryAction(Runnable r) {domEntryAction = r;}
	public boolean inDOM() {return inDOM;}
	
	public void setRoot() {
		isRoot = true;
		setPosition(Position.Fixed);
	}
	public boolean isRoot() {return isRoot;}
	
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
	
	public void addComponent(Component c) {
		c.setParent(this);
		componentsLock.lock();
		components.add(c);
		componentsLock.unlock();
		
		c.doPositioning();
		updateSiblings();
		if (parent!=null) parent.childUpdated();
		
		//DOM entry
		if (isRoot()) c.triggerDOMEntry(); //Trigger DOM entry action if this is the root node adding a component
		if (inDOM) c.triggerDOMEntry(); //Also trigger if this is another element already already in dom
	}
	
	public void removeComponent(Component c) {
		if (c==null) return;
		componentsLock.lock();
		components.remove(c);
		componentsLock.unlock();
		
		c.triggerDOMExit();
	}
	
	public void removeComponents(Collection<Component> toRemove) {
		if (toRemove==null||toRemove.isEmpty()) return;
		componentsLock.lock();
		components.removeAll(toRemove);
		componentsLock.unlock();
		
		for (Component c : toRemove) c.triggerDOMExit();
	}
	
	public void cleanComponents() {
		componentsLock.lock();
		components.clear();
		componentsLock.unlock();
	}
	
	public void sortComponents() {
		Collections.sort(components, new Comparator<Component>() {
			public int compare(Component c1, Component c2) {
				if (c1.getPriority()>c2.getPriority()) return -1;
				if (c1.getPriority()<c2.getPriority()) return 1;
				return 0;
			}
		});
	}
	
	/**
	* Recursivly triggers DOM entry action for this and all children
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
	*/
	protected void childUpdated() {
		if (parent!=null) parent.childUpdated();
	}
	
	/**
	* Trigger sibling updated hook in all children
	*/
	private void updateSiblings() {
		for (Component c : getComponents()) c.siblingUpdated();
	}
	
	/**
	* Update hook for when a sibling of this element is updated. Allows relative positioning to be
	* adjusted and to stay true when changes happen to siblings
	*/
	protected void siblingUpdated() {
		doPositioning();
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
		if (isCentered()) {
			centerElementInParent();
			/*
			 * An element with centered set should never be relative and should not respect it's
			 * float or fill property as center overrides both of these so safe to return here
			 */
			return;
		}

		/*
		* Float should be done from right end of box if position is not relative OR
		* if position is relative but there are no eligable elements added before this element.
		* This is done so that relative chaining can still work. Position relative uses values
		* of last added eligable element as a reference, and so needs the first relative element to still
		* be normally floated right for this to work.
		*/
		
		//Do float
		if (getFloat()==Float.Right&&getPosition()!=Position.Relative) floatRight();
		
		//Do relative positioning
		if (getPosition()==Position.Relative) positionRelatively();
		
		//Do fill
		if (getFill()!=Fill.None) fillToNextElement();
		checkMinMaxSize();
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
				UnitValue width = translateToUnit(lastRelativeSibling.getFuncWidth(), lastRelativeSibling, getX().u, this);
				//CLI.debug(lastRelativeSibling.getFunctionalRec().width+", "+width+", "+", "+sibX.v+", "+getX().v);
				rFunc.x = new UnitValue(sibX.v+width.v+getX().v, getX().u);
			}
			if (getFloat()==Float.Right) { //X backwards from top left of sibling
				UnitValue width = translateToUnit(getWidth(), this, getX().u, this);
				rFunc.x = new UnitValue(sibX.v-width.v-getX().v, getX().u);
			}
			rFunc.y = new UnitValue(getY().v+sibY.v, getY().u);
		}
		//No eligable relative sibling, still need to handle float as described aboves
		else if (getFloat()==Float.Right) floatRight();
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

	public void centerElementInParent() {
		UnitValue parentW = translateToUnit(parent.getWidth(), parent, getX().u, this);
		UnitValue parentH = translateToUnit(parent.getHeight(), parent, getY().u, this);
		UnitValue width = translateToUnit(getWidth(), this, getX().u, this);
		UnitValue height = translateToUnit(getHeight(), this, getY().u, this);
		CLI.debug("\n"+parent+"\n"+this);
		CLI.debug(parent.getWidth()+", "+getX());
		CLI.debug(parent.getWidth().u.isReal()+", "+getX().u.isReal());
		CLI.debug(parentW+", "+parentH+", "+width+", "+height);
		
		rFunc.x = new UnitValue((parentW.v-width.v)/2, getX().u);
		rFunc.y = new UnitValue((parentH.v-height.v)/2, getY().u);
	}
	
	/**
	* Translates this elements rec to actual size in pixels on the screen.
	* E.g if this element has a height of 100 but it is nested inside
	* one or more other components, then will get actual size of this
	* component.
	* 
	* @return
	*/
	public Rectangle getRealRec() {return getRealRec(rFunc);}
	
	/**
	* Allows you to translate any rectangle into real pixel values as if it were being processed
	* like this element.
	* 
	* @param r
	* @return
	*/
	public Rectangle getRealRec(UnitRectangle r) {
		Rectangle rNew = new Rectangle();
		
		//Fixed positioning
		if (position==Position.Fixed||isRoot()) { //Root element should be treated as position fixed
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
			if (parent==null) return r.toRect();
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
			Rectangle oldScopeR = oldScope.getParent().getRealRec();
			if (oldUV.u==Unit.pcw) oldUV = new UnitValue((oldUV.v/100d)*oldScopeR.width, Unit.px);
			if (oldUV.u==Unit.pch) oldUV = new UnitValue((oldUV.v/100d)*oldScopeR.height, Unit.px);
			return GUI.getScreenUtils().translateRealUnitToRealUnit(oldUV, newUnit);
		}

		if (oldUV.u.isRelative()&&newUnit.isRelative()) {
			//Need to translate olduv relative value into this scope relative value
			Rectangle oldScopeR = oldScope.getParent().getRealRec();
			Rectangle newScopeR = newScope.getParent().getRealRec();
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
			Rectangle newScopeR = newScope.getParent().getRealRec();
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
		if (p.x>=rS.x && p.x<=rS.x+rS.width
		&& p.y>=rS.y && p.y<=rS.y+rS.height) {
			return true;
		}
		
		/*
		* At this point look at all components down the tree from this element in case
		* a component is setup outside the bounds of this box, but isOver should still
		* trigger so events can reach that component.
		*/
		for (Component c : components) {
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
		//Scale real point to percentage point
		Point pNew = p.clone();
		pNew.x = GUI.getScreenUtils().cWR(pNew.x);
		pNew.y = GUI.getScreenUtils().cHR(pNew.y);
		
		Rectangle r = getRealRec();
		r = new Rectangle(GUI.getScreenUtils().cWR(r.x), GUI.getScreenUtils().cHR(r.y), GUI.getScreenUtils().cWR(r.width), GUI.getScreenUtils().cHR(r.height));
		//Scale to percentage of this element
		pNew.x = (pNew.x-r.x)/r.width;
		pNew.y = (pNew.y-r.y)/r.height;
		return pNew;
	}
	
	public void drawComponents(Graphics2D g) {
		componentsLock.lock();
		sortComponents();
		Collections.reverse(components);
		for (Component c : components) {
			if (c.isVisible()) c.draw(g);
		}
		componentsLock.unlock();
	}
	
	public void drawComponentShadows(Graphics2D g) {
		componentsLock.lock();
		sortComponents();
		Collections.reverse(components);
		for (Component c : components) {
			if (c.isVisible()&&c.hasShadow()) GUI.getScreenUtils().drawShadow(g, c);
		}
		componentsLock.unlock();
	}
	
	public void draw(Graphics2D g) {
		drawComponentShadows(g);
		drawComponents(g);
	}
	
	public void destroy() {
		componentsLock.lock();
		for (Component c : getComponents()) c.destroy();
		componentsLock.unlock();
	}
	
	protected void doClick(Point p) {
		componentsLock.lock();
		/*
		* Components with higher priority may have overrided their isOver method,
		* allowing them to take up more space for example when a selector is open.
		* In this case we don't want an element potentially under an expanded element
		* to register a click. Thats why we sort components first and only allow one 
		* element to register a click at any one time.
		*/
		
		sortComponents();
		Component clicked = null;
		for (Component c : getComponents()) {
			if (c.isVisible()&&c.isOver(p)) {
				c.doClick(p); //Will recur down
				clicked = c;
				break;
			}
		}
		
		//Deselect all non clicked components
		for (Component c : getComponents()) {
			if (clicked==null||c!=clicked) {
				if (c.isVisible()&&c.isSelected()) c.doDeselect();
			}
		}
		componentsLock.unlock();
	}
	
	protected void doMove(Point p) {
		componentsLock.lock();
		sortComponents();
		
		for (Component c : getComponents()) {
			if (!c.isVisible()) continue;
			if (c.isOver(p)) {
				c.doHover();
				c.doMove(p); //Will recur down
			}
			else c.doUnhover();
		}
		componentsLock.unlock();
	}
	
	protected void doDrag(Point entry, Point current) {
		componentsLock.lock();
		sortComponents();
		
		for (Component c : getComponents()) {
			if (!c.isVisible()) continue;
			if (c.isOver(current)) {
				c.doDrag(entry, current); //Will recur down
				break;
			}
		}
		componentsLock.unlock();
	}
	
	protected void doScroll(Point p, int amount) {
		componentsLock.lock();
		sortComponents();
		
		for (Component c : getComponents()) {
			if (c.isVisible()&&c.isOver(p)) {
				c.doScroll(p, amount); //Will recur down
				break;
			}
		}
		componentsLock.unlock();
	}
	
	protected void doKeyPress(KeyEvent k) {}; //Doesn't need to recur due to key listener registration in IO
	
	@Override
	public String toString() {
		//if (inDOM()) 
		return "["+CLI.orange+getClass().getSimpleName()+CLI.reset+": "+CLI.blue+"pos"+CLI.reset+": "+getPosition()+" "+CLI.blue+"float"+CLI.reset+": "+getFloat()+" "+CLI.blue+"dom"+CLI.reset+": "+inDOM()+" "+CLI.blue+"r"+CLI.reset+": "+getRec()+" "+CLI.blue+"rFunc"+CLI.reset+": "+rFunc+" "+CLI.blue+"rL"+CLI.reset+": "+getRealRec()+"]";
		//return "["+CLI.orange+getClass().getSimpleName()+CLI.reset+": "+CLI.blue+"pos"+CLI.reset+": "+getPosition()+" "+CLI.blue+"float"+CLI.reset+": "+getFloat()+" "+CLI.blue+"dom"+CLI.reset+": "+inDOM()+" "+CLI.blue+"r"+CLI.reset+": "+getRec()+" "+CLI.blue+"rFunc"+CLI.reset+": "+rFunc+"]";
	}
}
