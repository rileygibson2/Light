package light.guipackage.gui.components.primitives;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.primitives.boxes.CollumnBox;
import light.guipackage.gui.components.primitives.boxes.FlexBox;
import light.guipackage.gui.components.primitives.boxes.SimpleBox;

public class TempWindow extends CollumnBox {
	
	private Runnable onClose;
	SimpleBox topBar;
	FlexBox tabBar;
	FlexBox contentBox;
	
	List<FlexBox> tabs;
	int activeTab;

	/**
	 * List of objects that can be filled and recalled to avoid a whole class being made when 
	 * simply a TempWindow and one or two other elements is succifient.
	 */
	List<Object> helpfulObjects; 
	
	public TempWindow(String label) {
		super(new UnitPoint(0, Unit.vw, 0, Unit.vh));
		activeTab = -1;
		tabs = new ArrayList<FlexBox>();
		helpfulObjects = new ArrayList<Object>();
		setCentered(Center.xyCentered);
		setMinWidth(new UnitValue(40, Unit.vw));
		setPosition(Position.GlobalFixed);
		
		//Top bar
		topBar = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 5, Unit.vh));
		addComponent(topBar);
		
		Label title = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 20, Unit.vw, 100, Unit.pch), label, new Font(Styles.baseFont, Font.BOLD, 18), new Color(230, 230, 230));
		title.setFill(Fill.Horizontal);
		title.setColor(new Color(0, 0, 180));
		title.setBorder(1, new Color(10, 100, 255));
		title.setRounded(10);
		title.setTextXCentered(true);
		title.setTextYCentered(true);
		topBar.addComponent(title);
		
		Image exit = new Image(new UnitRectangle(0, Unit.vw, 0, Unit.vh, 5, Unit.vw, 100, Unit.pch), "exit.png");
		exit.setPosition(Position.Relative);
		exit.setFloat(Float.Right);
		exit.setColor(Styles.bg);
		exit.setBorder(1, new Color(80, 80, 80));
		exit.setRounded(10);
		exit.setClickAction(() -> close(false));
		topBar.addComponent(exit);
		
		//Tab bar
		tabBar = new FlexBox(new UnitPoint());
		tabBar.setMinWidth(new UnitValue(100, Unit.pcw));
		tabBar.setColor(Styles.focus);
		SimpleBox tBB = new SimpleBox(new UnitRectangle(0, 96.7, 100, 5, Unit.pcw, Unit.pch));
		tBB.setColor(Styles.focusOrange);
		//tabBar.addComponent(tBB);
		addComponent(tabBar);
		
		//Content box
		contentBox = new FlexBox(new UnitPoint());
		contentBox.setMinWidth(new UnitValue(100, Unit.pcw));
		contentBox.setColor(new Color(40, 40, 40));
		addComponent(contentBox);
	}
	
	public void addTab(String name) {
		Label tab = new Label(new UnitRectangle(0, 0, 80, 30, Unit.px), name, new Font(Styles.baseFont, Font.BOLD, 11), new Color(230, 230, 230));
		tab.setPosition(Position.Relative);
		tab.setColor(Styles.fg);
		tab.setRounded(new int[]{1, 4});
		tab.setBorder(Styles.focusOrange);
		tab.setTextCentered(true);
		tabBar.addComponent(tab);

		int tabi = tabs.size();
		tab.setClickAction(() -> switchTab(tabi));
		
		if (activeTab==-1) { //Add current content box as tab 1 box
			tabs.add(contentBox);
			switchTab(0);
		}
		else { //Add new content box for new tab
			FlexBox tabBox = new FlexBox(new UnitPoint());
			tabBox.setMinWidth(new UnitValue(100, Unit.pcw));
			tabBox.setColor(new Color(40, 40, 40));
			tabs.add(tabBox);
		}
	}
	
	public void switchTab(int i) {
		if (i<0||i>tabs.size()) return;
		
		//Switch content box
		removeComponent(contentBox);
		contentBox = tabs.get(i);
		addComponent(contentBox);
		
		//Style tab bar
		if (activeTab!=-1) {
			((SimpleBox) tabBar.getComponents().get(activeTab)).setBorder(Styles.focusOrange);
		}
		((SimpleBox) tabBar.getComponents().get(i)).setBorder(new int[]{1, 3, 4}, Styles.focusOrange);
		activeTab = i;
	}

	public int getActiveTabNum() {return activeTab;}

	public void addContent(Component c) {
		contentBox.addComponent(c);
	}

	public void addContent(Component c, int tab) {
		if (tab>tabs.size()) return;
		FlexBox tabBox = tabs.get(tab);
		if (tabBox!=null) tabBox.addComponent(c);
	}

	/**
	 * Returns current active contentBox
	 * @return
	 */
	public FlexBox getContentBox() {return contentBox;}

	/**
	 * Returns the box of the tab with the provided index
	 * @param i
	 * @return
	 */
	public FlexBox getTabContentBox(int i) {
		if (i<0||i>tabs.size()) return null;
		return tabs.get(i);
	}

	public void addSmother(double opacity) {
		//Smother
		SimpleBox smother = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
		smother.setColor(new Color(0, 0, 0));
		smother.setOpacity(opacity);
		smother.setPosition(Position.GlobalFixed);
		smother.decreasePriority();
		smother.setClickAction(() -> {}); //Registering a dummy click action stops elements underneath being clicked
		addComponent(smother);
	}
	
	public void setCloseAction(Runnable r) {this.onClose = r;}

	public void addHelpfulObject(Object o) {helpfulObjects.add(o);}
	
	public Object getHelpfulObject(int i) {
		if (i>=0&&i<helpfulObjects.size()) return helpfulObjects.get(i);
		return null;
	}
	
	public void close(boolean cancelled) {
		if (onClose!=null) onClose.run();
		removeFromParent();
		destroy();
	}
}
