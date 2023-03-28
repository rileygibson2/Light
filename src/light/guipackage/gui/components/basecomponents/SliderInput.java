package light.guipackage.gui.components.basecomponents;

import java.awt.Color;

import light.guipackage.general.Point;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.components.InputComponent;
import light.guipackage.gui.components.boxes.SimpleBox;

public class SliderInput extends InputComponent<Double> {

	private SimpleBox mainBox;
	private SimpleBox ball;

	private SimpleBox groove;
	private SimpleBox grooveFill;
	
	public SliderInput(UnitRectangle r) {
		super(r);

		//Main box
		mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100), new Color(70, 70, 70));
		mainBox.setRounded(new int[]{4, 3});
		mainBox.increasePriority();
		addComponent(mainBox);

		//Groove
		groove = new SimpleBox(new UnitRectangle(10, 40, 80, 20), new Color(100, 100, 100));
		mainBox.addComponent(groove);
		
		//Coloured Groove
		grooveFill = new SimpleBox(new UnitRectangle(groove.getX(), groove.getY(), new UnitValue(0, Unit.pcw), groove.getHeight()), new Color(150, 100, 100));
		mainBox.addComponent(grooveFill);

		//Ball
		ball = new SimpleBox(new UnitRectangle(groove.getX().v, 20, 14, 60), new Color(255, 100, 100));
		ball.setOval(true);
		mainBox.addComponent(ball);
	}

	@Override
	public void setValue(Double v) {
		super.setValue(v/=100);
		ball.setX(new UnitValue(groove.getX().v+(getValue()*groove.getWidth().v)-ball.getWidth().v/2, Unit.pcw));
		grooveFill.setWidth(new UnitValue(getValue()*groove.getWidth().v, Unit.pcw));
	}
	
	@Override
	public void doClick(Point p) {
		double x = scalePoint(p).x*100;
		
		if (x>=groove.getX().v&&x<=groove.getX().v+groove.getWidth().v) { //Check within bounds of groove
			setValue(((x-groove.getX().v)/groove.getWidth().v)*100);
			ball.setX(new UnitValue(x-ball.getWidth().v/2, Unit.pcw));
			grooveFill.setWidth(new UnitValue(x-grooveFill.getX().v, Unit.pcw));
		}
		if (hasActions()) getActions().submit(getValue());

		super.doClick(p);
	}
	
	@Override
	public void doDrag(Point entry, Point current) {
		double x = scalePoint(current).x*100;
		
		if (x>=groove.getX().v&&x<=groove.getX().v+groove.getWidth().v) { //Check within bounds of groove
			setValue(((x-groove.getX().v)/groove.getWidth().v)*100);
			ball.setX(new UnitValue(x-ball.getWidth().v/2, Unit.pcw));
			grooveFill.setWidth(new UnitValue(x-grooveFill.getX().v, Unit.pcw));
		}
		if (hasActions()) getActions().submit(getValue());
		
		super.doDrag(entry, current);
	}
}
