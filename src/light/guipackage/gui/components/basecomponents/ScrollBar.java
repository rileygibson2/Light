package light.guipackage.gui.components.basecomponents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import light.guipackage.cli.CLI;
import light.guipackage.general.Point;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.gui.components.Component;

public class ScrollBar extends Component {

	private double scroll;
	private Point bounds;

	public ScrollBar() {
		super(new UnitRectangle(95, 0, 5, 100));
	}
}
