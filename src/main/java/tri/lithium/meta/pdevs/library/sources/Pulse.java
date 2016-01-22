/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.sources;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.api.Output;

import java.util.Objects;

/**
 * Pulse generator block.
 */
public class Pulse extends Atomic {

    private double onPeriod = 1;
    private double offPeriod = 1;
    private double value;

    private boolean active;
    private boolean twice;

    private final double offset;

    @Output public Outport<Double> output = new Outport<Double>("output", this);


    @Override
    protected void outputFunction() {
        output.send(active ? value + offset : offset);
    }

    @Override
    protected void deltaExternal(double elapsedTime) {
        /* empty */
    }

    @Override
    protected void deltaInternal() {
        if (active) {
            if (twice) {
                twice  = false;
                active = false;
                timeout(0);
            } else {
                twice = true;
                timeout(onPeriod);
            }
        } else {
            if (twice) {
                twice = false;
                active = true;
                timeout(0);
            } else {
                twice = true;
                timeout(offPeriod);
            }
        }
    }

    public Pulse(String name, boolean active, double value, double offset, double onPeriod, double offPeriod, double start) {
        Objects.requireNonNull(name, "Name cannot be null");
        setName(name);
        this.value   = value;
        this.onPeriod  = onPeriod;
        this.offPeriod = offPeriod;
        this.active  = active;
        this.offset  = offset;
        this.timeout = start;
    }

    public Pulse(String name, boolean active, double value, double offset, double period, double start) {
        this(name, active, value, offset, period, period, start);
    }

    public Pulse(String name, boolean active, double value, double offset, double period) {
        this(name, active, value, offset, period, period, 0);
    }
}
