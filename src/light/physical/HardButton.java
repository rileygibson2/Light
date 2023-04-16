package light.physical;

import java.util.ArrayList;
import java.util.List;

public enum HardButton {
    DOT(Bank.Number, "."),
    NUM0(Bank.Number, "0"),
    NUM1(Bank.Number, "1"),
    NUM2(Bank.Number, "2"),
    NUM3(Bank.Number, "3"),
    NUM4(Bank.Number, "4"),
    NUM5(Bank.Number, "5"),
    NUM6(Bank.Number, "6"),
    NUM7(Bank.Number, "7"),
    NUM8(Bank.Number, "8"),
    NUM9(Bank.Number, "9"),
    
    STAR(Bank.Operator, "*"),
    SLASH(Bank.Operator, "/"),
    MINUS(Bank.Operator, "-"),
    PLUS(Bank.Operator, "+"),
    PERC(Bank.Operator, "%"),
    EQUALS(Bank.Operator, "="),
    CLEAR(Bank.Operator, "Clear"),
    
    BACK(Bank.Function, "Back"),
    DELETE(Bank.Function, "Delete"),
    HOME(Bank.Function, "Home"),
    END(Bank.Function, "End"),
    LEFTARROW(Bank.Function, "<-"),
    RIGHTARROW(Bank.Function, "->"),
    PLEASE(Bank.Function, "Please");
    
    public enum Bank {
        Number,
        Operator,
        Function
    };
    
    private String text;
    private Bank bank;
    
    private HardButton(Bank bank, String text) {
        this.bank = bank;
        this.text = text;
    }
    
    public String getText() {return this.text;}
    public Bank getBank() {return this.bank;}
    
    public boolean isBank(Bank b) {return bank==b;}
    
    public static List<HardButton> getBankList(Bank bank) {
        if (bank==null) return null;
        List<HardButton> b = new ArrayList<>();
        
        switch(bank) {
            case Function:
            b.add(HardButton.BACK);
            b.add(HardButton.DELETE);
            b.add(HardButton.HOME);
            b.add(HardButton.END);
            b.add(HardButton.LEFTARROW);
            b.add(HardButton.RIGHTARROW);
            b.add(HardButton.PLEASE);
            break;

            case Number:
            b.add(HardButton.NUM7);
            b.add(HardButton.NUM8);
            b.add(HardButton.NUM9);
            b.add(HardButton.NUM4);
            b.add(HardButton.NUM5);
            b.add(HardButton.NUM6);
            b.add(HardButton.NUM1);
            b.add(HardButton.NUM2);
            b.add(HardButton.NUM3);
            b.add(HardButton.NUM0);
            b.add(HardButton.DOT);
            break;

            case Operator:
            b.add(HardButton.STAR);
            b.add(HardButton.SLASH);
            b.add(HardButton.MINUS);
            b.add(HardButton.CLEAR);
            b.add(HardButton.PLUS);
            b.add(HardButton.PERC);
            b.add(HardButton.EQUALS);
            break;
        }
        return b;
    }
    
    public static HardButton getButton(String text) {
        for (HardButton button : HardButton.values()) {
            if (button.toString().equals(text)) return button;
        }
        return null;
    }
}
