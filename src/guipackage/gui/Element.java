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

import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.general.UnitPoint;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.components.Component;

public abstract class Element {
	
	private Element parent;
	protected UnitRectangle r; //Current values
	private UnitRectangle rO; //Original values
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

	/*public enum Float {
		Left, //Position is taken from top left of parent (if absolute) and from top right of sibling (if relative)
		Right //Position is taken from top right of parent (if absolute) and from top left of sibling (if relative)
	}*/
	
	public Element(UnitRectangle r) {
		this.r = r;
		this.rO = r.clone();
		this.minSize = new UnitPoint(0, Unit.px, 0, Unit.px);
		this.maxSize = new UnitPoint(GUI.screen.width, Unit.px, GUI.screen.height, Unit.px);
		this.components = new ArrayList<Component>();
		componentsLock = new ReentrantLock();
		position = Position.Absolute;
	}
	
	public void setX(UnitValue p) {
		r.x = p;
		checkSize();
		if (parent!=null) parent.childUpdated();
	}
	public void setY(UnitValue p) {
		r.y = p;
		checkSize();
		if (parent!=null) parent.childUpdated();
	}
	public void setWidth(UnitValue p) {
		r.width = p;
		checkSize();
		if (parent!=null) parent.childUpdated();
	}
	public void setHeight(UnitValue p) {
		r.height = p;
		checkSize();
		if (parent!=null) parent.childUpdated();
	}
	public void setMinWidth(UnitValue p) {
		minSize.x = p;
		if (parent!=null) parent.childUpdated();
	}
	public void setMinHeight(UnitValue p) {
		minSize.y = p;
		if (parent!=null) parent.childUpdated();
	}
	public void setMaxWidth(UnitValue p) {
		maxSize.x = p;
		if (parent!=null) parent.childUpdated();
	}
	public void setMaxHeight(UnitValue p) {
		maxSize.y = p;
		if (parent!=null) parent.childUpdated();
	}
	
	public UnitValue getX() {return r.x;}
	public UnitValue getY() {return r.y;}
	public UnitValue getWidth() {return r.width;}
	public UnitValue getHeight() {return r.height;}
	public UnitValue getMinWidth() {return minSize.x;}
	public UnitValue getMinHeight() {return minSize.y;}
	public UnitValue getMaxWidth() {return maxSize.x;}
	public UnitValue getMaxHeight() {return maxSize.y;}
	
	public void setRec(UnitRectangle r) {this.r = r;}
	public void setRecToOriginal() {r = rO.clone();}
	public UnitRectangle getRec() {return r;}
	public UnitRectangle getOriginalRec() {return rO;}
	public void changeOriginalRec(UnitRectangle r) {rO = r;}
	public void updateOriginalRec() {rO = r.clone();}

	public void setMinSize(UnitPoint p) {this.minSize = p;}
	public void setMaxSize(UnitPoint p) {this.maxSize = p;}
	
	public Element getParent() {return parent;}
	public void setParent(Element e) {parent = e;}
	
	public Position getPosition() {return position;}
	public void setPosition(Position p) {position = p;}
	
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
		
		if (c.getPosition()==Position.Relative) positionRelatively(c);
		
