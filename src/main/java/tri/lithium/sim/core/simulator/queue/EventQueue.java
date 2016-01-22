/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.core.simulator.queue;

import tri.lithium.sim.api.Event;

public interface EventQueue {

    /**
     * Dequeue all elements with the smallest time stamp.
     */
    void dequeueAll(FastList list);

    /**
     * Reschedule an already scheduled event.
     * @param event
     *      Event to enqueue.
     */
    void requeue(Event event);

    /**
     * Schedule an event.
     * @param event
     *      Event to enqueue.
     */
    void enqueue(Event event);

    /**
     * Get the time of the current imminent event.
     * @return time or INFINITY
     */
    double getMin();

    int size();
}