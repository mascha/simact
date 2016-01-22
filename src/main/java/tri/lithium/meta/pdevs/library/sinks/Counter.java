/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.sinks;

import tri.lithium.meta.pdevs.library.basic.Compute;

/**
 * Counter class.
 */
public class Counter extends Compute {

    public final Inport input = new Inport<>("input", this);
    public final Outport<Double> count = new Outport<>("count", this);

    private int entitiesLeft;

    public Counter(String name) {
        this.setName(name);
    }

    @Override
    protected void deltaExternal(double elapsedTime) {
        if (elapsedTime == 0) {
            passivate();
            return;
        }
        entitiesLeft += input.size();
        activate();
    }

    @Override
    public String toString() {
        return getName() + " : Counter(" + entitiesLeft + ")";
    }

    @Override
    public void exitSimulation() {
        System.out.println(getFullName() + " counted " + entitiesLeft + " objects");
    }

    @Override
    protected void outputFunction() {
        count.send((double) entitiesLeft);
    }
}
