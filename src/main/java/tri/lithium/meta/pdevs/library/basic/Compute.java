/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.basic;

import tri.lithium.meta.pdevs.core.Atomic;

/**
 * Compute class.
 *
 * Compute is a single atomic model to be used as a template class
 * for stateless functions.
 *
 * Stateless in the following sense: it is inactive until inputs arrive,
 * then schedules itself for an internal transition, computes the output value (lambda)
 * and waits for new inputs.
 *
 */
public abstract class Compute extends Atomic {

    /**
     * Execute when current time equals the timeout value.
     */
    protected void deltaInternal() {
        passivate();
    }

    /**
     * Make inactive at the start */
    public Compute() {
        passivate();
    }
}
