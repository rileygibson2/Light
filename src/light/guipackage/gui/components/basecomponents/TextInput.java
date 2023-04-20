package light.guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyEvent;

import light.general.ThreadController;
import light.guipackage.general.GUIUtils;
import light.guipackage.general.Getter;
import light.guipackage.general.Point;
import light.guipackage.general.Submitter;
import light.guipackage.general.UnitRectangle;
import light.guipackage.gui.IO;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.InputComponent;
import light.guipackage.threads.AnimationFactory;
import light.guipackage.threads.AnimationFactory.Animations;

public class TextInput extends InputComponent<String> {
	
	public Label textLabel;
	public Label descriptionLabel;
	private Getter<String> description;
	
	//Cursor
	private ThreadController cursorAni;
	public String cursor;
	
	public TextInput(UnitRectangle r) {
		super(r);
		if (getValue()==null) setValue("");
		cursor = "";
		
		setColor(Styles.focus);
		setRounded(true);
		
		textLabel = new Label(new UnitRectangle(8, 55, 0, 0), getValue(), new Font(Styles.baseFont, Font.ITALIC, 15), new Color(200, 200, 200));
		addComponent(textLabel);
		descriptionLabel = new Label(new UnitRectangle(8, 55, 0, 0), getValue(), new Font(Styles.baseFont, Font.ITALIC, 15), new Color(140, 140, 140));
		descriptionLabel.setVisible(false);
		addComponent(descriptionLabel);

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
		textLabel.setText(getValue());
	}
	
	@Override
	public void setValue(String v) {
		super.setValue(v);
		textLabel.setText(getValue()+cursor);
	}
	
	public void setDescriptionAction(Getter<String> d) {
		description = d;
		if (getValue().isEmpty()) {
			descriptionLabel.setText(description.get());
			descriptionLabel.setVisible(true);
		}
		else descriptionLabel.setVisible(false);
	}
	
	public void click(Point p) {
		setSelected(true);
		TextInput t = this;
		IO.getInstance().registerKeyListener(this, new Submitter<KeyEvent>() {
			@Override
			public void submit(KeyEvent e) {
				t.doKeyPress(e);
			}
		});
		
		cursorAni = AnimationFactory.getAnimation(this, Animations.CursorBlip);
		cursorAni.start();
		descriptionLabel.setVisible(false);
		super.doClick(p);
	}
	
	@Override
	public void doDeselect() {
		IO.getInstance().deregisterKeyListener(textLabel);;
		setSelected(false);
		if (cursorAni!=null) cursorAni.end();
		
		//Submit input
		if (hasActions()) {
			getActions().submit(getValue()); //Submit input
			setValue(getActions().get()); //Update text
			textLabel.setText(getValue());
		}
		if (getValue().isEmpty()&&description!=null) {
			descriptionLabel.setText(description.get());
			descriptionLabel.setVisible(true);
		}
		else descriptionLabel.setVisible(false);
		super.doDeselect();
	}
	
	@Override
	public void destroy() {
		if (cursorAni!=null) cursorAni.end();
		super.destroy();
	}
	
	@Override
	public void doHover() {
		GUIUtils.setCursor(Cursor.HAND_CURSOR);
	}
	
	@Override
	public void doUnhover() {
		GUIUtils.setCursor(Cursor.DEFAULT_CURSOR);
	}
	
	@Override
	public void doKeyPress(KeyEvent e) {
		if (e.getExtendedKeyCode()==8&&!getValue().isEmpty()) setValue(getValue().substring(0, getValue().length()-1));
		else setValue(getValue()+e.getKeyChar());
		textLabel.setText(getValue()+cursor);
	}
}
