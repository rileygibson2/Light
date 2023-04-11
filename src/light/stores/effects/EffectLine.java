package light.stores.effects;

import java.util.HashMap;
import java.util.Map;

import light.fixtures.Attribute;
import light.fixtures.Fixture;
import light.general.ThreadController;

public class EffectLine {
    
    private Attribute attribute;
    private boolean active;
    private Effect parent;
    private Map<Fixture, EffectToken> fixtures;
    
    private double speed; //In bpm
    private double start;
    private double end;
    private int blocks;
    
    private ThreadController effectThread;
    private static final int threadWait = 10;
    
    public EffectLine(Attribute attribute, Effect parent) {
        this.attribute = attribute;
        this.parent = parent;
        this.speed = Effect.defaultSpeed;
        this.blocks = Effect.defaultBlocks;
        effectThread = null;
        fixtures = new HashMap<Fixture, EffectToken>();
        active = true;
    }
    
    public Attribute getAttribute() {return attribute;}
    
    public boolean isActive() {return active;}

    public void setActive(boolean active) {this.active = active;}
    
    public double getSpeed() {return speed;}
    
    public void setSpeed(double speed) {
        if (speed<0) this.speed = 0;
        else if (speed>Effect.maxSpeed) this.speed = Effect.maxSpeed;
        else this.speed = speed;
    }
    
    public double getStart() { return start;}
    
    public void setStart(double start) {
        this.start = start;
    }
    
    public double getEnd() {return end;}
    
    public void setEnd(double end) {
        this.end = end;
    }
    
    public int getBlocks() {return blocks;}
    
    public void setBlocks(int blocks) {
        if (blocks<0) return;
        this.blocks = blocks;
    }
    
    public void addFixture(Fixture f) {
        fixtures.put(f, new EffectToken());
    }
    
    public void start() {
        effectThread = new ThreadController() {
            @Override
            public void run() {
                setWait(threadWait);
                doInitialDelay();
                
                
                //Find percentage to increment each interation - this should increment neatly up to 100
                double percIncr = calculatePercentageIncrement();
                double p = 0;
                while (isRunning()) {
                    if (p>=100) p = 0;
                    else p += percIncr;
                    for (Map.Entry<Fixture, EffectToken> e : fixtures.entrySet()) {
                        double v = getEffectValueAt(e.getValue(), p);
                        //Set output to value
                    }
                }
                finish();
            }
        };
    }
    
    public void stop() {
        if (effectThread==null) return;
        if (!effectThread.hasEnded()) effectThread.end();
        effectThread = null;
    }
    
    public boolean isRunning() {return effectThread!=null&&!effectThread.hasEnded();}
    
    /**
    * Spreads the phase across all fixtures between the low and high defined phases
    * @param low
    * @param high
    */
    public void spreadPhase(int low, int high) {
        double increment = (high-low)/fixtures.size();
        
        int i = 0;
        for (EffectToken t : fixtures.values()) {
            t.setPhase(low+(increment*i));
            i++;
        }
    }
    
    /**
    * Sets phase of all fixtures to the given value
    */
    public void setPhase(int phase) {
        for (EffectToken t : fixtures.values()) t.setPhase(phase);
    }
    
    /**
    * Gets the value for this effect line given a double representing the percentage of the cycle
    * This method respects phase.
    * @param percentage
    * @return
    */
    public double getEffectValueAt(EffectToken token, double perc) {
        /*
        * Calculate amount by which phase offsets the given percentage
        * Adjust the given percentage by this and reduce it back down so it's in
        * the bounds of 0 and 1
        */
        perc = (perc+(token.getPhase()/360))%1; 
        
        //Return the value of the effect at perc point in the cycle
        return perc*effectRange();
    }
    
    /**
    * Figures out the smallest effect value increment possible given the range of the effect
    * and the bpm speed.
    * Returns this as a percentage of the overall values
    * @return
    */
    private double calculatePercentageIncrement() {
        /*
        * Calculate the smallest value increment possible
        * Find number of effect thread ticks that will happen in one cycle
        * Then use this to find the smallest possible value increment
        */
        double smallestValueIncrement = effectRange()/(singleCycleTime()/threadWait);
        
        //Find this as a percentage of the total effect range
        return smallestValueIncrement/effectRange();
    }
    
    /**
    * Returns the time (in ms) it will take to complete a single cycle of this effect
    * @return
    */
    private double singleCycleTime() {return (1000/(speed/60));}
    
    /**
    * Range of values for this effect
    * @return
    */
    private double effectRange() {return end-start;}
}
