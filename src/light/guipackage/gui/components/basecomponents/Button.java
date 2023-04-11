package light.guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;

import light.general.ThreadController;
import light.guipackage.general.GUIUtils;
import light.guipackage.general.Point;
import light.guipackage.general.Submitter;
import light.guipackage.general.UnitRectangle;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.boxes.SimpleBox;

public class Button extends Component {

	private ThreadController hover;
	private ThreadController unHover;
	public SimpleBox mainBox;

	public Button(UnitRectangle r, Color col, String imgSrc) {
		super(r);
		mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100), col);
		mainBox.setRounded(true);
		addComponent(mainBox);

		Image img = new Image(new UnitRectangle(0, 0, 100, 100), imgSrc);
		mainBox.addComponent(img);

		//Click action
		setClickAction(new Submitter<Point>() {
			@Override
			public void submit(Point p) {
				click(p);
			}
		});
	}
	
	public void addButtonComponent(Component c) {
		mainBox.addComponent(c);
	}
	
	public Color getColor() {return mainBox.getColor();}
	
	public void setColor(Color col) {mainBox.setColor(col);}
	
	public void setOval(boolean o) {mainBox.setOval(o);}

	public void click(Point p) {
		GUIUtils.setCursor(Cursor.DEFAULT_CURSOR);
	};


	// @Override
	// public void doHover() {
	// 	if (isHoverPaused()) return;
	// 	if (!isHovered()) {
	// 		if (unHover!=null) unHover.end();
	// 		hover = AnimationFactory.getAnimation(this, Animations.Transform, new Rectangle(r.x-(r.width*0.1), r.y-(r.height*0.1), r.width*1.2, r.height*1.2), 15);
	// 		hover.start();
	// 		Utils.setCursor(Cursor.HAND_CURSOR);
	// 	}
	// 	super.doHover();
	// }

	// @Override
	// public void doUnhover() {
	// 	if (isHoverPaused()) return;
	// 	if (isHovered()) {
	// 		if (hover!=null) hover.end();
	// 		unHover = AnimationFactory.getAnimation(this, Animations.Transform, getOriginalRec().clone(), 15);
	// 		unHover.start();
	// 		Utils.setCursor(Cursor.DEFAULT_CURSOR);
	// 	}
	// 	super.doUnhover();
	// }

	@Override
	public void destroy() {
		if (hover!=null) hover.end();
		if (unHover!=null) unHover.end();
		super.destroy();
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
	}
}
