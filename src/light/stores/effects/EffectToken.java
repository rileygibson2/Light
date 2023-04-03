package light.stores.effects;

public class EffectToken {
    
    private double phase;
    private double width;

    public EffectToken() {
        phase = Effect.defaultPhase;
        width = Effect.defaultWidth;
    }

    public double getPhase() {return phase;}

    public void setPhase(double phase) {
        this.phase = phase;
    }

    public double getWidth() {return width;}

    public void setWidth(double width) {
        this.width = width;
    }
}
