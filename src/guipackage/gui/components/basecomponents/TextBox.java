package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyEvent;

import guipackage.general.Getter;
import guipackage.general.Point;
import guipackage.general.Submitter;
import guipackage.general.UnitRectangle;
import guipackage.general.Utils;
import guipackage.gui.GUI;
import guipackage.gui.IO;
import guipackage.gui.components.InputComponent;
import guipackage.gui.components.boxes.SimpleBox;
import guipackage.threads.AnimationFactory;
import guipackage.threads.AnimationFactory.Animations;
import guipackage.threads.ThreadController;

public class TextBox extends InputComponent<String> {
	
	private String text;
	private SimpleBox mainBox;
	public Label textLabel;
	public Label descriptionLabel;
	private Getter<String> description;
	
	//Cursor
	private ThreadController cursorAni;
	public String cursor;
	
	public TextBox(UnitRectangle r, String initialText) {
		super(r);
		text = initialText;
		if (text==null) text = "";
		cursor = "";
		
		mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100), GUI.focus);
		mainBox.setRounded(true);
		addComponent(mainBox);
		
		textLabel = new Label(new UnitRectangle(8, 55, 0, 0), text, new Font(GUI.baseFont, Font.ITALIC, 15), new Color(200, 200, 200));
		mainBox.addComponent(textLabel);
		descriptionLabel = new Label(new UnitRectangle(8, 55, 0, 0), text, new Font(GUI.baseFont, Font.ITALIC, 15), new Color(140, 140, 140));
		descriptionLabel.setVisible(false);
		mainBox.addComponent(descriptionLabel);
	}
	
	@Override
	public void actionsUpdated() {
		text = getActions().get();
		textLabel.setText(text+cursor);
	}
	
	public void setText(String t) {
		text = t;
		textLabel.setText(text+cursor);
	}
	
	public String getText() {return text;}
	
	public void setDescriptionAction(Getter<String> d) {
		description = d;
		if (text.isEmpty()) {
			descriptionLabel.setText(description.get());
			descriptionLabel.setVisible(true);
		}
		else descriptionLabel.setVisible(false);
	}
	
	@Override
	public void doClick(Point p) {
		setSelected(true);
		TextBox t = this;
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
			getActions().submit(text); //Submit input
			text = getActions().get(); //Update text
			textLabel.setText(text);
		}
		if (text.isEmpty()&&description!=null) {
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
		Utils.setCursor(Cursor.HAND_CURSOR);
	}
	
	@Override
	public void doUnhover() {
		Utils.setCursor(Cursor.DEFAULT_CURSOR);
	}
	
	@Override
	public void doKeyPress(KeyEvent e) {
		if (e.getExtendedKeyCode()==8&&!text.isEmpty()) text = text.substring(0, text.length()-1);
		else text += e.getKeyChar();
		textLabel.setText(text+cursor);
	}
}
