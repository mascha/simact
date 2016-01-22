/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.transduce;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import tri.lithium.meta.pdevs.core.Atomic;

/**
 * Convert an arrival to a continuous time signal
 * by employing a zero-order-hold over the average
 * number of arrivals per interval.
 */
public class Average extends Atomic {

    private final double duration;

    public final Inport<?> input = new Inport<>("input", this);
    public final Outport<Double> average = new Outport<>("average", this);

    private double avg;
    private int count;
    @Override
    protected void deltaConfluent() {
        count = input.size();
        timeout(duration);
    }

    @Override
    protected void deltaExternal(double elapsedTime) {
        count += input.size();

        double delta = timeout - elapsedTime;
        timeout(delta < 0 ? 0 : delta);
    }

    @Override
    protected void deltaInternal() {
        count = 0;
        avg   = 0;
        timeout(duration);
    }

    @Override
    protected void outputFunction() {
       average.send(count / duration);
    }

    public Average(String name, double timeSpan) {
        this.setName(name);
        this.duration = timeSpan;

        if (timeSpan <= 0)
            throw new NotStrictlyPositiveException(timeSpan);
    }

    public Average(String name) {
        this.setName(name);
        this.duration = 1;
    }
}
