package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyEvent;

import guipackage.general.Getter;
import guipackage.general.Point;
import guipackage.general.Submitter;
import guipackage.general.UnitRectangle;
import guipackage.general.GUIUtils;
import guipackage.gui.GUI;
import guipackage.gui.IO;
import guipackage.gui.components.InputComponent;
import guipackage.gui.components.boxes.SimpleBox;
import guipackage.threads.AnimationFactory;
import guipackage.threads.AnimationFactory.Animations;
import guipackage.threads.ThreadController;

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
		
		setColor(GUI.focus);
		setRounded(true);
		
		textLabel = new Label(new UnitRectangle(8, 55, 0, 0), getValue(), new Font(GUI.baseFont, Font.ITALIC, 15), new Color(200, 200, 200));
		addComponent(textLabel);
		descriptionLabel = new Label(new UnitRectangle(8, 55, 0, 0), getValue(), new Font(GUI.baseFont, Font.ITALIC, 15), new Color(140, 140, 140));
		descriptionLabel.setVisible(false);
		addComponent(descriptionLabel);
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
	
	@Override
	public void doClick(Point p) {
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
