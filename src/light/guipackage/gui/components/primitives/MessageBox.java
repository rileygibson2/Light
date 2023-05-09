package light.guipackage.gui.components.primitives;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import light.general.ThreadController;
import light.guipackage.general.Point;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.gui.GUI;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.primitives.boxes.SimpleBox;
import light.guipackage.threads.AnimationFactory;
import light.guipackage.threads.AnimationFactory.Animations;

public class MessageBox extends Component {

	public static Color error = new Color(220, 100, 100);
	public static Color ok = new Color(100, 200, 100);
	//public static Color info = new Color(80, 80, 80);
	public static Color info = Styles.focus;
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
		mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
		mainBox.setColor(col);
		mainBox.setRounded(true);
		mainBox.increasePriority();
		addComponent(mainBox);
		
		label = new Label(new UnitRectangle(49, 50, 0, 0), text, new Font(Styles.baseFont, Font.BOLD, 12), new Color(255, 255, 255));
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
