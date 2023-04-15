package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;

import light.fixtures.FeatureGroup;
import light.guipackage.cli.CLI;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.basecomponents.Label;
import light.guipackage.gui.components.boxes.FlexBox;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.uda.guiinterfaces.EncodersGUIInterface;


public class EncodersGUI extends Component implements EncodersGUIInterface {
    
    private SimpleBox mainBox;
    
    public EncodersGUI(UnitRectangle r) {
        super(r);
        
        CLI.debug("AAA");
        mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
        addComponent(mainBox);
        
        mainBox.setColor(new Color(50, 50, 50));
        mainBox.setRounded(true);
        
        //Top bar
        SimpleBox topBar = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 4, Unit.vh));
        addComponent(topBar);
        
        Label title = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 100, Unit.pch), "Encoder Bar", new Font(GUI.baseFont, Font.BOLD, 18), new Color(230, 230, 230));
        title.setColor(new Color(0, 0, 180));
        title.setBorder(1, new Color(10, 100, 255));
        title.setRounded(10);
        title.setTextXCentered(true);
        title.setTextYCentered(true);
        topBar.addComponent(title);
        
        mainBox.addComponent(topBar);
        
        //Feature bars
        FlexBox featureBar = new FlexBox(new UnitPoint(new UnitValue(0, Unit.px), new UnitValue(4.2, Unit.vh)));
        featureBar.setMinWidth(new UnitValue(100, Unit.pcw));
        featureBar.setMaxWidth(new UnitValue(100, Unit.pcw));

        CLI.debug(featureBar.getWidth()+", "+featureBar.getFuncWidth());
        mainBox.addComponent(featureBar);

        for (FeatureGroup feature : FeatureGroup.values()) {
            SimpleBox featureBox = new SimpleBox(new UnitRectangle(1, Unit.px, 0, Unit.px, 99d/FeatureGroup.values().length, Unit.pcw, 5, Unit.vh));
            featureBox.setPosition(Position.Relative);
            featureBox.setColor(new Color(40, 40, 40));
            featureBox.setBorder(new Color(180, 180, 180));
            featureBox.setRounded(true);
            featureBar.addComponent(featureBox);

            Label fLabel = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 100, Unit.pch), feature.toString(), new Font(GUI.baseFont, Font.BOLD, 11), GUI.textDull);
            fLabel.setTextCentered(true);
            featureBox.addComponent(fLabel);

            SimpleBox activeBox = new SimpleBox(new UnitRectangle(85, Unit.pcw, 0, Unit.px, 8, Unit.pcw, 8, Unit.pcw));
            activeBox.setColor(new Color(150, 150, 150));
            featureBox.addComponent(activeBox);
        }

        GUI.getInstance().scanDOM(mainBox, "");
    }
    
    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
