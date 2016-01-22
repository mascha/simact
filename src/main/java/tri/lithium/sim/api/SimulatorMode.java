/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.api;

public enum SimulatorMode {
    /**
     * Run the simulation as fast as possible.
     */
    ANALYTICAL,

    /**
     * Run the simulation synchronized and/or scaled to the wall clock time.
     */
    TIMED,

    /**
     * Run the simulation so that a predefined amount of steps per
     * time window is allowed.
     */
    STEPPED,
}
