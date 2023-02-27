package guipackage.gui;

import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue.Unit;

public class RootElement extends Element {
    
    public RootElement() {
        super(new UnitRectangle(0, 0, 100, 100, Unit.pcw, Unit.pch));
        setRoot();
        setPosition(Position.Fixed);
    }
}
