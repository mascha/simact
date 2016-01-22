/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.sources;

import tri.lithium.meta.pdevs.api.Output;
import tri.lithium.meta.qss.core.DoubleOutport;
import tri.lithium.meta.qss.core.Block;

import java.util.Objects;

/**
 * Saw generator block.
 */
public class Saw extends Block {

    private double period = 1;
    private double value;

    private boolean active;
    private boolean twice;

    private final double offset;

    @Output public DoubleOutport output = DoubleOutport.Double(this);

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
            twice  = false;
            active = false;
            timeout(period);
        } else {
            if (twice) {
                twice = false;
                active = true;
                timeout(0);
            } else {
                twice = true;
                timeout(0);
            }
        }
    }

    public Saw(String name, boolean active, double value, double offset, double period) {
        Objects.requireNonNull(name, "Name cannot be null");
        setName(name);
        this.value   = value;
        this.period  = period;
        this.active  = active;
        this.offset  = offset;
        this.timeout = 0;
    }
}
