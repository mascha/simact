package tri.lithium.sim.core.simulator.queue;

import tri.lithium.sim.api.Event;

/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
public interface FastList {
    void clear();

    boolean isEmpty();

    int size();

    void add(Event event);

    Event remove();

    Event get(int position);

    void set(int position, Event v);
}
