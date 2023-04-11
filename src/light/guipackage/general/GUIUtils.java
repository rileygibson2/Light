package light.guipackage.general;

import java.awt.Color;

public class GUIUtils {
	
	public static void setCursor(int c) {
		//GUI.frame.setCursor(Cursor.getPredefinedCursor(c));
	}

	public static Color modulateColor(Color col, double percentage) {
        int r = (int) (col.getRed()*percentage);
        if (r<0) r = 0; if (r>255) r = 255;
        int g = (int) (col.getGreen()*percentage);
        if (g<0) g = 0; if (g>255) g = 255;
        int b = (int) (col.getBlue()*percentage);
        if (b<0) b = 0; if (b>255) b = 255;
        return new Color(r, g, b);
    }
}
