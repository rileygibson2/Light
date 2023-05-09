package light.guipackage.gui.components.primitives;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import light.guipackage.general.Point;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.Styles;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.primitives.boxes.CollumnBox;
import light.guipackage.gui.components.primitives.boxes.FlexBox;

public class Table extends FlexBox {
    
    private List<CollumnBox> collumns;
    
    public Table(UnitPoint pos) {
        super(pos);
        collumns = new ArrayList<CollumnBox>();
    }
    
    public void addCollumn(UnitValue width, String title) {
        addCollumn(width, null, title);
    }
    
    public void addCollumn(UnitValue width, Object tag, String title) {
        CollumnBox c = new CollumnBox();
        c.setPosition(Position.Relative);
        c.setMinWidth(width);
        c.setTag(tag);
        addComponent(c);
        
        //Add title label
        Label b = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 5, Unit.vh), title, new Font("Geneva", Font.PLAIN, 12), Styles.textMain);
        b.setColor(new Color(90, 90, 90));
        b.setBorder(new Color(80, 80, 80));
        b.setTextCentered(true);
        c.addComponent(b);
        collumns.add(c);
    }
    
    public void addRow(Object tag) {
        for (CollumnBox collumn : collumns) {
            //Add content label
            Label b = new Label(new UnitRectangle(0, Unit.px, 0, Unit.px, 100, Unit.pcw, 5, Unit.vh), "", new Font("Geneva", Font.PLAIN, 12), Styles.textMain);
            b.setColor(new Color(50, 50, 50));
            b.setBorder(new Color(80, 80, 80));
            b.setTextCentered(true);
            b.setTag(tag);
            collumn.addComponent(b);
        }
    }

    public void setText(Point p, String text) {
        if (collumns.isEmpty()||p.x<0||p.x>collumns.size()||p.y<0||p.y>=collumns.get((int) (p.x)).getNumComponents()) return; 
        Label box = (Label) collumns.get((int) p.x).getNthComponent((int) p.y);
        box.setText(text);
    }

    public void setText(Object collumnTag, Object rowTag, String text) {
        for (CollumnBox collumn : collumns) {
            if (collumn.getTag()==null||!collumn.getTag().equals(collumnTag)) continue;
            for (Component box : collumn.getComponents()) {
                if (box.getTag()!=null&&box.getTag().equals(rowTag)&&box instanceof Label) {
                    ((Label) box).setText(text);
                }
            }
        }
    }
    
    /*public Map<Object, Object> getValuesForRow(int i) {
        if (collumns.isEmpty()) return null;
        if (i==0||i>collumns.get(0).cBox.getNumComponents()-1) return null;
        
        Map<Object, Object> values = new HashMap<>();
        for (int z=0; z<collumns.size(); z++) {
            Component c = collumns.get(z).cBox.getNthComponent(i-1);
            if (!(c instanceof InputComponent)) continue;
            values.put(collumns.get(z).tag, ((InputComponent) c).getValue());
        }
        return values;
    }*/
}
