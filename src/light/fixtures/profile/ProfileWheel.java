package light.fixtures.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileWheel extends ProfileElement {
    
    private List<ProfileWheelSlot> slots;

    public ProfileWheel() {
        super();
        slots = new ArrayList<ProfileWheelSlot>();
    }

    public int getNumSlots() {return slots.size();}

    public void addSlot(ProfileWheelSlot slot) {
        slots.add(slot);
        slot.setWheel(this);
        slot.setProfile(profile);
        Collections.sort(slots);
    }

    public List<ProfileWheelSlot> getSlots() {
        Collections.sort(slots);
        return slots;
    }

    public boolean hasSlot(int slotIndex) {
        for (ProfileWheelSlot slot : slots) if (slot.getIndex()==slotIndex) return true;
        return false;
    }

    public ProfileWheelSlot getSlot(int slotIndex) {
        for (ProfileWheelSlot slot : slots) if (slot.getIndex()==slotIndex) return slot;
        return null;
    }

    public boolean validate() {
        if (profile==null||index==-1) return false;
        for (ProfileWheelSlot slot : slots) if (!slot.validate()) return false;
        return true;
    }

    public String toProfileString(String indent) {
        String result = "[Wheel: index="+index+"]";
        indent += "\t";

        for (ProfileWheelSlot slot : slots) result += "\n"+indent+slot.toProfileString(indent);
        return result;
    }
}
