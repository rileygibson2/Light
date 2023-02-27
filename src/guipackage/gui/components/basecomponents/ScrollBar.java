package guipackage.gui.components.basecomponents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import guipackage.cli.CLI;
import guipackage.general.Point;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.gui.components.Component;

public class ScrollBar extends Component {

	private double y;
	private List<Component> scrollable;
	private Point bounds;
	private boolean armed;

	public ScrollBar() {
		super(new UnitRectangle(95, 0, 5, 100));
		y = 0;
		scrollable = new ArrayList<Component>();
		armed = true;
	}

	public void scroll(double amount) {
		if (!isArmed()||amount==0||scrollable.isEmpty()) return;
		if (bounds==null) {
			CLI.error("Scrollbar needs defined bounds.");
			return;
		}
		y -= amount*5;
		sortScrollableComponents(); //Make sure in correct order based on y value

		//If moving up check last has not moved above bottom bound
		if (amount>0) {
			Component last = scrollable.get(scrollable.size()-1);
			if ((last.getY().v+last.getHeight().v)<=bounds.y) return;
		}
		//If moving down check first has not moved below top bound
		if (amount<0) {
			if (scrollable.get(0).getY().v>=bounds.x) return;
		}

		//Move all scrollable components
		for (Component c : scrollable) {
			c.setY(new UnitValue(c.getY().v-amount, getY().u));

			//Check still within bounds
			if (!withinBounds(c)) c.setVisible(false);
			else if (!c.isVisible()) c.setVisible(true);
		}
	}

	public void reset() {
		y = 0;
		if (isArmed()) {
			for (Component c : scrollable) {
				//c.setRecToOriginal();
				c.setVisible(true);
			}
		}
		scrollable.clear();
	}

	public double getScroll() {return y;}

	public void addToScroll(Component c) {
		scrollable.add(c);
		
		if (isArmed()) { //Check within bounds
			if (!withinBounds(c)) c.setVisible(false);
		}
	}

	public boolean isArmed() {return armed;}

	public void setArmed(boolean a) {
		armed = a;
		setVisible(a);
	}

	public void setBounds(Point b) {bounds = b;}
	
	private boolean withinBounds(Component c) {
		if (c.getY().v>bounds.x&&c.getY().v<bounds.y) return true;
		return false;
	}

	public void sortScrollableComponents() {
		Collections.sort(scrollable, new Comparator<Component>() {
			public int compare(Component c1, Component c2) {
				if (c1.getY().v>c2.getY().v) return 1;
				if (c1.getY().v<c2.getY().v) return -1;
				return 0;
			}
		});
	}

}
