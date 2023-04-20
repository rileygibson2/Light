package light.encoders;

import java.util.List;
import java.util.Map;

import light.encoders.Encoders.Encoder;
import light.guipackage.general.Pair;

/**
 * Encoders have 2 way control.
 * Controller elements can call the update method which will prompt the encoders to query the controller as
 * to what values it's elements should have.
 * But Encoders class also maintains a list of what it's values are based on input coming from the GUI/Encoder wheels to it
 * and then tells the controller when a value changes.
 */
public interface EncoderCapable {

    public void encoderChanged(Encoder encoder, double value);

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
    
    /**
     * Gets all the relevant calculator macros for this encoder.
     * A calculator macro consists of a string name for the macro, the value of the macro
     * and potentially a string representing a file name for an image to be displayed with the macro.
     */
    public Map<String, Pair<Double, String>> getEncoderCalculatorMacros(Encoder encoder);

    public List<String> getAllEncoderPageNames();

    public int getNumEncoderPages();

    /**
     * Called by Encoders when it wants to obtain what the current value for an encoder should be
     * from the controller.
     */
    public double getEncoderValue(Encoder encoder);


    /**
     * Called by Encoders when it wants to check whether an encoder should be activated or not.
     * 
     * @param encoder
     * @return
     */
    public boolean getEncoderActivation(Encoder encoder);

}
