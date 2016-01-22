/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.api;

import java.util.Collection;

/**
 * Simulator interface.
 * 
 * A simulator executes a single replication of an
 * already initialized and flattened model instance. 
 */
public interface Simulator {
    /**
     * Execute a single simulation run using the
     * given event set and parameters.
     *
     * @param monitor
     *      The {@link tri.lithium.sim.api.Monitor} instance to use. Can be null.
     */
    void runSimulation(Monitor monitor);

    /**
     * Set the initial event set.
     * @param events
     *      The initial non-empty event set.
     *
     * TODO refactor to constant object.
     */
    void useEvents(Collection<? extends Event> events);

    /**
     * Set the stopping time.
     * @param endTime
     *      The stopping time.
     *
     * TODO refactor to constant object.
     */
    void setEndTime(double endTime);

    void addToSet(Event event);
}
