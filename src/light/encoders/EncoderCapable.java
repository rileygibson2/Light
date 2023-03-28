package light.encoders;

import light.encoders.Encoders.Encoder;

public interface EncoderCapable {

    public String getEncoderTitle(Encoder encoder);

    public double getInitialEncoderValue(Encoder encoder);

    /**
     * Method called by Encoders class whenever an encoder is updated to notify the current controller
     * @param encoder
     */
    public void encoderUpdated(Encoder encoder);
}
