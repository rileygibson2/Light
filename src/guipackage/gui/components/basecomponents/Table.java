package guipackage.gui.components.basecomponents;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Text;

import guipackage.general.Tag;
import guipackage.general.UnitPoint;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.gui.GUI;
import guipackage.gui.components.Component;
import guipackage.gui.components.InputComponent;
import guipackage.gui.components.boxes.CollumnBox;
import guipackage.gui.components.boxes.FlexBox;
import guipackage.gui.components.boxes.SimpleBox;

public class Table extends FlexBox {

    private List<TableCollumn> collumns;

    public Table(UnitPoint pos) {
        super(pos);
        collumns = new ArrayList<TableCollumn>();
    }

    public void addCollumn(Class<?> type, String title, UnitValue width) {
        addCollumn(type, null, title, width);
    }

    public void addCollumn(Class<?> type, Tag tag, String title, UnitValue width) {
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

        collumns.add(new TableCollumn(c, type, tag));
    }

    public void addRow() {
        for (TableCollumn t : collumns) {
            Component c = null;
           
            //Create element
            if (t.type==Label.class) {
                SimpleBox b = new SimpleBox(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 5, Unit.vh), GUI.focus2);
                b.setBorder(GUI.focus);
                Label l = new Label(new UnitRectangle(10, 10, 80, 80, Unit.pcw, Unit.pch), "hello", new Font("Geneva", Font.PLAIN, 10), GUI.textMain);
                l.setTextCentered(true);
                l.fitFont();
                b.addComponent(l);
                c = b;
            }
            if (t.type==CheckBoxInput.class) {
                CheckBoxInput cB = new CheckBoxInput(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 5, Unit.vh));
                cB.setBorder(GUI.focus);
                c = cB;
            }
            if (t.type==TextInput.class) {
                TextInput tB = new TextInput(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 5, Unit.vh));
                tB.setBorder(GUI.focus);
                c = tB;
            }

            //Add element to collumn
            t.cBox.addComponent(c);
        }
    }

    public Map<Tag, Object> getValuesForRow(int i) {
        if (collumns.isEmpty()) return null;
        if (i==0||i>collumns.get(0).cBox.getNumComponents()-1) return null;

        Map<Tag, Object> values = new HashMap<>();
        for (int z=0; z<collumns.size(); z++) {
            Component c = collumns.get(z).cBox.getNthComponent(i-1);
            if (!(c instanceof InputComponent)) continue;
            values.put(collumns.get(z).tag, ((InputComponent) c).getValue());
        }
        return values;
    }

    private class TableCollumn {
        CollumnBox cBox;
        Class<?> type;
        Tag tag;

        public TableCollumn(CollumnBox cBox, Class<?> type, Tag tag) {
            this.cBox = cBox;
            this.type = type;
            this.tag = tag;
        }
    }
}
