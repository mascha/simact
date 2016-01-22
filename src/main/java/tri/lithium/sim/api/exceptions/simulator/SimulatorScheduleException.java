/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.api.exceptions.simulator;

import tri.lithium.meta.pdevs.core.Atomic;

/**
 * Simulator scheduling exception.
 *
 * Throw when the scheduled time lies in the past and therefore
 * represents a causality error.
 *
 * Throw when the scheduled event is a null event. While theoretically
 * possible, this behaviour is not allowed.
 */
public class SimulatorScheduleException extends RuntimeException {

    public static final String NULL_EVENT = "Cannot schedule an event of type null";
    public static final String NEGATIVE_TIME = "Cannot schedule an event with negative time advance";

    public SimulatorScheduleException(Atomic atomic, String message, double advance) {
        super(atomic.getFullName() + " -> " + message + "("+ advance + ")");
    }

}
