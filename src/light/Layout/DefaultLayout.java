package light.Layout;


import light.zones.UDA;
import light.zones.commandline.CommandLine;

public class DefaultLayout extends Layout {
    
    public DefaultLayout() {
        addZone(new CommandLine());
        addZone(new UDA());
    }

}
