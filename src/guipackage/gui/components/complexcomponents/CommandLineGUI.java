package guipackage.gui.components.complexcomponents;

import java.awt.Font;

import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.Image;
import guipackage.gui.components.basecomponents.Label;
import guipackage.gui.components.basecomponents.SimpleBox;


public class CommandLineGUI extends Component {

    private SimpleBox mainBox;
    private Label prefix;
    private Label command;

    public CommandLineGUI(Rectangle r) {
        super(r);

        mainBox = new SimpleBox(new Rectangle(0, 0, 100, 100), GUI.fg);
        mainBox.setRounded(true);
        addComponent(mainBox);

        mainBox.addComponent(new Image(new Rectangle(0.5, 15, 4, 70), "icon.png"));

        prefix = new Label(new Point(5, 50), "[Command]>", new Font("Geneva", Font.PLAIN, 14), GUI.textDull);
        prefix.setYCentered(true);
        mainBox.addComponent(prefix);
        
        command = new Label(new Point(prefix.getWidth()+2, 50), "", new Font("Geneva", Font.PLAIN, 14), GUI.textMain);
        command.setYCentered(true);
        mainBox.addComponent(command);
    }

    public void setPrefixString(String p) {
        prefix.setText("["+p+"]>");
        command.setX(prefix.getX()+prefix.getWidth());
    }

    public void setCommandString(String c) {command.setText(c);} 

}
