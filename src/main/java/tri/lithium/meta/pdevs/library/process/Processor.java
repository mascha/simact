/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.process;

import org.apache.commons.math3.distribution.RealDistribution;
import tri.lithium.meta.pdevs.api.State;
import tri.lithium.meta.pdevs.library.basic.Compute;

import java.util.ArrayDeque;
import java.util.Objects;

/**
 * Simple processor unit.
 */
public class Processor<E> extends Compute {

    private final DurationProvider<E> timeProvider;

    public final Inport<E> input = new Inport<E>("input", this);
    public final Outport<E> processed = new Outport<E>("input", this);

    private E currentJob;

    @State
    private boolean busy;

    @Override
    protected void deltaInternal() {
        passivate();
        busy = false;
    }

    @Override
    protected void deltaConfluent() {
        deltaInternal();
        deltaExternal(0);
    }

    @Override
    protected void deltaExternal(double elapsedTime) {
        if (!busy) {
            currentJob = input.receive();
            timeout    = timeProvider.calculateProcessingTime(currentJob);

            if (input.size() > 0)
                System.err.println(getFullName() + " : Warning: Received multiple jobs on input port - Only yes can be processed");
        } else
            System.err.println(getFullName() + " : Warning: Received item while already busy - Dropped " + input.receive());
    }

    @Override
    protected void outputFunction() {
        if (busy)
            processed.send(currentJob);
    }

    @Override
    public String toString() {
        return getName() + " : Processor( job = " + currentJob + ")";
    }

    /**
     * Create a processor with a deterministic processing time.
     * @param name
     * @param processingTime
     */
    public Processor(String name, double processingTime) {
        this(name, new DurationProvider<E>() {
            public double calculateProcessingTime(E job) {
                return processingTime;
            }
        });
    }

    /**
     * Create a processor with a probabilistic processing time.
     * @param name
     * @param distribution
     */
    public Processor(String name, RealDistribution distribution) {
        this(name, new DurationProvider<E>() {
            public double calculateProcessingTime(E job) {
                return distribution.sample();
            }
        });
    }

    /**
     * Create a processor with a custom processing time.
     * @param name
     * @param provider
     */
    public Processor(String name, DurationProvider<E> provider) {
        Objects.requireNonNull(provider, "Processor needs a function that determines the processing time of the job");
        this.setName(name);
        this.timeProvider = provider;
    }

    public final class Buffered<E> extends Processor<E> {

        private ArrayDeque<E> queue = new ArrayDeque<E>();

        public Buffered(String name, RealDistribution distribution) {
            super(name, distribution);
        }

        public Buffered(String name, double processingTime) {
            super(name, processingTime);
        }

        public Buffered(String name, DurationProvider<E> provider) {
            super(name, provider);
        }
    }

    public interface DurationProvider<E> {
        double calculateProcessingTime(E job);
    }
}
