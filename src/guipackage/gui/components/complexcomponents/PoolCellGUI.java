package guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;

import guipackage.general.Rectangle;
import guipackage.general.UnitRectangle;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.Image;
import guipackage.gui.components.basecomponents.Label;
import guipackage.gui.components.basecomponents.SimpleBox;

public class PoolCellGUI extends Component {

    public enum PoolCellType {
        Title,
        Filled,
        Empty
    };

    PoolGUI poolGUI;
    PoolCellType type;

    SimpleBox mainBox;
    Label idLabel;
    Image dragIcon;

    public PoolCellGUI(UnitRectangle r, PoolGUI poolGUI, PoolCellType type, int id) {
        super(r);
        
        this.poolGUI = poolGUI;
        this.type = type;

        mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
        mainBox.setRounded(20);
        mainBox.setBorder(1, GUI.fg);
        addComponent(mainBox);

        idLabel = new Label(new UnitRectangle(10, 5, 0, 15), id+"", new Font("Geneva", Font.PLAIN, 20), GUI.textDull);
        idLabel.setTextYCentered(true);
        idLabel.fitFont();
        mainBox.addComponent(idLabel);

        switch (type) {
            case Title:
                mainBox.setColor(Color.BLUE);
                mainBox.removeComponent(idLabel);
                
                //Image address and title
                mainBox.addComponent(new Image(new UnitRectangle(20, 5, 60, 40), "icon.png"));
                Label l = new Label(new UnitRectangle(0, 62, 100, 22), poolGUI.pool.address.prefix+"", new Font("Geneva", Font.BOLD, 20), GUI.textMain);
                l.setTextXCentered(true);
                l.fitFont();
                mainBox.addComponent(l);
                l = new Label(new UnitRectangle(0, 80, 100, 22), poolGUI.pool.type.toString(), new Font("Geneva", Font.BOLD, 20), GUI.textMain);
                l.setTextXCentered(true);
                l.fitFont();
                mainBox.addComponent(l);

                break;
            case Empty:
                mainBox.setColor(new Color(20, 20, 20));
                break;
            case Filled:
                idLabel.setLabelColor(GUI.textMain);
                break;
        }
    }

    public void addDragIcon() {
        dragIcon = new Image(new UnitRectangle(60, 60, 40, 40), "drag.png") {
            

            
        };
        mainBox.addComponent(dragIcon);
    }

    public void removeDragIcon() {mainBox.removeComponent(dragIcon);}
    
}
