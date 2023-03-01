package guipackage.gui.components.basecomponents;

import java.awt.Color;

import guipackage.general.Point;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.components.InputComponent;
import guipackage.gui.components.boxes.SimpleBox;

public class Slider extends InputComponent<Double> {

	private SimpleBox mainBox;
	private SimpleBox ball;

	private double value;
	private SimpleBox groove;
	private SimpleBox grooveFill;
	
	public Slider(UnitRectangle r) {
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

	public void setValue(double v) {
		value = v;
		v /= 100;
		ball.setX(new UnitValue(groove.getX().v+(v*groove.getWidth().v)-ball.getWidth().v/2, Unit.pcw));
		grooveFill.setWidth(new UnitValue(v*groove.getWidth().v, Unit.pcw));
	}
	
	public double getValue() {return value;}
	
	@Override
	public void doClick(Point p) {
		double x = scalePoint(p).x*100;
		
		if (x>=groove.getX().v&&x<=groove.getX().v+groove.getWidth().v) { //Check within bounds of groove
			value = ((x-groove.getX().v)/groove.getWidth().v)*100;
			ball.setX(new UnitValue(x-ball.getWidth().v/2, Unit.pcw));
			grooveFill.setWidth(new UnitValue(x-grooveFill.getX().v, Unit.pcw));
		}
		if (hasActions()) getActions().submit(value);

		super.doClick(p);
	}
	
	@Override
	public void doDrag(Point entry, Point current) {
		double x = scalePoint(current).x*100;
		
		if (x>=groove.getX().v&&x<=groove.getX().v+groove.getWidth().v) { //Check within bounds of groove
			value = ((x-groove.getX().v)/groove.getWidth().v)*100;
			ball.setX(new UnitValue(x-ball.getWidth().v/2, Unit.pcw));
			grooveFill.setWidth(new UnitValue(x-grooveFill.getX().v, Unit.pcw));
		}
		if (hasActions()) getActions().submit(value);
		
		super.doDrag(entry, current);
	}
}
