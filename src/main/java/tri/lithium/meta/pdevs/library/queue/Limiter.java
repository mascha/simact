/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.queue;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import tri.lithium.meta.pdevs.core.Atomic;

import java.util.ArrayDeque;
import java.util.Comparator;

/**
 * A limiter acts as a queue releases its entities according
 * to a rate or intensity variable.
 * @param <E>
 */
public class Limiter<E> extends Atomic {

    private final UniformRealDistribution u;
    private boolean active;

    private java.util.Queue<E> queue;

    public final Inport<E> input = new Inport<>("input", this);
    public final Inport<Double> rate = new Inport<>("rate", this);
    public final Outport<E> output = new Outport<>("outport", this);
    public final Outport<Integer> count = new Outport<>("count", this);

    double limitingRate;
    private double maximumRate;

    @Override
    protected void deltaConfluent() {
        deltaExternal(0);
    }

    @Override
    protected void deltaExternal(double elapsedTime) {
        if (rate.hasInputs()) {
            limitingRate = rate.receive();
            limitingRate = limitingRate > maximumRate ? maximumRate : limitingRate;
            limitingRate = limitingRate < 0 ? 0 : limitingRate;
        }

        if (input.hasInputs()) {
            while (input.hasInputs())
                queue.add(input.receive());

            if (!active) {
                active = true;
            }

            timeout(getNextTime());
        }
    }

    @Override
    protected void deltaInternal() {
        active = queue.size() > 0;
        timeout(active ? getNextTime() : INFINITY);
    }

    @Override
    protected void outputFunction() {
        if (active && acceptTime() && queue.size() > 0)
            output.send(queue.poll());

        if (active)
            count.send(queue.size());
    }

    private boolean acceptTime() {
        return u.sample() <= limitingRate / maximumRate;
    }

    private double getNextTime() {
        return Math.log(u.sample()) / - maximumRate;
    }

    @Override
    public String toString() {
        return getName() + " : Limiter ("+ queue.size() +", rate = " + limitingRate +")";
    }


    public Limiter(String name, double initialRate, double maximumRate, Comparator<E> compararator) {
        this.setName(name);
        this.limitingRate = initialRate < maximumRate ? initialRate : maximumRate;
        this.maximumRate  = maximumRate;
        this.queue = compararator != null ? new java.util.PriorityQueue<E>(compararator) : new ArrayDeque<E>(100);
        u = new UniformRealDistribution();
    }
}
