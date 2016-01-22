package tri.lithium.meta.qss.core.quantizer;


import tri.lithium.meta.pdevs.api.Parameter;
import tri.lithium.meta.pdevs.api.State;
import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.qss.core.DoubleInport;
import tri.lithium.meta.qss.core.DoubleOutport;
import tri.lithium.meta.qss.core.Quantizer;
import tri.lithium.meta.qss.core.Roots;

/**
 * Second order qss quantizer.
 */
public class QSS2 implements Quantizer {

    @State("Internal level")    double x;
    @State("First derivative")  double dx;
    @State("Second derivative") double mdx;

    @State("Quantized level")       double q;
    @State("Quantized derivative")  double mq;

    @Parameter protected double dQ;
    @Parameter protected double dQmin;
    @Parameter protected double dQrel;

    protected final void updateQuantum() {
        dQ = dQrel * Math.abs(x) ;
        if (dQ < dQmin) dQ = dQmin;
    }

    @Override
    public void reset(double level) {
        x   = q  = level;
        mdx = mq = dx = 0;
    }

    @Override
    public void initialize(double dQRel, double dQmin) {
        this.dQrel = dQRel;
        this.dQmin = dQmin;
        updateQuantum();
    }

    @Override
    public void generateOutput(DoubleOutport output, double t) {
        x  += dx  * t + mdx * t * t;
        dx += mdx * t * 2d;

        output.sendPrimitive(x, dx);

        /*
        output.send(
                x + dx * t + mdx * t * t,
                dx + mdx * t * 2d
        ); */
    }

    @Override
    public double onInput(DoubleInport input, double e) {
        double dt, t;

        /* Advance state to actual extrapolated position */
        x += dx * e + mdx * e * e;

        /* Update quantized state */
        q += mq * e;

        /* Update internal values with new ones */
        dx  = input.receivePrimitive();
        mdx = input.receivePrimitive() / 2d;

        /* Compute the next crossing time */
        if (Math.abs(x - q) > dQ) {
            t = 0;
        } else {
            t  = Roots.findQuadraticRoot(- mdx, mq - dx, q - x - dQ);
            dt = Roots.findQuadraticRoot(- mdx, mq - dx, q - x + dQ);
            if (dt < t) t = dt;
        }

        return t;
    }

    @Override
    public double onTimeout(double t) {
        /* Update internal model to the quantum boundary */
        // x  += dx  * t + mdx * t * t;
        // dx += mdx * t * 2d;

        /* Reset quantized state to internal state */
        q  = x; mq = dx;

        updateQuantum();

        /* determine next crossing event */
        return (mdx != 0) ? Math.sqrt(dQ / Math.abs(mdx)) : Atomic.INFINITY;
    }

    @Override
    public double onConflict(DoubleInport input, double t) {
        /* Update internal model to the quantum boundary */
        // x += dx * t + mdx * t * t;

        /* Use new derivative value for future computation */
        dx  = input.receivePrimitive();
        mdx = input.receivePrimitive() / 2d;

        /* Reset quantized state to internal state */
        q  = x; mq = dx;

        updateQuantum();

        /* determine next crossing event */
        return (mdx != 0) ? Math.sqrt(dQ / Math.abs(mdx)) : Atomic.INFINITY;
    }


    @Override
    public double getCurrentLevel() {
        return x;
    }

}
