package light.guipackage.gui.components;

import light.general.Getter;
import light.general.Submitter;
import light.guipackage.general.UnitRectangle;
import light.guipackage.gui.components.primitives.boxes.SimpleBox;

public abstract class InputComponent<T> extends SimpleBox {
    
    public enum InputType {
        CheckBox,
        TextBox,
        DropDown,
        Slider
    }
    
    private T value;

    private Getter<T> getAction;
    private Submitter<T> submitAction;
    
    public InputComponent(UnitRectangle r) {
        super(r);
    }

    public void setValue(T v) {this.value = v;} 

    public T getValue() {return this.value;}
    
    public void setActions(Getter<T> getAction, Submitter<T> submitAction) {
        this.getAction = getAction;
        this.submitAction = submitAction;
        actionsUpdated();
    }
    
    public boolean hasActions() {return this.getAction!=null&&this.submitAction!=null;}

    public Getter<T> getAction() {return getAction;}

    public Submitter<T> submitAction() {return submitAction;}
    
    /**
    * Update hook for when actions are added to allow inputs to get initial state
    */
    public void actionsUpdated() {
        this.value = getAction.get();
    }
}
