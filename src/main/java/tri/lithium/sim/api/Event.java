/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.api;

/**
 * Represents a single schedulable entity with time semantics.
 */
public interface Event {

    /**
     * Return the current timeout value.
     * @return the current timeout value.
     */
    double getTimeout();

    /**
     * Compute the model outputs.
     */
    void produceOutputs();

    /**
     * Process the imminent or confluent atomic model
     * after outputs have been produced.
     * @param currentTime
     *      Current simulator time.
     */
    void processImminent(final double currentTime);

    /**
     * Process the atomic model after it has been influenced
     * by external events.
     *
     * @param currentTime
     *      Current simulator time.
     */
    void processInfluenced(final double currentTime);

    /**
     * Interface for signalling the influenced flag.
     *
     * TODO Fix leaking abstraction. Fix side effects.
     */
    void markAsInfluenced();


    Object getModel();
}
