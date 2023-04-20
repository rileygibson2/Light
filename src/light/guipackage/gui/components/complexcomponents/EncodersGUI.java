package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import light.encoders.Encoders;
import light.encoders.Encoders.Encoder;
import light.encoders.Encoders.EncoderDefaultCalculatorMacros;
import light.fixtures.FeatureGroup;
import light.guipackage.cli.CLI;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.basecomponents.Label;
import light.guipackage.gui.components.basecomponents.TempWindow;
import light.guipackage.gui.components.boxes.FlexBox;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.physical.HardButton;
import light.physical.HardButton.Bank;
import light.uda.guiinterfaces.EncodersGUIInterface;


public class EncodersGUI extends Component implements EncodersGUIInterface {
    
    private SimpleBox mainBox;
    private TempWindow openCalculator;
    
    public EncodersGUI(UnitRectangle r) {
        super(r);
        
        mainBox = new SimpleBox(new UnitRectangle(0, 0, 100, 100));
        addComponent(mainBox);
        
        mainBox.setColor(new Color(50, 50, 50));
        mainBox.setRounded(true);
        
        //Top bar
        SimpleBox topBar = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 3, Unit.vh));
        topBar.setPosition(Position.Relative);
        addComponent(topBar);
        
        Label title = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 100, Unit.pch), "Encoder Bar", new Font(Styles.baseFont, Font.BOLD, 14), new Color(230, 230, 230));
        title.setColor(new Color(0, 0, 180));
        title.setBorder(1, new Color(10, 100, 255));
        title.setRounded(10);
        title.setTextXCentered(true);
        title.setTextYCentered(true);
        topBar.addComponent(title);
        
        mainBox.addComponent(topBar);
        
        //Feature bars
        SimpleBox featureBar = new SimpleBox(new UnitRectangle(0, Unit.px, 1, Unit.px, 100, Unit.pcw, 30, Unit.pch));
        featureBar.setPosition(Position.Relative);
        mainBox.addComponent(featureBar);
        double w = (99d-(1*(FeatureGroup.values().length-1)))/FeatureGroup.values().length;
        
        int i = 0;
        for (FeatureGroup feature : FeatureGroup.values()) {
            int gap = 1;
            if (i==0) gap = 0;
            SimpleBox featureBox = new SimpleBox(new UnitRectangle(gap, Unit.pcw, 0, Unit.px, w, Unit.pcw, 100, Unit.pch));
            featureBox.setPosition(Position.Relative);
            featureBox.setColor(new Color(20, 20, 20));
            featureBox.setBorder(new Color(180, 180, 180));
            featureBox.setRounded(true);
            featureBar.addComponent(featureBox);
            
            Label fLabel = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 100, Unit.pch), feature.toString(), new Font(Styles.baseFont, Font.BOLD, 11), Styles.textDull);
            fLabel.setTextCentered(true);
            featureBox.addComponent(fLabel);
            
            SimpleBox activeBox = new SimpleBox(new UnitRectangle(85, Unit.pcw, 0, Unit.px, 8, Unit.pcw, 8, Unit.pcw));
            activeBox.setColor(new Color(150, 150, 150));
            featureBox.addComponent(activeBox);
            i++;
        }
        
        //Encoder bars
        SimpleBox encoderBar = new SimpleBox(new UnitRectangle(0, Unit.px, 2, Unit.px, 100, Unit.pcw, 52, Unit.pch));
        encoderBar.setPosition(Position.Relative);
        mainBox.addComponent(encoderBar);
        
        i = 0;
        w = 100/Encoder.values().length;
        for (Encoder e : Encoder.values()) {
            SimpleBox encoder = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, w, Unit.pcw, 100, Unit.pch));
            encoder.setPosition(Position.Relative);
            encoder.setColor(new Color(80, 80, 80));
            encoder.setRounded(true);
            encoder.setBorder(new Color(180, 180, 180));
            encoder.setClickAction(() -> openCalculator(e));
            encoderBar.addComponent(encoder);
            
            Label top = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 50, Unit.pch), "", new Font(Styles.baseFont, Font.BOLD, 14), new Color(230, 230, 230));
            top.setText(Encoders.getInstance().getEncoderTitle(e));
            top.setTextCentered(true);
            encoder.addComponent(top);
            
            Label bottom = new Label(new UnitRectangle(0, Unit.px, 50, Unit.pch, 100, Unit.pcw, 50, Unit.pch), "", new Font(Styles.baseFont, Font.BOLD, 14), new Color(230, 230, 230));
            bottom.setColor(new Color(45, 45, 45));
            bottom.setRounded(new int[] {2, 3});
            bottom.setText(Encoders.getInstance().getEncoderValueText(e));
            bottom.setTextCentered(true);
            encoder.addComponent(bottom);
            i++;
        } 
        
        //openCalculator();
    }
    
    private void openCalculator(Encoder encoder) {
        if (openCalculator!=null) return;
        
        //Get relevant structure
        
        TempWindow tB = new TempWindow("Value for "+Encoders.getInstance().getEncoderCalculatorTitle(encoder));
        tB.addSmother(80);
        tB.getContentBox().setMaxWidth(new UnitValue(80, Unit.vw));
        addComponent(tB);
        
        //Input bar
        Label input = new Label(new UnitRectangle(0, Unit.px, 2, Unit.px, 100, Unit.pcw, 5, Unit.vh), " input", new Font(Styles.baseFont, Font.BOLD, 14), new Color(230, 230, 230));
        //input.setPosition(Position.Relative);
        input.setColor(new Color(0, 60, 0));
        input.setRounded(true);
        input.setBorder(new Color(255, 200, 0));
        input.setTextYCentered(true);
        tB.addContent(input);
        
        //Button banks
        Bank[] banks = new Bank[] {Bank.Number, Bank.Operator, Bank.Function};
        
        for (Bank bank : banks) {
            int x = 1;
            double y = 0;
            double mw = 10.5;
            if (bank==Bank.Number) {
                x = 0;
                y = 5.5;
            }
            else mw = 7;
            
            FlexBox bankBox = new FlexBox(new UnitPoint(x, Unit.vw, y, Unit.vh));
            bankBox.setPosition(Position.Relative);
            bankBox.setMaxWidth(new UnitValue(mw, Unit.vw));
            tB.addContent(bankBox);
            
            for (HardButton button : HardButton.getBankList(bank)) {
                double w = 3.5;
                if (button==HardButton.PLEASE) w = 7;
                
                Label b = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, w, Unit.vw, 3.5, Unit.vw), button.getText(), new Font(Styles.baseFont, Font.BOLD, 12), new Color(230, 230, 230));
                b.setPosition(Position.Relative);
                b.setColor(new Color(20, 20, 20));
                b.setRounded(true);
                b.setBorder(new Color(255, 200, 0));
                b.setTextCentered(true);
                bankBox.addComponent(b);
            }
        }
        
        //Macros box
        FlexBox macroBox = new FlexBox(new UnitPoint(1, Unit.vw, 0, Unit.vh));
        macroBox.setPosition(Position.Relative);
        macroBox.setMaxWidth(new UnitValue(25, Unit.vw));
        tB.addContent(macroBox);
        
        //Default macros
        for (EncoderDefaultCalculatorMacros macro : EncoderDefaultCalculatorMacros.values()) {
            Label b = new Label(new UnitRectangle(1, Unit.px, 0, Unit.px, 6, Unit.vw, 5, Unit.vh), macro.toString(), new Font(Styles.baseFont, Font.BOLD, 12), new Color(230, 230, 230));
            b.setPosition(Position.Relative);
            b.setColor(new Color(41, 0, 0));
            b.setRounded(true);
            b.setBorder(new Color(255, 200, 0));
            b.setTextCentered(true);
            b.setClickAction(() -> {
                CLI.debug("Macro clicked: "+macro);
            });
            macroBox.addComponent(b);
        }

        //Encoder controller defined macros
        for (Map.Entry<String, Double> macro : Encoders.getInstance().getEncoderCalculatorMacros(encoder).entrySet()) {
            Label b = new Label(new UnitRectangle(1, Unit.px, 0, Unit.px, 6, Unit.vw, 5, Unit.vh), macro.getKey(), new Font(Styles.baseFont, Font.BOLD, 12), new Color(230, 230, 230));
            b.setPosition(Position.Relative);
            b.setColor(new Color(20, 20, 20));
            b.setRounded(true);
            b.setBorder(new Color(255, 200, 0));
            b.setTextCentered(true);
            b.setClickAction(() -> {
                CLI.debug("Macro clicked: "+macro.getKey()+", "+macro.getValue());
            });
            macroBox.addComponent(b);
        }
        
        openCalculator = tB;
    }
    
    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
