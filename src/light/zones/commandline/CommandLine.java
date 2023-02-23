package light.zones.commandline;

import java.util.ArrayDeque;

import guipackage.cli.CLI;
import guipackage.general.Submitter;
import guipackage.gui.GUI;
import guipackage.gui.IO;
import light.zones.Zone;
import java.awt.event.KeyEvent;

public class CommandLine extends Zone {
    
    ArrayDeque<CommandProxy> command;

    public CommandLine() {
        GUI.getInstance().addZoneToView(this);
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
