/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.api.exceptions.simulator;

import tri.lithium.meta.pdevs.core.Atomic;

/**
 * Thrown if the simulator detects behaviour not allowed by the
 * Discrete Event Specification.
 */
public final class SimulatorLogicBreachException extends RuntimeException {

    public static final String INFLUENCED_STILL_IMMINENT = "Influenced model was still imminent after all imminents were processed";
    public static final String ILLEGAL_STATE_ACCESS    = "Atomic models can only modify their state in transition functions";

    public SimulatorLogicBreachException(Atomic model, String message) {
        super(model.getFullName() + " -> " + message);
    }
}
