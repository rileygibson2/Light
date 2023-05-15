package light.guipackage.gui.components.primitives;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyEvent;

import light.general.ThreadController;
import light.guipackage.general.GUIUtils;
import light.guipackage.general.UnitRectangle;
import light.guipackage.gui.IO;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.InputComponent;

public class TextInput extends InputComponent<String> {
	
	private Label label;
	
	//Cursor
	private ThreadController cursorAni;
	public String cursor;
	
	public TextInput(UnitRectangle r) {
		super(r);
		cursor = "";
		
		setColor(Styles.focus);
		setRounded(true);
		setSelectAction(() -> select());
		setSelectAction(() -> deselect());
		
		label = new Label(new UnitRectangle(0, 0, 100, 100), new Font(Styles.baseFont, Font.BOLD, 12), new Color(200, 200, 200));
		label.setColor(new Color(20, 20, 20));
        label.setBorder(new Color(100, 100, 100));
        label.setRounded(true);
		label.setTextCentered(true);
		
		addComponent(label);

		if (getValue()==null) setValue(""); 
	}

	public Label getLabel() {return label;}
	
	@Override
	public void actionsUpdated() {
		setValue(getAction().get());
		label.setText(getValue());
	}
	
	@Override
	public void setValue(String v) {
		super.setValue(v);
		label.setText(getValue()+cursor);
	}
	
	public void select() {
		IO.getInstance().setOverrideKeyListener(e -> keyPressed(e));
		
		//cursorAni = AnimationFactory.getAnimation(this, Animations.CursorBlip);
		//cursorAni.start();
	}
	
	public void deselect() {
		IO.getInstance().deregisterKeyListener(label);
		if (cursorAni!=null) cursorAni.end();
		
		//Submit input
		if (hasActions()) {
			submitAction().submit(getValue()); //Submit input
			setValue(getAction().get()); //Update text
			label.setText(getValue());
		}
		/*if (getValue().isEmpty()&&description!=null) {
			descriptionLabel.setText(description.get());
			descriptionLabel.setVisible(true);
		}
		else descriptionLabel.setVisible(false);*/
	}

	public void keyPressed(KeyEvent e) {
		//Submit on enter
		if (e.getExtendedKeyCode()==10) submitAction().submit(getValue());
		//Backspace
		else if (e.getExtendedKeyCode()==8&&!getValue().isEmpty()) setValue(getValue().substring(0, getValue().length()-1));
		else setValue(getValue()+e.getKeyChar());
		label.setText(getValue()+cursor);
	}
	
	@Override
	public void destroy() {
		if (cursorAni!=null) cursorAni.end();
		super.destroy();
	}
	
	public void doHover() {
		GUIUtils.setCursor(Cursor.HAND_CURSOR);
	}
	
	public void doUnhover() {
		GUIUtils.setCursor(Cursor.DEFAULT_CURSOR);
	}
}
