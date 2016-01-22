/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.core.simulator.queue;

import tri.lithium.sim.api.Event;

public final class FastListImpl implements FastList {

    private int current = -1;

    private Event v0;
    private Event v1;
    private Event v2;
    private Event v3;
    private Event v4;
    private Event v5;
    private Event v6;
    private Event v7;
    private Event v8;
    private Event v9;

    private Event[] array;

    /**
     * Reset the header pointer but does not remove the event references.
     */
    @Override
    public final void clear() {
        current = -1;
    }

    /**
     * Check wether events are in this list.
     */
    @Override
    public final boolean isEmpty() {
        return current == -1;
    }

    /**
     * Get the current count of event in the list.
     */
    @Override
    public final int size() {
        return current + 1;
    }

    @Override
    public final void add(Event event) {
        current++;
        set(current, event);
    }

    @Override
    public final Event remove() {
        return isEmpty() ? get(--current) : null;
    }

    @Override
    public final Event get(final int position) {
        switch (position) {
            case 0: return v0;
            case 1: return v1;
            case 2: return v2;
            case 3: return v3;
            case 4: return v4;
            case 5: return v5;
            case 6: return v6;
            case 7: return v7;
            case 8: return v8;
            case 9: return v9;
            default:return array[position - 10];
        }
    }

    @Override
    public void set(final int position, final Event v) {
        switch (position) {
            case 0: v0 = v; break;
            case 1: v1 = v; break;
            case 2: v2 = v; break;
            case 3: v3 = v; break;
            case 4: v4 = v; break;
            case 5: v5 = v; break;
            case 6: v6 = v; break;
            case 7: v7 = v; break;
            case 8: v8 = v; break;
            case 9: v9 = v; break;
            default: array[position - 10] = v;
        }
    }

    public FastListImpl(int size) {
        if (size > 10) {
            array = new Event[size - 10];
        }
    }
}