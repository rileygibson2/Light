package light.guipackage.threads;

import light.general.ThreadController;
import light.guipackage.gui.IO;

public class GUIThreadController extends ThreadController {

    private boolean iteratePaint; //Whether to paint upon iteration

    public GUIThreadController() {
        super();
        iteratePaint = true;
    }

    public void setPaintOnIterate(boolean p) {iteratePaint = p;}

    @Override
    public void iterate() {
        if (iteratePaint) IO.getInstance().requestPaint();
        super.iterate();
    }

    @Override
    public void finish() {
        super.finish();
        if (iteratePaint) IO.getInstance().requestPaint();
    }
    
}
