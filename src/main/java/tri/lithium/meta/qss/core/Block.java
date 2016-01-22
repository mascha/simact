package tri.lithium.meta.qss.core;

import tri.lithium.meta.pdevs.library.basic.Compute;

import java.util.ArrayList;
import java.util.List;


/**
 */
public abstract class Block extends Compute {

    private DoubleOutport qssOutport;

    private List<DoubleInport> inports = new ArrayList<DoubleInport>(2);

    public void setOutport(DoubleOutport qssOutport) {
        this.qssOutport = qssOutport;
    }

    public DoubleOutport getOutport() {
        return qssOutport;
    }

    public List<DoubleInport> getPrimitivePorts() {
        return inports;
    }

    @Override
    public void cleanseInports() {
        super.cleanseInports();
        for (int i = 0; i < inports.size(); i++) {
            inports.get(i).clear();
        }
    }
}
