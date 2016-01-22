/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.sources;

import tri.lithium.meta.pdevs.api.Output;
import tri.lithium.meta.qss.core.DoubleOutport;
import tri.lithium.meta.qss.core.Block;

/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
public class Step extends Block {

    private final double stepValue;
    private boolean twice;
    private boolean init;
    private double from = 0;
    private int count;


    public Step(String name, double stepValue, double time) {
        this.stepValue = stepValue;
        this.setName(name);
        timeout(time);
    }

    @Output public DoubleOutport output = DoubleOutport.Double(this);

    @Override
    protected void deltaExternal(double elapsedTime) {
        /* */
    }

    @Override
    protected void deltaInternal() {
        if (twice)
            passivate();
        else {
            activate();
            twice = true;
        }

    }

    @Override
    protected void outputFunction() {
        output.send(twice ? stepValue * ++count : from);
    }
}
