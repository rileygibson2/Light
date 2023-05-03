package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import light.Programmer;
import light.fixtures.Attribute;
import light.fixtures.FeatureGroup;
import light.fixtures.Fixture;
import light.fixtures.FixtureType;
import light.fixtures.PatchManager;
import light.fixtures.profile.Profile;
import light.fixtures.profile.ProfileChannel;
import light.fixtures.profile.ProfileChannelFunction;
import light.fixtures.profile.ProfileChannelMacro;
import light.fixtures.profile.ProfileWheelSlot;
import light.general.DataStore;
import light.general.Utils;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.basecomponents.Image;
import light.guipackage.gui.components.basecomponents.Label;
import light.guipackage.gui.components.basecomponents.Table;
import light.guipackage.gui.components.boxes.CollumnBox;
import light.guipackage.gui.components.boxes.FlexBox;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.output.Output;
import light.uda.FixtureWindow;
import light.uda.guiinterfaces.FixtureWindowGUIInterface;


public class FixtureWindowGUI extends SimpleBox implements FixtureWindowGUIInterface {
    
    private FixtureWindow fixtureWindow;
    private Map<Fixture, SimpleBox> fixtureGUIs;
    private Table fixtureTable;
    
    public FixtureWindowGUI(UnitRectangle r, FixtureWindow fixtureWindow) {
        super(r);
        fixtureGUIs = new HashMap<Fixture, SimpleBox>();
        
        this.fixtureWindow = fixtureWindow; 
        setColor(new Color(20, 20, 20));
        setRounded(true);
        setMaxHeight(getHeight());
        setMaxWidth(getWidth());
        setClickAction(() -> {}); //To stop click chain
        setOverflow(Overflow.ScrollBoth);
        
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
        Label titleLabel =  new Label(new UnitRectangle(3, Unit.pcw, 0, Unit.px, 97, Unit.pcw, 100, Unit.pch), "Fixture Window", new Font(Styles.baseFont, Font.BOLD, 18), new Color(230, 230, 230));
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
        
        //Create fixtues
        if (fixtureWindow.getTableMode()) buildFixturesTableView();
        else buildFixturesBlockView();
    }
    
    private void buildFixturesTableView() {
        fixtureTable = new Table(new UnitPoint(0, Unit.pcw, 0, Unit.vh));
        fixtureTable.setPosition(Position.Relative);
        addComponent(fixtureTable);

        //Setup table
        fixtureTable.addCollumn(new UnitValue(5, Unit.vw), Fixture.class, "Fixtures");
        for (Attribute a : PatchManager.getInstance().allAttributeList()) {
            fixtureTable.addCollumn(new UnitValue(5, Unit.vw), a, a.getUserName());
        }
        for (Fixture f : PatchManager.getInstance().allFixtureList()) {
            fixtureTable.addRow(f);
            fixtureTable.setText(Fixture.class, f, f.getAddress().getSuffix()+"");
            populateFixtureBoxTableView(f);
        }
    }

    private void populateFixtureBoxTableView(Fixture f) {
        DataStore outputStore = Output.getInstance().generateOutputStore();
        for (Map.Entry<Attribute, Double> values : outputStore.getFixtureValues(f).entrySet()) {
            fixtureTable.setText(values.getKey(), f, Math.round(values.getValue())+"");
        }

    }

    private void buildFixturesBlockView() {
        CollumnBox wrapper = new CollumnBox(new UnitPoint(3, Unit.pcw, 0, Unit.px));
        wrapper.setPosition(Position.Relative);
        wrapper.setMinWidth(new UnitValue(90, Unit.pcw));
        addComponent(wrapper);
        fixtureGUIs.clear();

        for (Profile profile : PatchManager.getInstance().allProfileSet()) {
            Label profileLabel =  new Label(new UnitRectangle(0, Unit.px, 4, Unit.vh, 100, Unit.pcw, 3, Unit.vh), profile.getName()+" "+profile.getModeName(), new Font(Styles.baseFont, Font.BOLD, 14), new Color(230, 230, 230));
            profileLabel.setBorder(new int[] {2}, new Color(250, 250, 250));
            profileLabel.setTextYCentered(true);
            wrapper.addComponent(profileLabel);
            
            FlexBox fBox = new FlexBox(new UnitPoint(0, Unit.px, 1, Unit.vh));
            fBox.setMaxWidth(new UnitValue(100, Unit.pcw));
            wrapper.addComponent(fBox);
            
            boolean initial = true;
            for (Fixture f : PatchManager.getInstance().getFixtures(profile)) {
                int x = 1;
                if (initial) {
                    x = 0;
                    initial = false;
                }    
                
                //Main box
                SimpleBox box = new SimpleBox(new UnitRectangle(x, Unit.vw, 0, Unit.vh, 5, Unit.vw, 10, Unit.vh));
                box.setPosition(Position.Relative);
                box.setClickAction(() -> {
                    Programmer prog = Programmer.getInstance();
                    if (prog.isSelected(f)) prog.deselect(f);
                    else prog.select(f); 
                });
                fBox.addComponent(box);
                
                fixtureGUIs.put(f, box);
                populateFixtureBoxBlockView(f);
            }
        }
    }
    
