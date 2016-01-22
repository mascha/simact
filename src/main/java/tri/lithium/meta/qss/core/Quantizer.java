package tri.lithium.meta.qss.core;

/**
 * Quantizer interface.
 */
public interface Quantizer {
    /**
     * Reset the quantizer to a new state.
     *
     * @param level
     *      The initial level to reset the quantizer to.
     */
    void reset(double level);

    /**
     * Initialize the quantizer with new precision parameters.
     *
     * @param dQRel
     *      The relative error. Zero indicates fixed quantization.
     * @param dQmin
     *      The maximum precision to be used when the function
     *      evolves near zero. If the relative error is zero, it's value
     *      will be used as quantum size.
     */
    void initialize(double dQRel, double dQmin);

    /**
     * Make the quantizer send it's state to the output port.
     *
     * @param output
     *      The output port to use.
     * @param sigma
     *      The current time of the model.
     */
    void generateOutput(DoubleOutport output, double sigma);

    /**
     * Called if timeout and external inputs occur at the same time.
     *
     * @param input
     *      The input port.
     * @param sigma
     *      The time since last event.
     * @return the new timeout value.
     */
    double onConflict(DoubleInport input, double sigma);

    /**
     * Called if external input arrive.
     *
     * @param input
     *      The input port.
     * @param elapsedTime
     *      The time since last event.
     * @return the new timeout value.
     */
    double onInput(DoubleInport input, double elapsedTime);

    /**
     * Called if timeout occurs.
     * @param sigma
     *      The time since last event.
     * @return the new timeout value.
     */
    double onTimeout(double sigma);

    double getCurrentLevel();
}
