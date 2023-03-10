package guipackage.gui.components.basecomponents;

import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.general.UnitRectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.InputComponent;
import guipackage.gui.components.boxes.SimpleBox;
import guipackage.threads.AnimationFactory;
import guipackage.threads.AnimationFactory.Animations;
import guipackage.threads.ThreadController;

public class CheckBoxInput extends InputComponent<Boolean> {

	private SimpleBox innerBox;
	private Image tick;
	private ThreadController transform;

	public CheckBoxInput(UnitRectangle r) {
		super(r);
		setValue(false);
		setColor(GUI.focus);
		setRounded(true);

		//Inner box
		innerBox = new SimpleBox(new UnitRectangle(50, 50, 0, 0), GUI.focus2);
		innerBox.setRounded(true);
		addComponent(innerBox);
		
		//Tick
		tick = new Image(new UnitRectangle(15, 10, 80, 80), "ok.png");
		tick.setVisible(false);
		addComponent(tick);
	}

	@Override
	public void actionsUpdated() {
		if (getValue()) {
			tick.setVisible(true);
			innerBox.setRec(new UnitRectangle(0, 0, 100, 100));
		}
		else {
			tick.setVisible(false);
			innerBox.setRec(new UnitRectangle(50, 50, 0, 0));
		}
	}

	@Override
	public void doClick(Point p) {
		if (hasActions()) {
			getActions().submit(!getValue());
			setValue(getActions().get());
		}
		else setValue(!getValue());
		
		if (transform!=null) transform.end();
		if (getValue()) {
			tick.setVisible(true);
			transform = AnimationFactory.getAnimation(innerBox, Animations.Transform, new Rectangle(0, 0, 100, 100));
		}
		else {
			tick.setVisible(false);
			transform = AnimationFactory.getAnimation(innerBox, Animations.Transform, new Rectangle(50, 50, 0, 0));
		}
		transform.setWait(5);
		transform.start();
		
		super.doClick(p);
	}
}
