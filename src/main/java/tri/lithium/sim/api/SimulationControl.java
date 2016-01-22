/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.api;

import tri.lithium.sim.core.experiment.Configuration;

/**
 * Simulation control interface.
 *
 * Allows control of the simulation engine
 * and it's simulators.
 */
public interface SimulationControl {
    /**
     * Registers a state update listener on the engine.
     * @param listener
     *      The listener to attach.
     */
    void registerStateListener(SimulationStateListener listener);

    /**
     * Retrieves the last saved state of the engine.
     * Current state may be different.
     * @return the last saved state.
     */
    EngineState getEngineState();

    /**
     * Add a experimental configuration to the execution set.
     * @param configuration
     *      The configuration to add.
     */
    public void addConfiguration(Configuration configuration);

    /**
     * Signal the engine to start its execution given
     * the current configuration.
     *
     * Has no effect if the engine is running and will
     * result in the engine resuming it's execution if
     * it is paused.
     */
    public void start();

    /**
     * Signal the engine to stop and reset it's execution
     * as soon as possible.
     *
     * Has no effect if the engine is already reset.
     */
    public void stop();

    /**
     * Signal the engine to pause it's execution.
     *
     * Has no effect if the engine is not running.
     */
    public void pause();

    /**
     * Signal the engine to resume execution.
     *
     * If the engine was not paused, this will result in
     * the start of the engine.
     */
    public void resume();
}
