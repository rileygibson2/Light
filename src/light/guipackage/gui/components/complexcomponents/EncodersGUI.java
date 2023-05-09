package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import light.commands.commandcontrol.CommandController;
import light.commands.commandcontrol.CommandFormatException;
import light.commands.commandcontrol.commandproxys.OperatorTypeProxy;
import light.commands.commandcontrol.commandproxys.OperatorTypeProxy.Operator;
import light.commands.commandcontrol.commandproxys.ValueTypeProxy;
import light.encoders.Encoders;
import light.encoders.Encoders.Encoder;
import light.encoders.Encoders.EncoderDefaultCalculatorMacros;
import light.fixtures.FeatureGroup;
import light.general.Utils;
import light.guipackage.cli.CLI;
import light.guipackage.general.Pair;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.GUI;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.primitives.Image;
import light.guipackage.gui.components.primitives.Label;
import light.guipackage.gui.components.primitives.TempWindow;
import light.guipackage.gui.components.primitives.boxes.FlexBox;
import light.guipackage.gui.components.primitives.boxes.SimpleBox;
import light.physical.PhysicalKey;
import light.physical.PhysicalKeyBank;
import light.uda.guiinterfaces.EncodersGUIInterface;


public class EncodersGUI extends Component implements EncodersGUIInterface {
    
    private SimpleBox mainBox;
    private SimpleBox pageBar;
    private SimpleBox encoderBar;
    
    //Calculator stuff
    private TempWindow openCalculator;
    private CommandController calculatorCommand;
    
    public EncodersGUI(UnitRectangle r) {
        super(r);
        setClickAction(() -> CLI.debug("clicckcckedddd"));

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
        
        buildPages();
        buildEncoders();
    }
    
    private void buildPages() {
        pageBar = new SimpleBox(new UnitRectangle(0, Unit.px, 1, Unit.px, 100, Unit.pcw, 30, Unit.pch));
        pageBar.setPosition(Position.Relative);
        mainBox.addComponent(pageBar);
        double w = (99d-(1*(FeatureGroup.values().length-1)))/FeatureGroup.values().length;
        
        int i = 0;
        for (String pageTitle : Encoders.getInstance().getAllPageNames()) {
            int gap = 1;
            if (i==0) gap = 0;
            boolean active = Encoders.getInstance().getCurrentPage()==i;
            
            Label pageBox = new Label(new UnitRectangle(gap, Unit.pcw, 0, Unit.px, w, Unit.pcw, 100, Unit.pch), Utils.capitaliseFirst(pageTitle), new Font(Styles.baseFont, Font.BOLD, 11), Styles.textDull);
            pageBox.setPosition(Position.Relative);
            if (active) {
                pageBox.setColor(new Color(40, 40, 40));
                pageBox.setTextColor(Styles.textMain);
            }
            else {
                pageBox.setColor(new Color(20, 20, 20));
            }
            pageBox.setTextCentered(true);
            pageBox.setBorder(new Color(180, 180, 180));
            pageBox.setRounded(true);
            pageBox.setTag(i);
            pageBox.setClickAction(() -> Encoders.getInstance().setPage((int) pageBox.getTag(), this));
            pageBar.addComponent(pageBox);
            
            SimpleBox activeBox = new SimpleBox(new UnitRectangle(85, Unit.pcw, 0, Unit.px, 8, Unit.pcw, 8, Unit.pcw));
            if (active) activeBox.setColor(new Color(255, 0, 0));
            else activeBox.setColor(new Color(150, 150, 150));
            pageBox.addComponent(activeBox);
            i++;
        }
    }
    
    private void buildEncoders() {
        //Encoder bars
        encoderBar = new SimpleBox(new UnitRectangle(0, Unit.px, 2, Unit.px, 100, Unit.pcw, 52, Unit.pch));
        encoderBar.setPosition(Position.Relative);
        mainBox.addComponent(encoderBar);
        
        int i = 0;
        int w = 100/Encoder.values().length;
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
    }
    
