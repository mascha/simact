package tri.lithium.meta.qss.core;

import tri.lithium.meta.pdevs.api.Input;
import tri.lithium.meta.pdevs.api.Output;
import tri.lithium.meta.pdevs.api.Parameter;
import tri.lithium.meta.qss.core.quantizer.QSS2;
import tri.lithium.meta.qss.core.quantizer.QSS3;
import tri.lithium.meta.qss.core.quantizer.first.QSS1;

/**
 * Integrator block.
 *
 * Holds port and state information and delegates input and
 * timeout events to the quantization algorithm.
 */
public class Integrator extends Block {

    private static final double DEFAULT_RELATIVE_PRECISION = 10e-3;
    private static final double DEFAULT_MAXIMUM_PRECISION  = 10e-3;

    @Output("State")
    public DoubleOutport state;

    @Input("Derivative")
    public DoubleInport input;

    @Input("Reset")
    public DoubleInport reset;

    protected final Quantizer quantizer;

    protected final void outputFunction() {
        quantizer.generateOutput(state, timeout);
    }

    protected final void deltaExternal(double elapsedTime) {
        if (reset.hasInputs()) quantizer.reset(reset.receive());

        timeout(quantizer.onInput(input, elapsedTime));
    }

    protected final void deltaInternal() {
        timeout(quantizer.onTimeout(timeout));
    }

    protected final void deltaConfluent() {
        timeout(quantizer.onConflict(input, timeout));
    }

    public Integrator(@Parameter int order, @Parameter double initLevel, @Parameter double relativeError, @Parameter double minimumPrecision) {

        state  = DoubleOutport.Double("state", this);
        input  = DoubleInport.Double("input", this);
        reset  = DoubleInport.Double("reset", this);


        switch (order) {
            case 1  : quantizer = new QSS1(); break;
            case 2  : quantizer = new QSS2(); break;
            case 3  : quantizer = new QSS3(); break;
            default : throw new RuntimeException("Invalid order of integration specified (was "+ order+")");
        }

        if (relativeError >= 0 && minimumPrecision > 0) {
            quantizer.reset(initLevel);
            quantizer.initialize(relativeError, minimumPrecision);
        } else {
            throw new RuntimeException(
                    getName() + ": Invalid precision parameters ("
                            + (relativeError < 0 ? "Relative error < 0!, " : "")
                            + (minimumPrecision <= 0 ? "Absolute error <= 0!, " : "") +")");
        }

        activate();
    }

    public String toString() {
        return getName() + " = Integrator(order = " + quantizer.getClass().getSimpleName() + ", level = " + quantizer.getCurrentLevel() + ")";
    }

    public Integrator(String name, int order, double initLevel, double relativeError, double minimumPrecision) {
        this(order, initLevel, relativeError, minimumPrecision);
        setName(name);
    }

    public Integrator(String name, int order, double initLevel) {
        this(name, order, initLevel, DEFAULT_RELATIVE_PRECISION, DEFAULT_MAXIMUM_PRECISION);
    }

    public Integrator(String name, int order) {
        this(name, order, 0, DEFAULT_RELATIVE_PRECISION, DEFAULT_MAXIMUM_PRECISION);
    }

    public Integrator(String name) {
        this(name, 2, 0, DEFAULT_RELATIVE_PRECISION, DEFAULT_MAXIMUM_PRECISION);
    }

    public Integrator initLevel(double level) {
        quantizer.reset(level);
        return this;
    }
}
