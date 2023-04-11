package light.guipackage.gui.components.complexcomponents;

import java.awt.Font;

import light.guipackage.general.UnitRectangle;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.basecomponents.Image;
import light.guipackage.gui.components.basecomponents.Label;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.uda.guiinterfaces.CommandLineGUIInterface;


public class CommandLineGUI extends Component implements CommandLineGUIInterface{

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
        
        command = new Label(new UnitRectangle(15, 50, 0, 0), "", new Font("Geneva", Font.PLAIN, 14), GUI.textMain);
        command.setTextYCentered(true);
        mainBox.addComponent(command);
    }

    public void setPrefixString(String p) {
        prefix.setText("["+p+"]>");
    }

    public void setCommandString(String c) {command.setText(c);}

    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    } 

}
