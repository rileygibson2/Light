package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;

import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.basecomponents.Image;
import light.guipackage.gui.components.basecomponents.Label;
import light.guipackage.gui.components.boxes.CollumnBox;
import light.guipackage.gui.components.boxes.FlexBox;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.physical.PhysicalKey;
import light.physical.PhysicalKeyPads;
import light.uda.KeyWindow;
import light.uda.guiinterfaces.KeyWindowGUIInterface;

public class KeyWindowGUI extends SimpleBox implements KeyWindowGUIInterface {
    
    private KeyWindow keyWindow;
    private FlexBox mainBox;
    
    public KeyWindowGUI(UnitRectangle r, KeyWindow keyWindow) {
        super(r);
        this.keyWindow = keyWindow; 
        
        setColor(new Color(20, 20, 20));
        setRounded(true);
        setMaxHeight(getHeight());
        setMaxWidth(getWidth());
        setClickAction(() -> {}); //To stop click chain
        
        //Top bar
        SimpleBox topBar = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 4, Unit.vh));
        topBar.setPosition(Position.Relative);
        addComponent(topBar);
        
        SimpleBox title = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 20, Unit.vw, 100, Unit.pch));
        title.setFill(Fill.Horizontal);
        title.setColor(new Color(0, 0, 180));
        title.setBorder(1, new Color(10, 100, 255));
        title.setRounded(10);
        topBar.addComponent(title);
        Label titleLabel =  new Label(new UnitRectangle(3, Unit.pcw, 0, Unit.px, 97, Unit.pcw, 100, Unit.pch), "Command Palate", new Font(Styles.baseFont, Font.BOLD, 16), new Color(230, 230, 230));
        titleLabel.setTextYCentered(true);
        title.addComponent(titleLabel);
        
        Image exit = new Image(new UnitRectangle(0, Unit.vw, 0, Unit.vh, 5, Unit.vw, 100, Unit.pch), "exit.png");
        exit.setPosition(Position.Relative);
        exit.setFloat(Float.Right);
        exit.setColor(Styles.bg);
        exit.setBorder(1, new Color(80, 80, 80));
        exit.setRounded(10);
        topBar.addComponent(exit);
        
        addComponent(topBar);

        mainBox = new FlexBox(new UnitPoint(0, Unit.vw, 0, Unit.vh));
        mainBox.setPosition(Position.Relative);
        //mainBox.setOverflow(Overflow.ScrollX);
        mainBox.setMaxWidth(getFuncWidth());
        mainBox.setMaxHeight(getFuncHeight());
        addComponent(mainBox);
        
        buildKeys();
    }
    
    private void buildKeys() {
        //Top row
        buildPad(PhysicalKeyPads.topCommandPad, new UnitPoint(0, Unit.vw, 1, Unit.vh));
        buildPad(PhysicalKeyPads.storePad, new UnitPoint(13, Unit.vw, 1, Unit.vh));
        buildPad(PhysicalKeyPads.topRightCommandPad, new UnitPoint(26.5, Unit.vw, 1, Unit.vh));
        //Bottom row
        buildPad(PhysicalKeyPads.leftCommandPad, new UnitPoint(0, Unit.vw, 28, Unit.vh));
        buildPad(PhysicalKeyPads.numPad, new UnitPoint(10, Unit.vw, 28, Unit.vh));
        buildPad(PhysicalKeyPads.modulatePad, new UnitPoint(26.5, Unit.vw, 28, Unit.vh));
        buildPad(PhysicalKeyPads.pointerControlPad, new UnitPoint(26.5, Unit.vw, 40, Unit.vh));
    }

    private void buildPad(PhysicalKey[][] pad, UnitPoint pos) {
        CollumnBox padBox = new CollumnBox(pos);
        mainBox.addComponent(padBox);
        for (PhysicalKey[] row : pad) {
            FlexBox rowBox = new FlexBox(new UnitPoint(0, Unit.vw, 0.2, Unit.vw));
            padBox.addComponent(rowBox);
            for (PhysicalKey key : row) {
                if (key==null) { //Need empty box
                    SimpleBox b = new SimpleBox(new UnitRectangle(0.2, Unit.vw, 0, Unit.vh, 3.5, Unit.vw, 3.5, Unit.vw));
                    b.setPosition(Position.Relative);
                    rowBox.addComponent(b);
                    continue;
                }
                Label b = new Label(new UnitRectangle(0.2, Unit.vw, 0, Unit.vh, 3.5, Unit.vw, 3.5, Unit.vw), new Font(Styles.baseFont, Font.BOLD, 10), new Color(230, 230, 230));
                b.setPosition(Position.Relative);
                b.setText(key.getText());
                b.setColor(new Color(20, 20, 20));
                //b.setTextColor(new Color(255, 200, 0));
                b.setRounded(true);
                b.setBorder(0.8, new Color(255, 200, 0));
                b.setTextCentered(true);
                b.setTag(key);
                b.setClickAction(() -> keyWindow.keyClicked((PhysicalKey) b.getTag()));
                rowBox.addComponent(b);
            }
        }
    }
    
    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
