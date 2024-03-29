package light.guipackage.gui.components.primitives;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.GUI;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.primitives.boxes.SimpleBox;

public class Dialog extends Component {

	private Runnable onClose;
	SimpleBox mainBox;
	SimpleBox close;
	SimpleBox accept;
	Set<Component> addedComponents; //Components that are not part of the core box
	List<SimpleBox> tabs; //Tabs added to this popup;
	
	public Dialog(String label) {
		super(new UnitRectangle(50, 50, 0, 0));
		addedComponents = new HashSet<Component>();
		tabs = new ArrayList<SimpleBox>();
		//setAbsolute(true);

		//Main box
		mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
		mainBox.setColor(Styles.fg);
		mainBox.setRounded(true);
		mainBox.increasePriority();
		addComponent(mainBox);

		//Top bar
		SimpleBox tB = new SimpleBox(new UnitRectangle(getX(), getY(), getWidth(), new UnitValue(12.5, Unit.pch)));
		tB.setColor(Styles.focus);
		//tB.setAbsolute(true);
		tB.setRounded(new int[]{1, 4});
		mainBox.addComponent(tB);

		//Top label
		Label l = new Label(new UnitRectangle(50, 50, 0 , 0), label, new Font(Styles.baseFont, Font.BOLD, 16), new Color(230, 230, 230));
		l.setTextCentered(true);
		tB.addComponent(l);

		//Exit button
		//close = new Button(new Rectangle(getX()+getWidth()*0.70, getY()+getHeight()*0.75, 5, 10), Styles.focus);
		//close.setAbsolute(true);
		close.setClickAction(() -> close(false));
		close.addComponent(new Image(new UnitRectangle(10, 10, 80, 80), "exit.png"));
		mainBox.addComponent(close);

		//Accept button
		//accept = new Button(new Rectangle(getX()+getWidth()*0.85, getY()+getHeight()*0.75, 5, 10), new Color(100, 200, 100));
		//accept.setAbsolute(true);
		accept.setClickAction(() -> close(true));
		accept.addComponent(new Image(new UnitRectangle(25, 25, 50, 50), "ok.png"));
		mainBox.addComponent(accept);
	}

	public void addSmother(double opacity) {
		//Smother
		SimpleBox smother = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
		smother.setColor(new Color(0, 0, 0));
		smother.setOpacity(opacity);
		//smother.setAbsolute(true);
		addComponent(smother);
	}
	
	public void setCloseAction(Runnable r) {this.onClose = r;}
	
	public void addTab(String name, Runnable clickAction) {
		//Tabs
		Font f = new Font(Styles.baseFont, Font.BOLD, 14);
		double w = GUI.getScreenUtils().getStringWidthAsPerc(f, name)+10;
		double h = GUI.getScreenUtils().getStringHeightAsPerc(f, name)+5;
		double x = 0;
		if (!tabs.isEmpty()) x = tabs.get(tabs.size()-1).getX().v+tabs.get(tabs.size()-1).getWidth().v;
		
		SimpleBox tab = new SimpleBox(new UnitRectangle(x, 16-h, w, h));
		tab.setColor(Styles.fg);
		tab.setClickAction(() -> {
			for (SimpleBox t : tabs) t.setColor(Styles.focus2);
			tab.setColor(Styles.fg);
			clickAction.run();
		});
		tab.setRounded(new int[] {1, 4});
		Label l = new Label(new UnitRectangle(50, 50, 0, 0), name, f, new Color(220, 220, 220));
		l.setTextCentered(true);
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
	}

	private void close(boolean cancelled) {
		if (onClose!=null) onClose.run();
		removeFromParent();
		destroy();
	}
}