    private void populateFixtureBoxBlockView(Fixture f) {
        SimpleBox box = fixtureGUIs.get(f);
        if (box==null) return;
        box.clearComponents();
        DataStore outputStore = Output.getInstance().generateOutputStore();
        
        if (Programmer.getInstance().isSelected(f)) {
            box.setColor(new Color(249, 255, 30));
            box.setBorder(new Color(249, 255, 30));
        }
        else {
            box.setColor(new Color(170, 170, 170));
            box.setBorder(new Color(170, 170, 170));
        }
        
        //ID label
        Label l =  new Label(new UnitRectangle(0, Unit.px, 0, Unit.vh, 100, Unit.pcw, 20, Unit.pch), f.getAddress().getSuffix()+"", new Font(Styles.baseFont, Font.BOLD, 11), new Color(10, 10, 10));
        l.setTextCentered(true);
        box.addComponent(l);
        
        //Bottom black box
        SimpleBox bottom = new SimpleBox(new UnitRectangle(2, Unit.pcw, 20, Unit.pch, 98, Unit.pcw, 80, Unit.pch));
        bottom.setColor(new Color(10, 10, 10));
        box.addComponent(bottom);
        
        //Get relevant data
        Color col = getDisplayColor(f);
        double dim = 0;
        if (f.getProfile().hasAttribute(Attribute.DIM)) dim = outputStore.get(f, Attribute.DIM);
        
        if (f.getProfile().getFixtureType()==FixtureType.SPOT) { //Spot profiles
            SimpleBox circle = new SimpleBox(new UnitRectangle(20, Unit.pcw, 5, Unit.pch, 60, Unit.pcw, 60, Unit.pcw));
            circle.setOval(true);
            circle.setColor(col);
            circle.setOpacity(dim);
            bottom.addComponent(circle);
            
            if (dim==0) {
                circle.setRec(new UnitRectangle(27.5, Unit.pcw, 11, Unit.pch, 45, Unit.pcw, 45, Unit.pcw));
                circle.setColor(new Color(10, 10, 10));
                circle.setBorder(col);
            }
            
            //Gobo image
            String fileName = getGoboFileName(f);
            if (fileName!=null) {
                Image gobo = new Image(new UnitRectangle(20, Unit.pcw, 5, Unit.pch, 60, Unit.pcw, 60, Unit.pcw), null);
                //gobo.setColor(Color.BLUE);
                gobo.setSource(fileName);
                gobo.setMakeImageTransparent(true);
                bottom.addComponent(gobo);
            }
            
            //Intensity label
            l =  new Label(new UnitRectangle(0, Unit.px, 70, Unit.pch,  100, Unit.pcw, 10, Unit.pch), (int) Math.floor(dim)+"%", new Font(Styles.baseFont, Font.BOLD, 10), new Color(0, 0, 0));
            l.setPosition(Position.Relative);
            l.setTextColor(new Color(249, 255, 30));
            l.setTextCentered(true);
            bottom.addComponent(l);
            
            //Position label
            double pan = 0;
            if (f.getProfile().hasAttribute(Attribute.PAN)) pan = outputStore.get(f, Attribute.PAN);
            
            double tilt = 0;
            if (f.getProfile().hasAttribute(Attribute.TILT)) tilt = outputStore.get(f, Attribute.TILT);
            
            l =  new Label(new UnitRectangle(0, Unit.px, 7, Unit.pch,  100, Unit.pcw, 10, Unit.pch), (int) Math.floor(pan)+" "+(int) Math.floor(tilt), new Font(Styles.baseFont, Font.BOLD, 10), new Color(0, 0, 0));
            l.setPosition(Position.Relative);
            l.setTextColor(new Color(249, 255, 30));
            l.setTextCentered(true);
            bottom.addComponent(l);
        }
        else { //Wash profiles
            bottom.setBorder(new int[] {4}, new Color(10, 10, 10));
            
            SimpleBox square = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 72, Unit.pcw));
            square.setPosition(Position.Relative);
            square.setColor(col);
            square.setOpacity(dim);
            bottom.addComponent(square);
            
