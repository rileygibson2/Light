package guipackage.gui.components.complexcomponents;

import java.awt.Font;

import guipackage.general.UnitRectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.Image;
import guipackage.gui.components.basecomponents.Label;
import guipackage.gui.components.boxes.SimpleBox;


public class CommandLineGUI extends Component {

    private SimpleBox mainBox;
    private Label prefix;
    private Label command;

    public CommandLineGUI(UnitRectangle r) {
        super(r);

        mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100), GUI.fg);
        mainBox.setRounded(true);
        
        addComponent(mainBox);

        mainBox.addComponent(new Image(new UnitRectangle(0.5, 20, 4, 60), "icon.png"));

        prefix = new Label(new UnitRectangle(5, 50, 0, 0), "[Command]>", new Font("Geneva", Font.PLAIN, 14), GUI.textDull);
        prefix.setTextYCentered(true);
        mainBox.addComponent(prefix);
        
        command = new Label(new UnitRectangle(prefix.getWidth().v+2, 50, 0, 0), "", new Font("Geneva", Font.PLAIN, 14), GUI.textMain);
        command.setTextYCentered(true);
        mainBox.addComponent(command);
    }

    public void setPrefixString(String p) {
        prefix.setText("["+p+"]>");
    }

    public void setCommandString(String c) {command.setText(c);} 

}
