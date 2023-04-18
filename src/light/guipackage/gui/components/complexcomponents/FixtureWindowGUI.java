package light.guipackage.gui.components.complexcomponents;

import java.awt.Color;
import java.awt.Font;

import light.Programmer;
import light.fixtures.Attribute;
import light.fixtures.Fixture;
import light.fixtures.FixtureType;
import light.fixtures.PatchManager;
import light.fixtures.profile.Profile;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.GUI;
import light.guipackage.gui.components.basecomponents.Image;
import light.guipackage.gui.components.basecomponents.Label;
import light.guipackage.gui.components.boxes.CollumnBox;
import light.guipackage.gui.components.boxes.FlexBox;
import light.guipackage.gui.components.boxes.SimpleBox;
import light.uda.FixtureWindow;
import light.uda.guiinterfaces.FixtureWindowGUIInterface;


public class FixtureWindowGUI extends SimpleBox implements FixtureWindowGUIInterface {
    
    private FixtureWindow fixtureWindow;
    
    public FixtureWindowGUI(UnitRectangle r, FixtureWindow fixtureWindow) {
        super(r);
        this.fixtureWindow = fixtureWindow; 
        setColor(new Color(20, 20, 20));
        setRounded(true);
        
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
        Label titleLabel =  new Label(new UnitRectangle(3, Unit.pcw, 0, Unit.px, 97, Unit.pcw, 100, Unit.pch), "Fixture Window", new Font(GUI.baseFont, Font.BOLD, 18), new Color(230, 230, 230));
        titleLabel.setTextYCentered(true);
        title.addComponent(titleLabel);
        
        Image exit = new Image(new UnitRectangle(0, Unit.vw, 0, Unit.vh, 5, Unit.vw, 100, Unit.pch), "exit.png");
        exit.setPosition(Position.Relative);
        exit.setFloat(Float.Right);
        exit.setColor(GUI.bg);
        exit.setBorder(1, new Color(80, 80, 80));
        exit.setRounded(10);
        topBar.addComponent(exit);
        
        addComponent(topBar);
        
        //Create fixtues
        CollumnBox fixtureBox = new CollumnBox(new UnitPoint(3, Unit.pcw, 0, Unit.px));
        fixtureBox.setPosition(Position.Relative);
        fixtureBox.setMinWidth(new UnitValue(90, Unit.pcw));
        addComponent(fixtureBox);
        
        for (Profile profile : PatchManager.getInstance().allProfileSet()) {
            Label profileLabel =  new Label(new UnitRectangle(0, Unit.px, 2, Unit.vh, 100, Unit.pcw, 3, Unit.vh), profile.getName()+" "+profile.getModeName(), new Font(GUI.baseFont, Font.BOLD, 14), new Color(230, 230, 230));
            profileLabel.setBorder(new int[] {2}, new Color(250, 250, 250));
            profileLabel.setTextYCentered(true);
            fixtureBox.addComponent(profileLabel);
            
            FlexBox fBox = new FlexBox(new UnitPoint(0, Unit.px, 1, Unit.vh));
            fixtureBox.addComponent(fBox);
            
            boolean initial = true;
            for (Fixture f : PatchManager.getInstance().getFixtures(profile)) {
                int x = 1;
                if (initial) {
                    x = 0;
                    initial = false;
                }    
                
                //Main box
                SimpleBox box = new SimpleBox(new UnitRectangle(x, Unit.vw, 0, Unit.px, 5, Unit.vw, 10, Unit.vh));
                box.setPosition(Position.Relative);
                box.setColor(new Color(170, 170, 170));
                box.setBorder(new Color(170, 170, 170));
                fBox.addComponent(box);
                
                //ID label
                Label l =  new Label(new UnitRectangle(0, Unit.px, 0, Unit.vh, 100, Unit.pcw, 20, Unit.pch), f.getAddress().getSuffix()+"", new Font(GUI.baseFont, Font.BOLD, 11), new Color(10, 10, 10));
                l.setTextCentered(true);
                box.addComponent(l);
                
                //Bottom black box
                SimpleBox bottom = new SimpleBox(new UnitRectangle(2, Unit.pcw, 20, Unit.pch, 98, Unit.pcw, 80, Unit.pch));
                bottom.setColor(new Color(10, 10, 10));
                box.addComponent(bottom);
                
                //Get relevant data
                Programmer prog = Programmer.getInstance();
                
                Color col = Color.RED;
                
                double dim = 0;
                    if (f.getProfile().hasAttribute(Attribute.DIM)) {
                        if (prog.contains(f, Attribute.DIM)) dim = prog.get(f, Attribute.DIM);
                        else dim = f.getProfile().getChannelWithAttribute(Attribute.DIM).getMinValue();
                    }
                
                if (profile.getFixtureType()==FixtureType.SPOT) { //Spot profiles
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
                    
                    //Intensity label
                    l =  new Label(new UnitRectangle(0, Unit.px, 70, Unit.pch,  100, Unit.pcw, 10, Unit.pch), (int) Math.floor(dim)+"%", new Font(GUI.baseFont, Font.BOLD, 10), new Color(0, 0, 0));
                    l.setPosition(Position.Relative);
                    l.setTextColor(new Color(249, 255, 30));
                    l.setTextCentered(true);
                    bottom.addComponent(l);
                    
                    //Position label
                    double pan = 0;
                    if (f.getProfile().hasAttribute(Attribute.PAN)) {
                        if (prog.contains(f, Attribute.PAN)) pan = prog.get(f, Attribute.PAN);
                        else pan = f.getProfile().getChannelWithAttribute(Attribute.PAN).getMinValue();
                    }
                    
                    double tilt = 0;
                    if (f.getProfile().hasAttribute(Attribute.TILT)) {
                        if (prog.contains(f, Attribute.TILT)) tilt = prog.get(f, Attribute.TILT);
                        else tilt = f.getProfile().getChannelWithAttribute(Attribute.TILT).getMinValue();
                    }

                    l =  new Label(new UnitRectangle(0, Unit.px, 7, Unit.pch,  100, Unit.pcw, 10, Unit.pch), (int) Math.floor(pan)+" "+(int) Math.floor(tilt), new Font(GUI.baseFont, Font.BOLD, 10), new Color(0, 0, 0));
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
                    
                    l =  new Label(new UnitRectangle(0, Unit.px, 1, Unit.pch,  100, Unit.pcw, 29, Unit.pch), (int) Math.floor(dim)+"%", new Font(GUI.baseFont, Font.BOLD, 12), new Color(0, 0, 0));
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
        }
    }
    
    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
