package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import guipackage.general.Point;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.boxes.SimpleBox;
import guipackage.threads.AnimationFactory;
import guipackage.threads.AnimationFactory.Animations;
import guipackage.threads.ThreadController;

public class MessageBox extends Component {

	public static Color error = new Color(220, 100, 100);
	public static Color ok = new Color(100, 200, 100);
	//public static Color info = new Color(80, 80, 80);
	public static Color info = GUI.focus;
	public static Color update = new Color(100, 100, 200);
	
	private SimpleBox mainBox;
	private Label label;
	private ThreadController move;
	private ThreadController fade;
	

	public MessageBox(String text, Color col, double goalY, int hold) {
		super(new UnitRectangle(50, -15, 10, 10));

		//Smother
		/*SimpleBox smother = new SimpleBox(new Rectangle(0, 0, 100, 100), new Color(0, 0, 0));
		smother.setOpacity(50);
		smother.setAbsolute(true);
		addComponent(smother);*/

		//Main box
		mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100), col);
		mainBox.setRounded(true);
		mainBox.increasePriority();
		addComponent(mainBox);
		
		label = new Label(new UnitRectangle(49, 50, 0, 0), text, new Font(GUI.baseFont, Font.BOLD, 12), new Color(255, 255, 255));
		label.setTextCentered(true);
		mainBox.addComponent(label);
		
		double w = label.getWidth().v+5;
		double h = label.getHeight().v+5;
		setX(new UnitValue((100-w)/2, getWidth().u));
		setWidth(new UnitValue(w, getWidth().u));
		setHeight(new UnitValue(h, getHeight().u));
		
		fade = AnimationFactory.getAnimation(this, Animations.Fade, 100);
		fade.start();
		move = AnimationFactory.getAnimation(this, Animations.MoveTo, new Point(getX().v, goalY));
		move.setFinishAction(() -> {
			move.sleep(hold);
			GUI.getInstance().removeMessage(this);
			fade = AnimationFactory.getAnimation(this, Animations.Fade, 0);
			fade.setFinishAction(() -> removeFromParent());
			fade.start();
		});
		move.start();
		
		setOpacity(0);
	}
	
	public void updateGoal(double goalY) {
		List<Object> extras = new ArrayList<>();
		extras.add(new Point(getX().v, goalY));
		
		if (move!=null&&!move.isDoomed()) move.setExtras(extras);
		else {
			move = AnimationFactory.getAnimation(this, Animations.MoveTo, new Point(getX().v, goalY));
			move.start();
		}
	}
}
