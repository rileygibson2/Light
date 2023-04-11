package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;

import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.Element.Fill;
import light.guipackage.gui.Element.Position;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.basecomponents.Image;
import light.guipackage.gui.components.basecomponents.Label;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.uda.FixtureWindow;
import light.uda.guiinterfaces.FixtureWindowGUIInterface;


public class FixtureWindowGUI extends Component implements FixtureWindowGUIInterface {
    
    private FixtureWindow fixtureWindow;
    private SimpleBox mainBox;
    
    public FixtureWindowGUI(UnitRectangle r, FixtureWindow fixtureWindow) {
        super(r);
        this.fixtureWindow = fixtureWindow;
        
        mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
        addComponent(mainBox);

        SimpleBox sB = new SimpleBox(new UnitRectangle(0, 0, 100, 100), Color.RED);
        mainBox.addComponent(sB);

        //Top bar
		SimpleBox topBar = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 4, Unit.vh));
		addComponent(topBar);
		
		Label title = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 20, Unit.vw, 100, Unit.pch), "Fixture Window", new Font(GUI.baseFont, Font.BOLD, 18), new Color(230, 230, 230));
		title.setFill(Fill.Horizontal);
		title.setColor(new Color(0, 0, 180));
		title.setBorder(1, new Color(10, 100, 255));
		title.setRounded(10);
		title.setTextYCentered(true);
		topBar.addComponent(title);
		
		Image exit = new Image(new UnitRectangle(0, Unit.vw, 0, Unit.vh, 5, Unit.vw, 100, Unit.pch), "exit.png");
		exit.setPosition(Position.Relative);
		exit.setFloat(Float.Right);
		exit.setColor(GUI.bg);
		exit.setBorder(1, new Color(80, 80, 80));
		exit.setRounded(10);
		topBar.addComponent(exit);

        mainBox.addComponent(topBar);
    }
    
    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
