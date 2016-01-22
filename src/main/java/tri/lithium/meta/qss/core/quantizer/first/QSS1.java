package tri.lithium.meta.qss.core.quantizer.first;

import tri.lithium.meta.pdevs.api.Parameter;
import tri.lithium.meta.pdevs.api.State;
import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.qss.core.DoubleInport;
import tri.lithium.meta.qss.core.DoubleOutport;
import tri.lithium.meta.qss.core.Roots;
import tri.lithium.meta.qss.core.Quantizer;

/**
 * First order quantizer.
 */
@Deprecated
public final class QSS1 implements Quantizer {

    @State("Quantized level") double q;
    @State("Internal level") double x;
    @State("Internal derivative") double dx;

    @Parameter protected double dQmin = 0.005;
    @Parameter protected double dQrel = 0.001;

    protected double dQ;

    protected final void setQuantum() {
        dQ = dQrel * Math.abs(x);
        if (dQ < dQmin) dQ = dQmin;
    }

    public void reset(double level) {
        x = level;
        q = level;
    }

    public void initialize(double dQRel, double dQmin) {
        this.dQmin = dQmin;
        this.dQrel = dQRel;
        setQuantum();
    }

    public void generateOutput(DoubleOutport output, double sigma) {
        output.sendPrimitive(x + dx * sigma);
    }

    @Override
    public double onInput(DoubleInport input, double elapsedTime) {
        double dt2, dt1;

        x  += dx * elapsedTime;
        dx  = input.receivePrimitive();

        dt1 = Roots.findSimpleRoot(-dx, q - x - dQ);
        dt2 = Roots.findSimpleRoot(-dx, q - x + dQ);

        return (dt1 < dt2) ? dt1 : dt2;
    }

    @Override
    public double onConflict(DoubleInport input, double sigma) {
        x += dx * sigma;

        dx = input.receivePrimitive();

        /* update quantum */
        q  = x;

        setQuantum();

        return (dx != 0) ? Math.abs(dQ / dx) : Atomic.INFINITY;
    }

    @Override
    public double onTimeout(double sigma) {
        x  = x + dx * sigma;
        q  = x;

        setQuantum();

        return (dx != 0) ? Math.abs(dQ / dx) : Atomic.INFINITY;
    }

    @Override
    public double getCurrentLevel() {
        return x;
    }

    public QSS1() {
        System.err.println("Warning: The usage of yes order algorithms is not recommended!");
    }
}
