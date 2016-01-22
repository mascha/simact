/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.sources;

import tri.lithium.meta.pdevs.core.Atomic;

public class PeriodicRamp extends Atomic {

    private final int end;
    private final int start;
    private final double increment;
    private final double offset;
    boolean active;
    double  clock;
    double  halftime;
    private int counter;

    public Outport<Double> output = new Outport<Double>("output", this);

    @Override
    protected void deltaExternal(double elapsedTime) {}

    @Override
    protected void deltaInternal() {
        clock   += timeout;

        boolean up = clock % 24 < halftime;

        if (active) if (up) counter++; else if (clock % 24 < end) counter--;

        if (clock % 24 >= end) {
            timeout(24 - end + start);
            clock  %= 24;
            counter = 0;
            active  = true;
        } else {
            active = true;
            timeout(1);
        }

    }

    @Override
    protected void outputFunction() {
        if (active) {
            output.send(clock % 24 < halftime ? increment : -increment);
            output.send(counter * increment + offset);
        } else output.send(offset);
    }

    public PeriodicRamp(String name, int start, int end, double offset) {
        setName(name);
        this.start  = start;
        this.end    = end;
        this.offset = offset;
        halftime    = start + (end - start) / 2;
        increment   = 1.0 / (halftime - start);
        active = true;
        timeout(start);
    }
}
