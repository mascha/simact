/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.api;

/**
 * Allow listening to state changes on the
 * simulation engine.
 */
public interface SimulationStateListener {

    /**
     * Called when the state of the engine changes.
     * @param oldState
     *      The old state of the engine.
     * @param currentState
     *      The new state of the engine.
     */
    void callback(EngineState oldState, EngineState currentState);
}
