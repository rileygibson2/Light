package guipackage.gui.components;

import guipackage.general.GetterSubmitter;
import guipackage.general.UnitRectangle;
import guipackage.gui.components.boxes.SimpleBox;

public class InputComponent<T> extends SimpleBox {
    
    public enum InputType {
        CheckBox,
        TextBox,
        DropDown,
        Slider
    }
    
    private T value;

    private GetterSubmitter<T, T> actions;
    
    public InputComponent(UnitRectangle r) {
        super(r);
    }

    public void setValue(T v) {this.value = v;} 

    public T getValue() {return this.value;}
    
    public void setActions(GetterSubmitter<T, T> actions) {
        this.actions = actions;
        actionsUpdated();
    }
    
    public boolean hasActions() {return this.actions!=null;}
    
    /**
    * Update hook for when actions are added to allow inputs to get initial state
    */
    public void actionsUpdated() {
        this.value = getActions().get();
    }
    
    public GetterSubmitter<T, T> getActions() {return this.actions;}
}
