package light.uda.guiinterfaces;

import guipackage.general.Point;
import guipackage.general.Rectangle;

public interface UDAInterface extends GUIInterface {

    Point getSize();

    void openZonePicker(Rectangle zoneRec);
}
