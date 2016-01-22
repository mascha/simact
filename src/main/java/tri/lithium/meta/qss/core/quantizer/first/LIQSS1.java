package tri.lithium.meta.qss.core.quantizer.first;

import tri.lithium.meta.qss.core.DoubleInport;
import tri.lithium.meta.qss.core.DoubleOutport;
import tri.lithium.meta.qss.core.Quantizer;

@Deprecated
public final class LIQSS1 implements Quantizer {
    @Override
    public void reset(double level) {

    }

    @Override
    public void initialize(double dQRel, double dQmin) {

    }

    @Override
    public void generateOutput(DoubleOutport output, double sigma) {

    }

    @Override
    public double onConflict(DoubleInport input, double sigma) {
        return 0;
    }

    @Override
    public double onInput(DoubleInport input, double elapsedTime) {
        return 0;
    }

    @Override
    public double onTimeout(double sigma) {
        return 0;
    }

    @Override
    public double getCurrentLevel() {
        return 0;
    }
}
