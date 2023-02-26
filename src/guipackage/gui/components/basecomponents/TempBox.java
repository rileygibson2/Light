package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import guipackage.cli.CLI;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;

public class TempBox extends Component {

	private Runnable onClose;
	SimpleBox mainBox;
	Set<Component> addedComponents; //Components that are not part of the core box
	List<SimpleBox> tabs; //Tabs added to this popup;
	
	public TempBox(String label) {
		super(new UnitRectangle(10, Unit.vw, 10, Unit.vh, 50, Unit.vw, 50, Unit.vh));
		addedComponents = new HashSet<Component>();
		tabs = new ArrayList<SimpleBox>();

		//Main box
		/*mainBox = new SimpleBox(new Rectangle(0, 0, 100, 100), Color.RED);
		mainBox.setRounded(true);
		mainBox.increasePriority();
		addComponent(mainBox);*/

		//Top bar
		/*SimpleBox tB = new SimpleBox(new Rectangle(0, 0, 0, 0), Color.RED);
		tB.setRounded(new int[]{1, 4});
		tB.setPosition(Position.Relative);
		addComponent(tB);*/

		SimpleBox b = new SimpleBox(new UnitRectangle(0, 0, 100, 100), Color.yellow);
		addComponent(b);

		FlexBox f = new FlexBox(new UnitRectangle(0, 0, 0, 10, Unit.px));
		addComponent(f);
		b = new SimpleBox(new UnitRectangle(0, 0, 10, 10, Unit.vw, Unit.vh), Color.red);
		f.addComponent(b);
		b = new SimpleBox(new UnitRectangle(20, 0, 15, 10, Unit.vw, Unit.vh), Color.green);
		f.addComponent(b);
		b = new SimpleBox(new UnitRectangle(0, 0, 100, 100), Color.PINK);
		f.addComponent(b);
		CLI.debug("added");

		//Label
		/*Label l = new Label(new Rectangle(0, 0, 0 , 0), label, new Font(GUI.baseFont, Font.BOLD, 16), new Color(230, 230, 230));
		l.setCentered(true);
		tB.addComponent(l);

		Button b = new Button(new Rectangle(0, 0, 10, 10), GUI.focus, "exit.png");
		tB.addComponent(b);*/
	}

	@Override
	public void addComponent(Component c) {
		super.addComponent(c);

		//Re-position to center
		//setX(100-getWidth()/2);
		//setY(100-getHeight()/2);
	}

	public void addSmother(double opacity) {
		//Smother
		SimpleBox smother = new SimpleBox(new UnitRectangle(0, 0, 100, 100), new Color(0, 0, 0));
		smother.setOpacity(opacity);
		smother.setPosition(Position.Absolute);
		addComponent(smother);
	}
	
	public void setCloseAction(Runnable r) {this.onClose = r;}
	
	public void addTab(String name, Runnable clickAction) {
		//Tabs
		Font f = new Font(GUI.baseFont, Font.BOLD, 14);
		double w = GUI.getInstance().getScreenUtils().getStringWidthAsPerc(f, name)+10;
		double h = GUI.getInstance().getScreenUtils().getStringHeightAsPerc(f, name)+5;
		double x = 0;
		if (!tabs.isEmpty()) x = tabs.get(tabs.size()-1).getX()+tabs.get(tabs.size()-1).getWidth();
		
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
	}

	private void close(boolean cancelled) {
		if (onClose!=null) onClose.run();
		removeFromParent();
		destroy();
	}
}
