package light.uda;

import guipackage.general.Rectangle;

public class UDAZone<Z> {
    
    Z z;
    public Rectangle cells;

    public UDAZone(Z z, Rectangle cells) {
        this.z = z;
        this.cells = cells;
    }

    public Z getZone() {return z;}
}
