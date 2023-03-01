package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import guipackage.general.Pair;
import guipackage.general.UnitPoint;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.boxes.CollumnBox;
import guipackage.gui.components.boxes.FlexBox;
import guipackage.gui.components.boxes.SimpleBox;

public class Table extends FlexBox {

    private List<Pair<Class<?>, CollumnBox>> collumns;

    public Table(UnitPoint pos) {
        super(pos);
        collumns = new ArrayList<Pair<Class<?>, CollumnBox>>();
    }

    public void addCollumn(Class<?> type, String title, UnitValue width) {
        CollumnBox c = new CollumnBox();
        c.setPosition(Position.Relative);
        c.setMinWidth(width);
        addComponent(c);

        //Add title label
        SimpleBox b = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 5, Unit.vh), GUI.fg);
        b.setBorder(new Color(100, 100, 100));
        c.addComponent(b);
        Label l = new Label(new UnitRectangle(10, 10, 80, 80, Unit.pcw, Unit.pch), title, new Font("Geneva", Font.PLAIN, 10), GUI.textMain);
        l.setTextCentered(true);
        l.fitFont();
        b.addComponent(l);

        collumns.add(new Pair<Class<?>, CollumnBox>(type, c));
    }

    public void addRow() {
        for (Pair<Class<?>, CollumnBox> p : collumns) {
            Component c = null;
           
            //Create element
            if (p.a==Label.class) {
                SimpleBox b = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 5, Unit.vh), GUI.focus2);
                b.setBorder(GUI.focus);
                Label l = new Label(new UnitRectangle(10, 10, 80, 80, Unit.pcw, Unit.pch), "hello", new Font("Geneva", Font.PLAIN, 10), GUI.textMain);
                l.setTextCentered(true);
                l.fitFont();
                b.addComponent(l);
                c = b;
            }

            //Add element to collumn
            p.b.addComponent(c);
        }
    }
}
