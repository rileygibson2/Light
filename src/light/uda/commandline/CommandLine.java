package light.uda.commandline;

import java.awt.event.KeyEvent;
import java.util.ArrayDeque;

import guipackage.cli.CLI;
import guipackage.general.Submitter;
import guipackage.gui.GUI;
import guipackage.gui.IO;
import light.uda.guiinterfaces.CommandLineInterface;

public class CommandLine {
    
    CommandLineInterface gui;
    ArrayDeque<CommandProxy> command;

    public enum Operator {
        Plus,
        Minus,
        At,
        Thru,
        If;
    }

    public CommandLine() {
        gui = (CommandLineInterface) GUI.getInstance().addToGUI(this);
        command = new ArrayDeque<CommandProxy>();
        
        IO.getInstance().registerKeyListener(this, new Submitter<KeyEvent>() {
            @Override
            public void submit(KeyEvent s) {keyPressed(s);}
        });
    }

    private void keyPressed(KeyEvent e) {
        CLI.debug("AA "+e.getExtendedKeyCode());
    }
}
