package guipackage.gui.components;

import guipackage.general.GetterSubmitter;
import guipackage.general.UnitRectangle;

public class InputComponent<T> extends Component {
    
    public enum InputType {
        CheckBox,
        TextBox,
        DropDown,
        Slider
    }
    
    private GetterSubmitter<T, T> actions;
    
    public InputComponent(UnitRectangle r) {
        super(r);
    }
    
    public void setActions(GetterSubmitter<T, T> actions) {
        this.actions = actions;
        actionsUpdated();
    }
    
    public boolean hasActions() {return this.actions!=null;}
    
    /**
    * Update hook for when actions are added to allow inputs to get initial state
    */
    public void actionsUpdated() {}
    
    public GetterSubmitter<T, T> getActions() {return this.actions;}
}
