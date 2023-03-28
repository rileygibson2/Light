package light.uda.guiinterfaces;

import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;

public interface UDAGUIInterface extends GUIInterface {

    Point getSize();

    void openZonePicker(Rectangle zoneRec);
}
