package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;

import light.commands.commandline.CommandLine;
import light.commands.commandline.CommandProxy;
import light.executors.Executor;
import light.general.ConsoleAddress;
import light.guipackage.general.GUIUtils;
import light.guipackage.general.Point;
import light.guipackage.general.Submitter;
import light.guipackage.general.UnitRectangle;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.basecomponents.Image;
import light.guipackage.gui.components.basecomponents.Label;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.stores.Group;
import light.stores.Preset;
import light.stores.effects.Effect;

public class PoolCellGUI extends Component {
    
    PoolGUI poolGUI;
    ConsoleAddress address;
    
    SimpleBox mainBox;
    Label idLabel;
    Image dragIcon;
    
    //Styles
    private final static Color cellBotCol = new Color(50, 50, 50);
    
    private final static Color[] typeCols = new Color[] {
        //Presets
        Color.BLUE,
        new Color(0, 150, 200),
        new Color(0, 150, 0),
        Color.PINK,
        new Color(100, 0, 255),
        new Color(150, 0, 0),
        new Color(150, 0, 0),

        //Other pools
        new Color(150, 150, 150),
        new Color(150, 0, 0),
        new Color(50, 0, 200),
    };
    
    public PoolCellGUI(UnitRectangle r, PoolGUI poolGUI, ConsoleAddress address, boolean titleCell) {
        super(r);
        this.poolGUI = poolGUI;
        this.address = address;
        
        //Box
        mainBox = new SimpleBox(new UnitRectangle(1, 1, 98, 98));
        mainBox.setRounded(20);
        //mainBox.setBorder(1, Styles.fg);
        mainBox.setBorder(1, GUIUtils.modulateColor(getTypeColor(), 0.5));
        addComponent(mainBox);
        
        //Title cell
        if (titleCell) {
            mainBox.setColor(GUIUtils.modulateColor(getTypeColor(), 0.5));
            mainBox.setBorderColor(getTypeColor());
            //Image, address and title
            mainBox.addComponent(new Image(new UnitRectangle(20, 5, 60, 40), "icon.png"));
            Label l = new Label(new UnitRectangle(0, 62, 100, 22), address.getPrefix()+"", new Font("Geneva", Font.BOLD, 20), Styles.textMain);
            l.setTextXCentered(true);
            l.fitFont();
            mainBox.addComponent(l);
            String text = getTypeText();
            l = new Label(new UnitRectangle(0, 80, 100, 22), text, new Font("Geneva", Font.BOLD, 20), Styles.textMain);
            l.setTextXCentered(true);
            l.fitFont();
            mainBox.addComponent(l);
            
            return;
        }
        
        //All non title cells have id label
        idLabel = new Label(new UnitRectangle(10, 5, 10, 15), address.getSuffix()+"", new Font("Geneva", Font.PLAIN, 20), Styles.textDull);
        idLabel.setTextYCentered(true);
        idLabel.fitFont();
        
        if (poolGUI.getPool().contains(address)) { //Filled cell
            mainBox.setColor(new Color(10, 10, 10));
            mainBox.setBorderColor(getTypeColor());
            idLabel.setTextColor(Styles.textMain);
            
            //Bottom gray and dividor
            SimpleBox bottom = new SimpleBox(new UnitRectangle(0, 50, 100, 50), new Color(80, 80, 80));
            bottom.setRounded(new int[] {2, 3}, 20);
            mainBox.addComponent(bottom);
            mainBox.addComponent(new SimpleBox(new UnitRectangle(0, 49, 100, 2), new Color(150, 150, 150)));
            
            //Name
            Label name = new Label(new UnitRectangle(0, 25, 100, 50), "aaa", new Font("Geneva", Font.PLAIN, 20), Styles.textMain);
            name.setTextCentered(true);
            name.fitFont();
            name.setText(poolGUI.getPool().get(address).getLabel());
            bottom.addComponent(name);

        }
        else { //Empty cell
            mainBox.setColor(new Color(20, 20, 20));
        }
        
        //Add last so top priority
        mainBox.addComponent(idLabel);

        //Click action
		setClickAction(new Submitter<Point>() {
			@Override
			public void submit(Point p) {
				click(p);
			}
		});
    }
    
    private Color getTypeColor() {
        if (address.matchesScope(Preset.class)) return typeCols[Preset.getTypeFromAddress(address).ordinal()];
        if (address.matchesScope(Group.class)) return typeCols[7];
        if (address.matchesScope(Effect.class)) return typeCols[8];
        if (address.matchesScope(Executor.class)) return typeCols[9];
        return null;
    }

    private String getTypeText() {
        if (address.matchesScope(Preset.class)) return Preset.getTypeFromAddress(address).toString();
        return address.getScope().getSimpleName();
    }
    
    public void addDragIcon() {
        dragIcon = new Image(new UnitRectangle(60, 60, 40, 40), "drag.png") {
            
            
            
        };
        mainBox.addComponent(dragIcon);
    }
    
    public void removeDragIcon() {mainBox.removeComponent(dragIcon);}
    
    public void click(Point p) {
        CommandLine.getInstance().addToCommand(new CommandProxy(address));
    }
    
}
