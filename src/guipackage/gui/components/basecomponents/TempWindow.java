package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Font;

import guipackage.general.UnitPoint;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.boxes.CollumnBox;
import guipackage.gui.components.boxes.FlexBox;
import guipackage.gui.components.boxes.SimpleBox;

public class TempWindow extends CollumnBox {
	
	private Runnable onClose;
	SimpleBox topBar;
	FlexBox tabBar;
	FlexBox contentBox;
	
	int activeTab;
	
	public TempWindow(String label) {
		super(new UnitPoint(0, Unit.vw, 0, Unit.vh));
		activeTab = -1;
		setCentered(true);
		setMinWidth(new UnitValue(40, Unit.vw));
		
		//Top bar
		topBar = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 5, Unit.vh));
		addComponent(topBar);
		
		Label title = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 20, Unit.vw, 100, Unit.pch), label, new Font(GUI.baseFont, Font.BOLD, 18), new Color(230, 230, 230));
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
		exit.setColor(GUI.bg);
		exit.setBorder(1, new Color(80, 80, 80));
		exit.setRounded(10);
		topBar.addComponent(exit);

		//Tab bar
		tabBar = new FlexBox(new UnitPoint());
		tabBar.setMinWidth(new UnitValue(100, Unit.pcw));
		tabBar.setColor(GUI.fg);
		SimpleBox tBB = new SimpleBox(new UnitRectangle(0, 96.7, 100, 5, Unit.pcw, Unit.pch), new Color(245, 0, 66));
		tabBar.addComponent(tBB);
		addComponent(tabBar);
		
		//Content box
		contentBox = new FlexBox(new UnitPoint());
		contentBox.setMinWidth(new UnitValue(100, Unit.pcw));
		contentBox.setColor(GUI.fg);
		addComponent(contentBox);
		
		Table table = new Table(new UnitPoint(0, Unit.px, 5, Unit.vh));
		table.addCollumn(Label.class, "Attrib", new UnitValue(50, Unit.px));
		table.addCollumn(Label.class, "Inter", new UnitValue(150, Unit.px));
		table.addCollumn(Label.class, "Mode", new UnitValue(50, Unit.px));
		table.addRow();
		contentBox.addComponent(table);
	}

	public void addTab(String name) {
		SimpleBox tab = new SimpleBox(new UnitRectangle(0, 0, 80, 30, Unit.px), GUI.focus);
		tab.setPosition(Position.Relative);
		tab.setRounded(new int[]{1, 4});
		tab.setBorder(new Color(245, 185, 66));
		Label tabName = new Label(new UnitRectangle(0, 0, 100, 100, Unit.pcw, Unit.pch), name, new Font(GUI.baseFont, Font.BOLD, 18), new Color(230, 230, 230));
		tabName.setTextCentered(true);
		tab.addComponent(tabName);
		tabBar.addComponent(tab);
		if (activeTab==-1) activeTab = 1;
	}
	
	public void addSmother(double opacity) {
		//Smother
		SimpleBox smother = new SimpleBox(new UnitRectangle(0, 0, 100, 100), new Color(0, 0, 0));
		smother.setOpacity(opacity);
		smother.setPosition(Position.Absolute);
		addComponent(smother);
	}

	public void addContent(Component c) {

	}
	
	public void setCloseAction(Runnable r) {this.onClose = r;}
	
	public void close(boolean cancelled) {
		if (onClose!=null) onClose.run();
		removeFromParent();
		destroy();
	}
}