		//DOM entry
		if (isRoot()) c.triggerDOMEntry(); //Trigger DOM entry action if this is the root node adding a component
		if (inDOM) c.triggerDOMEntry(); //Also trigger if this is another element already already in dom
	}
	
	/**
	* Relativly position this component from the top right of the last component
	*/
	public void positionRelatively(Component c) {
		componentsLock.lock();
		
		if (!components.isEmpty()) {
			//Get last added relative component
			Component lastRelativeSibling = null;
			for (int i=components.size()-1; i>-1; i--) {
				Component sibling = components.get(i);
				if (sibling!=c&&sibling.getPosition()==Position.Relative&&!sibling.getRec().hasUnit(Unit.pc)) {
					lastRelativeSibling = sibling;
					break;
				}
			}
			componentsLock.unlock();
			
			if (lastRelativeSibling!=null) {
				//Adjust this elements position values to base off siblings
				
				UnitValue pos = GUI.getScreenUtils().translateToUnit(lastRelativeSibling.getX(), c.getX().u);
				UnitValue size = GUI.getScreenUtils().translateToUnit(lastRelativeSibling.getWidth(), c.getX().u);
				c.setX(new UnitValue(c.getX().v+pos.v+size.v, c.getX().u));

				pos = GUI.getScreenUtils().translateToUnit(lastRelativeSibling.getY(), c.getY().u);
				c.setY(new UnitValue(c.getY().v+pos.v, c.getY().u));
			}
		}
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
	public void childUpdated() {
		if (parent!=null) parent.childUpdated();
	}

	/**
	 * Validates size is not smaller than min values
	 */
	public void checkSize() {
		//Check min width
		UnitValue size = GUI.getScreenUtils().translateToUnit(getWidth(), getMinWidth().u);
		if (size.v<getMinWidth().v) r.width = getMinWidth().clone();
		
		//Check min height
		size = GUI.getScreenUtils().translateToUnit(getHeight(), getMinHeight().u);
		if (size.v<getMinHeight().v) r.height = getMinHeight().clone();

		//Check max width
		size = GUI.getScreenUtils().translateToUnit(getWidth(), getMaxWidth().u);
		if (size.v>getMaxWidth().v) r.width = getMaxWidth().clone();
		
		//Check max height
		size = GUI.getScreenUtils().translateToUnit(getHeight(), getMaxHeight().u);
		if (size.v>getMaxHeight().v) r.height = getMaxHeight().clone();
	}
	
	/**
	* Translates this elements rec to actual size in pixels on the screen.
	* E.g if this element has a height of 100 but it is nested inside
	* one or more other components, then will get actual size of this
	* component.
	* 
	* @return
	*/
	public Rectangle getRealRec() {return getRealRec(r);}
	
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
				case pc:
				case vw: rNew.x = GUI.getScreenUtils().cW(r.x.v); break;
				case vh: rNew.x = GUI.getScreenUtils().cH(r.x.v); break;
				case px: rNew.x = r.x.v; break;
			}
			switch (r.y.u) {
				case pc:
				case vh: rNew.y = GUI.getScreenUtils().cH(r.y.v); break;
				case vw: rNew.y = GUI.getScreenUtils().cW(r.y.v); break;
				case px: rNew.y = r.y.v; break;
			}
			switch (r.width.u) {
				case pc:
				case vw: rNew.width = GUI.getScreenUtils().cW(r.width.v); break;
				case vh: rNew.width = GUI.getScreenUtils().cH(r.width.v); break;
				case px: rNew.width = r.width.v; break;
			}
			switch (r.height.u) {
				case pc:
				case vh: rNew.height = GUI.getScreenUtils().cH(r.height.v); break;
				case vw: rNew.height = GUI.getScreenUtils().cW(r.height.v); break;
				case px: rNew.height = r.height.v; break;
			}
		}
		else { //Absolute and relative positioning
			if (parent==null) return r.toRect();
			Rectangle pR = parent.getRealRec();
			switch (r.x.u) {
				case pc: rNew.x = pR.x+(r.x.v/100d)*pR.width; break;
				case vw: rNew.x = pR.x+GUI.getScreenUtils().cW(r.x.v); break;
				case vh: rNew.x = pR.x+GUI.getScreenUtils().cH(r.x.v); break;
				case px: rNew.x = pR.x+r.x.v; break;
			}
			switch (r.y.u) {
				case pc: rNew.y = pR.y+(r.y.v/100d)*pR.height; break;
				case vw: rNew.y = pR.y+GUI.getScreenUtils().cW(r.y.v); break;
				case vh: rNew.y = pR.y+GUI.getScreenUtils().cH(r.y.v); break;
				case px: rNew.y = pR.y+r.y.v; break;
			}
			switch (r.width.u) {
				case pc: rNew.width = (r.width.v/100d)*pR.width; break;
				case vw: rNew.width = GUI.getScreenUtils().cW(r.width.v); break;
				case vh: rNew.width = GUI.getScreenUtils().cH(r.width.v); break;
				case px: rNew.width = r.width.v; break;
			}
			switch (r.height.u) {
				case pc: rNew.height = (r.height.v/100d)*pR.height; break;
				case vw: rNew.height = GUI.getScreenUtils().cW(r.height.v); break;
				case vh: rNew.height = GUI.getScreenUtils().cH(r.height.v); break;
				case px: rNew.height = r.height.v; break;
			}
		}
		
		//CLI.debug("El: "+getClass().getSimpleName()+" r: "+r.toString()+" rNew: "+rNew.toString()+" pos: "+position.toString()+" par: "+parent.getClass().getSimpleName());
		//if (isRoot()) CLI.debug("ISROOT");
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
		Rectangle pR = getRealRec(new UnitRectangle(0, 0, p.x, p.y, Unit.pc));
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
	* Scales a point real point on the screen and turns it into a percentage
	* position in this element. This method respects nested components.
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
	
	public void doClick(Point p) {
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
	
	public void doMove(Point p) {
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
	
	public void doDrag(Point entry, Point current) {
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
	
	public void doScroll(Point p, int amount) {
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
	
	public void doKeyPress(KeyEvent k) {}; //Doesn't need to recur due to key listener registration in IO
	
	@Override
	public String toString() {
		if (inDOM()) return "["+getClass().getSimpleName()+": pos: "+getPosition()+" dom: "+inDOM()+" r: "+getRec()+" rL: "+getRealRec()+"]";
		return "["+getClass().getSimpleName()+": pos: "+getPosition()+" dom: "+inDOM()+" r: "+getRec()+"]";
	}
}
