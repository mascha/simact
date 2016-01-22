/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.meta.pdevs.api.hints;

/**
 * Indicates the handling method of
 * multiple events on a single port.
 */
public enum InputHandling {
    /**
     * Process only one event, then discard the rest.
     * Warn if more than one input remains in the queue.
     */
    SINGLE,

    /**
     * Process all events. Make sure that all events on
     * this port are processed, otherwise a {@link tri.lithium.sim.api.exceptions.model.ModelBreachException}
     * will be thrown.
     */
    ALL,

    /**
     * Unprocessed events remain in the input port,
     * effectively queueing them up until processed.
     */
    IMPLICIT_BUFFER
}
