package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;

public class TempWindow extends FlexBox {

	private Runnable onClose;
	Set<Component> addedComponents; //Components that are not part of the core box
	List<SimpleBox> tabs; //Tabs added to this popup;

	public SimpleBox b;
	
	public TempWindow(String label) {
		super(new UnitRectangle(10, Unit.vw, 10, Unit.vh, 0, Unit.vw, 0, Unit.vh));
		addedComponents = new HashSet<Component>();
		tabs = new ArrayList<SimpleBox>();

		//Main box
		/*mainBox = new SimpleBox(new Rectangle(0, 0, 100, 100), Color.RED);
		mainBox.setRounded(true);
		mainBox.increasePriority();
		addComponent(mainBox);*/

		SimpleBox b = new SimpleBox(new UnitRectangle(0, 0, 100, 100), Color.RED);
		addComponent(b);

		//Top bar
		/*SimpleBox tB = new SimpleBox(new Rectangle(0, 0, 0, 0), Color.RED);
		tB.setRounded(new int[]{1, 4});
		tB.setPosition(Position.Relative);
		addComponent(tB);*/

		FlexBox topBar = new FlexBox(new UnitRectangle(0, 0, 0, 0, Unit.px));
		topBar.setMinWidth(new UnitValue(40, Unit.vw));
		topBar.setMinHeight(new UnitValue(5, Unit.vh));
		addComponent(topBar);

		b = new SimpleBox(new UnitRectangle(0, 0, 100, 100), Color.PINK);
		b.setOval(true);
		topBar.addComponent(b);

		Label title = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 30, Unit.vw, 100, Unit.pch), label, new Font(GUI.baseFont, Font.BOLD, 18), new Color(230, 230, 230));
		title.setPosition(Position.Relative);
		//title.setFloat(Float.Right);
		title.setColor(new Color(0, 0, 180));
		title.setBorder(1, new Color(10, 100, 255));
		title.setRounded(10);
		title.setXCentered(true);
		title.setYCentered(true);
		topBar.addComponent(title);

		Image exit = new Image(new UnitRectangle(0, Unit.vw, 0, Unit.vh, 20, Unit.vw, 100, Unit.pch), "exit.png");
		exit.setPosition(Position.Relative);
		//exit.setFloat(Float.Right);
		exit.setColor(GUI.bg);
		exit.setBorder(1, new Color(80, 80, 80));
		exit.setRounded(10);
		topBar.addComponent(exit);

		/*b = new SimpleBox(new UnitRectangle(0, 0, 5, 5, Unit.vw, Unit.vh), Color.yellow);
		//b.setFloat(Float.Right);
		b.setPosition(Position.Relative);
		topBar.addComponent(b);*/
		/*SimpleBox content = new SimpleBox(new UnitRectangle(0, Unit.pc, 5, Unit.vh, 100, Unit.pc, 20, Unit.vh), GUI.fg);
		content.setRounded(10);
		addComponent(content);*/
	}

	public void addSmother(double opacity) {
		//Smother
		SimpleBox smother = new SimpleBox(new UnitRectangle(0, 0, 100, 100), new Color(0, 0, 0));
		smother.setOpacity(opacity);
		smother.setPosition(Position.Absolute);
		addComponent(smother);
	}
	
	public void setCloseAction(Runnable r) {this.onClose = r;}
	
	/*public void addTab(String name, Runnable clickAction) {
		//Tabs
		Font f = new Font(GUI.baseFont, Font.BOLD, 14);
		double w = GUI.getScreenUtils().getStringWidthAsPerc(f, name)+10;
		double h = GUI.getScreenUtils().getStringHeightAsPerc(f, name)+5;
		double x = 0;
		if (!tabs.isEmpty()) x = tabs.get(tabs.size()-1).getX().v+tabs.get(tabs.size()-1).getWidth().v;
		
		SimpleBox tab = new SimpleBox(new UnitRectangle(x, 16-h, w, h), GUI.fg);
		tab.setClickAction(() -> {
			for (SimpleBox t : tabs) t.setColor(GUI.focus2);
			tab.setColor(GUI.fg);
			clickAction.run();
		});
		tab.setRounded(new int[] {1, 4});
		Label l = new Label(new UnitRectangle(50, 50, 0, 0), name, f, new Color(220, 220, 220));
		l.setCentered(true);
		tab.addComponent(l);
		tab.increasePriority();
		mainBox.addComponent(tab);
		
		tabs.add(tab);
	}

	//So components get added to the main box not to the popup which is essentially a wrapper
	public void addPopUpComponent(Component c) {
		addedComponents.add(c);
		mainBox.addComponent(c);
	}
	
	public void cleanPopupComponents() {
		mainBox.removeComponents(addedComponents);
		addedComponents.clear();
	}*/

	private void close(boolean cancelled) {
		if (onClose!=null) onClose.run();
		removeFromParent();
		destroy();
	}
}
