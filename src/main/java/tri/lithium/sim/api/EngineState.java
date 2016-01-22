/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.api;

/**
 * The last state of the engine.
 */
public enum EngineState {

    /**
     * The model is not initialized.
     */
    UNINITIALIZED,

    /**
     * The model is set up and can be simulated.
     */
    READY,

    /**
     * The simulation is currently running.
     */
    RUNNING,

    /**
     * The simulation is paused.
     */
    PAUSED,

    /**
     * The simulation has stopped.
     */
    STOPPED,

    /**
     * The simulation has stopped due to an
     * unrecoverable error.
     */
    ERROR
}
