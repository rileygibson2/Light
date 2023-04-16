package light.encoders;

import java.util.Map;

import light.encoders.Encoders.Encoder;

/**
 * Encoders have 2 way control.
 * Controller elements can call the update method which will prompt the encoders to query the controller as
 * to what values it's elements should have.
 * But Encoders class also maintains a list of what it's values are based on input coming from the GUI/Encoder wheels to it
 * and then tells the controller when a value changes.
 */
public interface EncoderCapable {

    public String getEncoderTitle(Encoder encoder);

    /**
     * Asks the controller how it would like the value of the provided encoder
     * to be displayed in a textual form. This allows controllers to add reformat the text
     * and add to the text.
     * @param encoder
     * @return
     */
    public String getEncoderValueText(Encoder encoder);

    public String getEncoderCalculatorTitle(Encoder encoder);
    
    public Map<String, Double> getEncoderCalculatorMacros(Encoder encoder);

    /**
     * Called by Encoders when it wants to obtain what the current value for an encoder should be
     * from the controller. If no special actions or checks are required then this method should simply
     * be implemented by grabbing the current value from the encoder and returning that.
     */
    public double checkEncoderValue(Encoder encoder);


    /**
     * Called by Encoders when it wants to check whether an encoder should be activated or not. If no
     * special checks are required by the controller then this method should be implemented by grabbing the
     * current activation state of the encoder from Encoders and returning that.
     * 
     * @param encoder
     * @return
     */
    public boolean checkEncoderActivation(Encoder encoder);

    /**
     * Method called by Encoders class whenever an encoder is updated to notify the current controller
     * @param encoder
     */
    public void encoderUpdated(Encoder encoder);
}