            l =  new Label(new UnitRectangle(0, Unit.px, 1, Unit.pch,  100, Unit.pcw, 29, Unit.pch), (int) Math.floor(dim)+"%", new Font(Styles.baseFont, Font.BOLD, 12), new Color(0, 0, 0));
            l.setPosition(Position.Relative);
            l.setTextColor(new Color(249, 255, 30));
            l.setTextCentered(true);
            bottom.addComponent(l);  
        }
        
        //Intensity bar
        double iB = 0.95*dim;
        SimpleBox bar = new SimpleBox(new UnitRectangle(3, Unit.pcw, 3, Unit.pch, 8, Unit.pcw, 75, Unit.pch));
        bar.setColor(new Color(10, 10, 10));
        bar.setBorder(1, new Color(180, 180, 180));
        bottom.addComponent(bar);
        
        SimpleBox inner = new SimpleBox(new UnitRectangle(20, Unit.pcw, 98.5-iB, Unit.pch, 62, Unit.pcw, iB, Unit.pch));
        inner.setColor(new Color(255, 200, 7));
        bar.addComponent(inner);
    }
    
    public Color getDisplayColor(Fixture fixture) {
        if (fixture==null) return null;
        double rd = 0, gn = 0, bl = 0;
        
        if (fixture.getProfile().hasAttributeOfFeatureGroup(FeatureGroup.COLOR)) {
            for (Map.Entry<Attribute, Double> value : Output.getInstance().generateOutputStore().getFixtureValuesMatchingGroup(fixture, FeatureGroup.COLOR).entrySet()) {
                //Get display color for attribute
                Color toBlend = null;
                switch (value.getKey()) {
                    case COLORRGB1: toBlend = Styles.displayRed; break;
                    case COLORRGB2: toBlend = Styles.displayGreen; break;
                    case COLORRGB3: toBlend = Styles.displayBlue; break;
                    case COLORRGB4: toBlend = Styles.displayAmber; break;
                    default: break;
                }
                if (toBlend==null) continue;
                
                //Get percentage of display color to blend in and blend
                double perc = fixture.getProfile().valueAsPercOfRange(value.getKey(), value.getValue());
                rd = rd+(perc*toBlend.getRed());
                gn = gn+(perc*toBlend.getGreen());
                bl = bl+(perc*toBlend.getBlue());
            }
        }
        else { //Fixture has no color attributes so white is default color
            rd = 255; gn = 255; bl = 255;
        }
        return new Color(Utils.castToDMX(rd), Utils.castToDMX(gn), Utils.castToDMX(bl));
    }
    
    public String getGoboFileName(Fixture fixture) {
        if (fixture==null||!fixture.getProfile().hasAttribute(Attribute.GOBO1_INDEX)) return null;
        
        double value = Output.getInstance().generateOutputStore().get(fixture, Attribute.GOBO1_INDEX);
        if (value==DataStore.NONE) return null;
        
        //Get slot mapping
        ProfileChannel channel = fixture.getProfile().getChannelWithAttribute(Attribute.GOBO1_INDEX);
        if (channel==null) return null;
        ProfileChannelFunction function = channel.getFunctionForValue(value);
        if (function==null) return null;
        ProfileChannelMacro macro = function.getMacroForValue(value);
        if (macro==null||!macro.hasSlotIndex()) return null;
        
        ProfileWheelSlot slot = macro.getSlotMapping();
        if (slot.hasMediaFileName()) return slot.getMediaFileName();
        
        return null;
    }
    
    @Override
    public void updateFixtures(Collection<Fixture> fixtures) {
        if (fixtures==null) return;
        
        for (Fixture fixture : fixtures) {
            if (fixtureGUIs.get(fixture)==null) continue;
            if (fixtureWindow.getTableMode()) populateFixtureBoxTableView(fixture);
            else populateFixtureBoxBlockView(fixture);
        }
    }
    
    @Override
    public void update() {
        if (fixtureWindow.getTableMode()) buildFixturesTableView();
        else buildFixturesBlockView();
    }
}
