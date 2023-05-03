package light.physical;

import java.util.ArrayList;
import java.util.List;

import light.commands.ClearCommand;
import light.commands.Command;
import light.commands.LabelCommand;
import light.commands.MoveCommand;
import light.commands.StoreCommand;

public enum PhysicalKey {
    DOT(PhysicalKeyBank.NUMERIC, "."),
    NUM0(PhysicalKeyBank.NUMERIC, "0"),
    NUM1(PhysicalKeyBank.NUMERIC, "1"),
    NUM2(PhysicalKeyBank.NUMERIC, "2"),
    NUM3(PhysicalKeyBank.NUMERIC, "3"),
    NUM4(PhysicalKeyBank.NUMERIC, "4"),
    NUM5(PhysicalKeyBank.NUMERIC, "5"),
    NUM6(PhysicalKeyBank.NUMERIC, "6"),
    NUM7(PhysicalKeyBank.NUMERIC, "7"),
    NUM8(PhysicalKeyBank.NUMERIC, "8"),
    NUM9(PhysicalKeyBank.NUMERIC, "9"),
    
    STAR(PhysicalKeyBank.OPERATOR, "*"),
    SLASH(PhysicalKeyBank.OPERATOR, "/"),
    MINUS(PhysicalKeyBank.OPERATOR, "-"),
    PLUS(PhysicalKeyBank.OPERATOR, "+"),
    PERC(PhysicalKeyBank.OPERATOR, "%"),
    EQUALS(PhysicalKeyBank.OPERATOR, "="),
    
    BACK(PhysicalKeyBank.FUNCTION, "Back"),
    DELETE(PhysicalKeyBank.FUNCTION, "Delete"),
    GOTO(PhysicalKeyBank.FUNCTION, "Goto"),
    HOME(PhysicalKeyBank.FUNCTION, "Home"),
    END(PhysicalKeyBank.FUNCTION, "End"),
    LEFTARROW(PhysicalKeyBank.FUNCTION, "<-"),
    RIGHTARROW(PhysicalKeyBank.FUNCTION, "->"),
    PLEASE(PhysicalKeyBank.FUNCTION, "Please"),
    IF(PhysicalKeyBank.FUNCTION, "If"),
    THRU(PhysicalKeyBank.FUNCTION, "Thru"),
    SET(PhysicalKeyBank.FUNCTION, "Set"),

    TIME(PhysicalKeyBank.COMMAND, "Time"),
    ESCAPE(PhysicalKeyBank.COMMAND, "Esc"),
    EDIT(PhysicalKeyBank.COMMAND, "Edit"),
    OOPS(PhysicalKeyBank.COMMAND, "Oops"),
    UPDATE(PhysicalKeyBank.COMMAND, "Update"),
    CLEAR(PhysicalKeyBank.COMMAND, "Clear"),
    STORE(PhysicalKeyBank.COMMAND, "Store"),
    COPY(PhysicalKeyBank.COMMAND, "Copy"),
    MOVE(PhysicalKeyBank.COMMAND, "Move"),
    FIX(PhysicalKeyBank.COMMAND, "Fix"),
    SELECT(PhysicalKeyBank.COMMAND, "Select"),
    TEMP(PhysicalKeyBank.COMMAND, "Temp"),
    TOP(PhysicalKeyBank.COMMAND, "Top"),
    ON(PhysicalKeyBank.COMMAND, "On"),
    OFF(PhysicalKeyBank.COMMAND, "Off"),
    LEARN(PhysicalKeyBank.COMMAND, "Learn"),
    SPEEDDOWN(PhysicalKeyBank.COMMAND, "<<<"),
    SPEEDUP(PhysicalKeyBank.COMMAND, ">>>"),
    GO(PhysicalKeyBank.COMMAND, "Go"),
    GOMINUS(PhysicalKeyBank.COMMAND, "Go-"),
    PAUSE(PhysicalKeyBank.COMMAND, "Pause"),
    GOPLUS(PhysicalKeyBank.COMMAND, "Go+"),
    LABEL(PhysicalKeyBank.COMMAND, "Label"),

    VIEW(PhysicalKeyBank.STORE, "View"),
    EFFECT(PhysicalKeyBank.STORE, "Effect"),
    PAGE(PhysicalKeyBank.STORE, "Page"),
    MACRO(PhysicalKeyBank.STORE, "Macro"),
    PRESET(PhysicalKeyBank.STORE, "Preset"),
    SEQUENCE(PhysicalKeyBank.STORE, "Sequ"),
    CUE(PhysicalKeyBank.STORE, "Cue"),
    EXECUTOR(PhysicalKeyBank.STORE, "Exec"),
    FIXTURE(PhysicalKeyBank.STORE, "Fixture"),
    GROUP(PhysicalKeyBank.STORE, "Group"),

    UP(PhysicalKeyBank.POINTERCONTROL, "Up"),
    DOWN(PhysicalKeyBank.POINTERCONTROL, "Down"),
    PREVIOUS(PhysicalKeyBank.POINTERCONTROL, "Prev"),
    NEXT(PhysicalKeyBank.POINTERCONTROL, "Next"),

    AT(PhysicalKeyBank.MODULATE, "At"),
    FULL(PhysicalKeyBank.MODULATE, "Full"),
    HIGHLIGHT(PhysicalKeyBank.MODULATE, "Highlt"),
    SOLO(PhysicalKeyBank.MODULATE, "Solo");

    
    private String text;
    private PhysicalKeyBank bank;
    
    private PhysicalKey(PhysicalKeyBank bank, String text) {
        this.bank = bank;
        this.text = text;
    }
    
    public String getText() {return this.text;}
    public PhysicalKeyBank getBank() {return this.bank;}
    
    public boolean isInBank(PhysicalKeyBank b) {return bank==b;}
    
    public static List<PhysicalKey> getBankList(PhysicalKeyBank bank) {
        if (bank==null) return null;
        List<PhysicalKey> b = new ArrayList<>();

        for (PhysicalKey key : PhysicalKey.values()) {
            if (key.getBank()==bank) b.add(key);
        }
        return b;
    }
    
    public static PhysicalKey getKeyForText(String text) {
        for (PhysicalKey button : PhysicalKey.values()) {
            if (button.toString().equals(text)) return button;
        }
        return null;
    }

    public Class<? extends Command> getCommandClass() {
        if (!isInBank(PhysicalKeyBank.COMMAND)) return null;
        switch (this) {
            case CLEAR: return ClearCommand.class;
            case LABEL: return LabelCommand.class;
            case STORE: return StoreCommand.class;
            case MOVE: return MoveCommand.class;
            default: return null;
        }
    }
}
