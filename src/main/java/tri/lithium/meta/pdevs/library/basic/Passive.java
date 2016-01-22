/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.basic;

import tri.lithium.meta.pdevs.core.Atomic;

/**
 * Passive blocks do not undergo internal transitions
 * and do not produce outputs.
 *
 * After having received and processed a bag of events,
 * it schedules itself at infinity.
 */
public abstract class Passive extends Atomic {

    @Override protected void outputFunction() {}

    @Override protected void deltaInternal() {
        passivate();
    }

    public Passive() {
        passivate();
    }
}
