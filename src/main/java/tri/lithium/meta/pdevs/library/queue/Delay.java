/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.queue;

import org.apache.commons.math3.distribution.RealDistribution;
import tri.lithium.meta.pdevs.api.Parameter;
import tri.lithium.meta.pdevs.core.Atomic;

import java.util.*;

/**
 * The delay model accumulates and stores it's inputs
 * for a specified time and the releases them.
 */
public class Delay<E> extends Atomic {

    private List<Entry<E>>  ready = new ArrayList<Entry<E>>();

    private PriorityQueue<Entry<E>> queue;

    public final Outport<E> output = new Outport<E>("delayOutput", this);
    public final Inport<E> input   = new Inport<E>("delayInput", this);

    @Parameter("delayTime")
    private DelayTime<E> delayTime;

    private double currentTime;

    public PriorityQueue<Entry<E>> getDelayContents() {
        return queue;
    }

    @Override
    protected final void deltaExternal(double elapsedTime) {
        currentTime += elapsedTime;

        while (input.hasInputs()) {
            E element = input.receive();
            queue.add(new Entry<E>(element, currentTime, delayTime.calculate(element)));
        }

        if (queue.isEmpty())
            passivate();
        else {
            timeout = queue.peek().leavingTime() - currentTime;
            if (timeout < 0) timeout = 0;
        }


    }

    @Override
    protected final void deltaInternal() {
        currentTime += timeout;

        if (queue.isEmpty())
            passivate();
        else
            timeout(queue.peek().leavingTime() - currentTime);
    }

    @Override
    protected final void outputFunction() {
        double time = currentTime + timeout;
        while (!queue.isEmpty() && queue.peek().leavingTime() <= time) {
            output.send(queue.poll().element);
        }
    }

    @Override
    public String toString() {
        if (queue.size() < 1)
            return getName() + " : Delay (empty)";
        else if (queue.size() < 3)
            return getName() + " : Delay ("+ queue +")";
        else
            return getName() + " : Delay ("+ queue.size() +" entities )";
    }

    /**
     *
     * @param name
     * @param delayTime
     */
    public Delay(String name, double delayTime) {
        this.setName(name);
        this.delayTime = new DelayTime<E>() {
            public double calculate(E e) {
                return delayTime;
            }
        };

        queue = new PriorityQueue<Entry<E>>(new Comparator<Entry<E>>() {
            @Override
            public int compare(Entry<E> x, Entry<E> y) {
                double a = x.leavingTime();
                double b = y.leavingTime();
                return a < b ? -1 : a > b ? 1 : 0;
            }
        });

        passivate();
    }

    /**
     *
     * @param name
     * @param delayTime
     */
    public Delay(String name, DelayTime<E> delayTime) {
        this.setName(name);
        this.delayTime = delayTime;

        queue = new PriorityQueue<Entry<E>>(new Comparator<Entry<E>>() {
            @Override
            public int compare(Entry<E> x, Entry<E> y) {
                double a = x.leavingTime();
                double b = y.leavingTime();
                return a < b ? -1 : a > b ? 1 : 0;
            }
        });

        passivate();
    }

    /**
     *
     * @param name
     */
    public Delay(String name, RealDistribution distribution) {
        this(name, new DelayTime<E>() {
            @Override
            public double calculate(E e) {
                return distribution.sample();
            }
        });
    }

    private final static class Entry<E> {
        private final double arrived;
        private final E element;
        double stayingTime;

        @Override
        public String toString() {
            return "DelayEntry ("+ element.getClass().getSimpleName() + ", arrived = "+ arrived +", leaving = "+ leavingTime() + ")";
        }

        public Entry(E element, double arrivedAt, double stayingFor) {
            Objects.requireNonNull(element);
            this.stayingTime = stayingFor;
            this.element     = element;
            this.arrived     = arrivedAt;
        }

        public double leavingTime() {
            return arrived + stayingTime;
        }
    }

    public interface DelayTime<E> {
        double calculate(E e);
    }
}
