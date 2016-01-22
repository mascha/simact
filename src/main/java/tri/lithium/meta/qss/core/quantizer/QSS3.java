package tri.lithium.meta.qss.core.quantizer;

import tri.lithium.meta.pdevs.api.State;
import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.qss.core.DoubleInport;
import tri.lithium.meta.qss.core.DoubleOutport;
import tri.lithium.meta.qss.core.Roots;

/**
 * Third order qss quantizer.
 */
public class QSS3 extends QSS2 {

    @State(value = "Third derivative", terse = "x'''") double pdx;

    @State(value = "Quantized no derivative", terse = "q''") double pq;

    public void generateOutput(DoubleOutport output, double t) {
        x   += dx  * t + mdx * t * t + pdx * t * t * t;
        dx  += mdx * t * 2d + pdx * t * t * 3d;
        mdx += pdx * t * 3d;

        output.sendPrimitive(x, dx, mdx);

        /* output.send(
                x   +  dx * sigma + mdx * sigma * sigma + pdx * sigma * sigma * sigma,
                dx  + mdx * sigma * 2d + pdx * sigma * sigma * 3d,
                mdx + pdx * sigma * 3d
        ); */
    }

    @Override
    public double onInput(DoubleInport input, double e) {
        double dt1, dt2, timeout;

        /* Advance state to actual extrapolated position */
        x = x + dx * e + mdx * e * e + pdx * e * e * e;

        /* Update quantized state */
        q  =  q + mq * e + pq * e * e;
        mq = mq + pq * e * 2d;

        /* Update internal values with new ones */
        dx  = input.receivePrimitive();
        mdx = input.receivePrimitive() / 2d;
        pdx = input.receivePrimitive() / 3d;

        if (Math.abs(x - q) > dQ) {
            timeout = 0;
        }  else {
            dt1 = Roots.findCubicRoot(-pdx, pq - mdx, mq - dx, q - x - dQ);
            dt2 = Roots.findCubicRoot(-pdx, pq - mdx, mq - dx, q - x + dQ);
            timeout = (dt1 < dt2) ? dt1 : dt2;
        }

        return timeout;
    }

    public double onTimeout(double sigma) {
        /* Update internal model to the quantum boundary */
        //x   += dx  * sigma + mdx * sigma * sigma + pdx * sigma * sigma * sigma;
        //dx  += mdx * sigma * 2d + pdx * sigma * sigma * 3d;
        //mdx += pdx * sigma * 3d;

        /* Reset quantized state to internal state */
        q  = x; mq = dx; pq = mdx;

        updateQuantum();

        /* determine next crossing event */
        return (pdx != 0) ? Math.cbrt(dQ / Math.abs(pdx)) : Atomic.INFINITY;
    }

    @Override
    public double onConflict(DoubleInport input, double t) {
        /* Update internal model to the quantum boundary */
        // x += dx * t + mdx * t * t;

        /* Use new derivative value for future computation */
        dx  = input.receivePrimitive();
        mdx = input.receivePrimitive() / 2d;
        pdx = input.receivePrimitive() / 3d;

        /* Reset quantized state to internal state */
        q  = x; mq = dx; pq = mdx;

        updateQuantum();

        /* determine next crossing event */
        return (mdx != 0) ? Math.cbrt(dQ / Math.abs(mdx)) : Atomic.INFINITY;
    }

    @Override
    public void reset(double level) {
        x   = q  = level;
        mdx = mq = dx = pdx = pq = 0;
    }
}
