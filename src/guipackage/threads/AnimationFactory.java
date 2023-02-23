package guipackage.threads;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import guipackage.cli.CLI;
import guipackage.general.GetterSubmitter;
import guipackage.general.Pair;
import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.TextBox;

public class AnimationFactory {

	public static enum Animations {
		//Funtionals
		CheckCondition,
		//Generics
		Paint,
		MoveTo,
		Transform,
		Fade,

		//Special
		PulseRings,
		CursorBlip
	}

	private AnimationFactory() {};

	public static ThreadController getAnimation(Object target, Animations t, Object... extras) {
		ThreadController tC = getThread(t);
		tC.setTarget(target);
		if (extras.length>0) tC.setExtras(Arrays.asList(extras));

		return tC;
	}

	public static ThreadController getAnimation(Animations t, Object... extras) {
		return getThread(t);
	}

	private static ThreadController getThread(Animations t) {
		switch (t) {
		case PulseRings: return pulseRings();
		case CursorBlip: return cursorBlip();
		case Paint: return paint();
		case MoveTo: return moveTo();
		case Transform: return transform();
		case Fade: return fade();
		case CheckCondition: return checkCondition();
		default: break;
		}
		return null;
	}
	
	public static double sineCurve(double startValue, double endValue, double p) {
		double theta = Math.PI * (p/100);
		double curvedT = (1 - Math.cos(theta))/2;
		return startValue + (endValue - startValue) * curvedT;
	}

	//Functional animations
	final static ThreadController checkCondition() {
		return new ThreadController() {
			@Override
			public void run() {
				doInitialDelay();
				@SuppressWarnings("unchecked")
				GetterSubmitter<Boolean, Boolean> gS = (GetterSubmitter<Boolean, Boolean>) getTarget();
				
				while (isRunning()) {
					if (gS.get()) {
						gS.submit(true);
						break;
					}
					iterate();
				}
				finish();
			}
		};
	}
	
	//Generic animations

	final static ThreadController moveTo() {
		return new ThreadController() {
			@Override
			public void run() {
				doInitialDelay();
				Component c = (Component) getTarget();
				Point to = (Point) extras.get(0);
				Rectangle start = c.getRec().clone();
				
				double p = 0;
				int increment = 2;
				if (extras.size()>1) increment = (int) extras.get(1);

				while (isRunning()) {
					p += increment;
					c.setX(sineCurve(start.x, to.x, p));
					c.setY(sineCurve(start.y, to.y, p));

					if (p>=100) end();
					iterate();
				}
				finish();
			}
		};
	}
	
	final static ThreadController transform() {
		return new ThreadController() {
			@Override
			public void run() {
				doInitialDelay();
				Component c = (Component) getTarget();
				Rectangle to = (Rectangle) extras.get(0);
				Rectangle start = c.getRec().clone();
				
				double p = 0;
				int increment = 2;
				if (extras.size()>1) increment = (int) extras.get(1);

				while (isRunning()) {
					p += increment;
					c.setX(sineCurve(start.x, to.x, p));
					c.setY(sineCurve(start.y, to.y, p));
					c.setWidth(sineCurve(start.width, to.width, p));
					c.setHeight(sineCurve(start.height, to.height, p));;

					if (p>=100) end();
					iterate();
				}
				finish();
			}
		};
	}
	
	final static ThreadController fade() {
		return new ThreadController() {
			@Override
			public void run() {
				doInitialDelay();
				Component[] components;
				if (getTarget() instanceof Component[]) components = (Component[]) getTarget();
				else if (getTarget() instanceof Component) components = new Component[] {(Component) getTarget()};
				else {
					CLI.error("Wrong cast in fade animation");
					return;
				}
				
				int to = (Integer) extras.get(0);
				List<Double> start = new ArrayList<>();
				for (Component c : components) start.add(c.getOpacity());
				
				double p = 0;
				int increment = 2;
				if (extras.size()>1) increment = (int) extras.get(1);

				while (isRunning()) {
					p += increment;
					for (int i=0; i<components.length; i++) {
						components[i].setOpacity(sineCurve(start.get(i), to, p));
					}

					if (p>=100) end();
					iterate();
				}
				finish();
			}
		};
	}

	final static ThreadController paint() {
		return new ThreadController() {
			@Override
			public void run() {
				doInitialDelay();
				while (isRunning()) iterate();
				finish();
			}
		};
	}

	//Special animations
	final static ThreadController pulseRings() {
		return new ThreadController() {
			@Override
			public void run() {
				setWait(40);
				doInitialDelay();
				elements = new HashSet<Object>(); //One point for each pulse, x is opacity, y is rad

				while (isRunning()) {
					@SuppressWarnings("unchecked")
					Pair<Color, Color> cols = (Pair<Color, Color>) extras.get(0);

					//Add new bubbles
					if (((getIncrement()-65)/70d)%1==0) elements.add(new Pair<Point, Color>(new Point(50, 1), cols.a));
					if ((getIncrement()/70d)%1==0) elements.add(new Pair<Point, Color>(new Point(50, 1), cols.b));

					Set<Object> toRemove = new HashSet<>();
					for (Object o : elements) {
						@SuppressWarnings("unchecked")
						Pair<Point, Color> pa = (Pair<Point, Color>) o;
						Point p = pa.a;
						p.y += 0.2; //Expand bubble
						p.x-= 1.8; //Lower opacity
						if (p.x<0) toRemove.add(o);
					}
					elements.removeAll(toRemove); //Remove invisible bubbles

					iterate();
				}

				finish();
			}
		};
	}

	final static ThreadController cursorBlip() {
		return new ThreadController() {
			@Override
			public void run() {
				doInitialDelay();
				TextBox t = (TextBox) getTarget();

				while (isRunning()) {
					if (t.cursor.isEmpty()) t.cursor = "_";
					else t.cursor = "";
					t.textLabel.setText(t.getText()+t.cursor);

					iterate();
				}

				//Reset
				t.cursor = "";
				t.textLabel.setText(t.getText());
				finish();
			}
		};
	}
}
