package light.guipackage.gui.components.basecomponents;

import light.general.ThreadController;
import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.general.Submitter;
import light.guipackage.general.UnitRectangle;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.InputComponent;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.guipackage.threads.AnimationFactory;
import light.guipackage.threads.AnimationFactory.Animations;

public class CheckBoxInput extends InputComponent<Boolean> {

	private SimpleBox innerBox;
	private Image tick;
	private ThreadController transform;

	public CheckBoxInput(UnitRectangle r) {
		super(r);
		setValue(false);
		setColor(Styles.focus);
		setRounded(true);

		//Inner box
		innerBox = new SimpleBox(new UnitRectangle(50, 50, 0, 0), Styles.focus2);
		innerBox.setRounded(true);
		addComponent(innerBox);
		
		//Tick
		tick = new Image(new UnitRectangle(15, 10, 80, 80), "ok.png");
		tick.setVisible(false);
		addComponent(tick);

		//Click action
		setClickAction(new Submitter<Point>() {
			@Override
			public void submit(Point p) {
				click(p);
			}
		});
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

	public void click(Point p) {
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
	}
}