    private void openCalculator(Encoder encoder) {
        if (openCalculator!=null||!Encoders.getInstance().getEncoderActivation(encoder)) return;
        
        TempWindow tB = new TempWindow("Value for "+Encoders.getInstance().getEncoderCalculatorTitle(encoder));
        tB.addSmother(80);
        tB.getContentBox().setMaxWidth(new UnitValue(80, Unit.vw));
        tB.setCloseAction(() -> closeCalulator());
        GUI.getInstance().getCurrentRoot().addComponent(tB);
        
        //Input bar
        Label input = new Label(new UnitRectangle(0, Unit.px, 2, Unit.px, 100, Unit.pcw, 5, Unit.vh), "", new Font(Styles.baseFont, Font.BOLD, 14), new Color(230, 230, 230));
        //input.setPosition(Position.Relative);
        input.setColor(new Color(0, 60, 0));
        input.setRounded(true);
        input.setBorder(new Color(255, 200, 0));
        input.setTextYCentered(true);
        tB.addContent(input);
        
        //Command
        calculatorCommand = new CommandController();
        calculatorCommand.setCommandUpdatedAction(() -> {input.setText(calculatorCommand.getCommandAsString());});
        
        //Button banks
        PhysicalKeyBank[] banks = new PhysicalKeyBank[] {PhysicalKeyBank.NUMERIC, PhysicalKeyBank.OPERATOR, PhysicalKeyBank.FUNCTION};
        
        for (PhysicalKeyBank bank : banks) {
            int x = 1;
            double y = 0;
            double mw = 10.5;
            if (bank==PhysicalKeyBank.NUMERIC) {
                x = 0;
                y = 5.5;
            }
            else mw = 7;
            
            FlexBox bankBox = new FlexBox(new UnitPoint(x, Unit.vw, y, Unit.vh));
            bankBox.setPosition(Position.Relative);
            bankBox.setMaxWidth(new UnitValue(mw, Unit.vw));
            tB.addContent(bankBox);
            
            for (PhysicalKey button : PhysicalKey.getBankList(bank)) {
                double w = 3.5;
                if (button==PhysicalKey.PLEASE) w = 7;
                
                Label b = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, w, Unit.vw, 3.5, Unit.vw), button.getText(), new Font(Styles.baseFont, Font.BOLD, 12), new Color(230, 230, 230));
                b.setPosition(Position.Relative);
                b.setColor(new Color(20, 20, 20));
                b.setRounded(true);
                b.setBorder(new Color(255, 200, 0));
                b.setTextCentered(true);
                b.setClickAction(() -> {
                    switch (button) {
                        case NUM0: calculatorCommand.updateWorkingText("0"); break;
                        case NUM1: calculatorCommand.updateWorkingText("1"); break;
                        case NUM2: calculatorCommand.updateWorkingText("2"); break;
                        case NUM3: calculatorCommand.updateWorkingText("3"); break;
                        case NUM4: calculatorCommand.updateWorkingText("4"); break;
                        case NUM5: calculatorCommand.updateWorkingText("5"); break;
                        case NUM6: calculatorCommand.updateWorkingText("6"); break;
                        case NUM7: calculatorCommand.updateWorkingText("7"); break;
                        case NUM8: calculatorCommand.updateWorkingText("8"); break;
                        case NUM9: calculatorCommand.updateWorkingText("9"); break;
                        case PLUS: 
                        calculatorCommand.parseWorkingText();
                        calculatorCommand.addToCommand(new OperatorTypeProxy(Operator.PLUS));
                        break;
                        case PLEASE:
                        calculatorCommand.parseWorkingText();
                        Double d = null;
                        try {d = calculatorCommand.resolveForDouble();}
                        catch (CommandFormatException e) {
                            CLI.error("calculator command did not resolve - "+e.getMessage());
                            calculatorCommand.clear();
                        }
                        CLI.debug("command result double: "+d);
                        
                        //TODO: error handle by changing text red or wiping text/wiping text and displaying error message in command bar until next entered input
                        if (d!=null) {
                            Encoders.getInstance().set(encoder, d);
                            openCalculator.close(false);
                        }
                        break;
                        default: break;   
                    }
                });
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
        for (Map.Entry<String, Pair<Double, String>> macro : Encoders.getInstance().getEncoderCalculatorMacros(encoder).entrySet()) {
            Label b = new Label(new UnitRectangle(1, Unit.px, 0, Unit.px, 6, Unit.vw, 5, Unit.vh), macro.getKey(), new Font(Styles.baseFont, Font.BOLD, 12), new Color(230, 230, 230));
            b.setPosition(Position.Relative);
            b.setColor(new Color(20, 20, 20));
            b.setRounded(true);
            b.setBorder(new Color(255, 200, 0));
            b.setTextCentered(true);
            b.setClickAction(() -> calculatorCommand.addToCommand(new ValueTypeProxy(macro.getValue().a)));
            macroBox.addComponent(b);
            
            if (macro.getValue().b!=null) {
                Image img = new Image(new UnitRectangle(2, Unit.pcw, 5, Unit.pch, 60, Unit.pcw, 90, Unit.pch), macro.getValue().b);
                b.addComponent(img);
            }
        }
        
        openCalculator = tB;
    }
    
    public void closeCalulator() {
        openCalculator = null;
        calculatorCommand = null;
    }
    
    @Override
    public void update() {}
    
    @Override
    public void updatePages() {
        mainBox.removeComponent(pageBar);
        buildPages();
    }
    
    @Override
    public void updateEncoders() {
        mainBox.removeComponent(encoderBar);
        buildEncoders();
    }
}
