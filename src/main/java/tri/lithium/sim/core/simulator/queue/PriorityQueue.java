/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.core.simulator.queue;

import tri.lithium.sim.api.Event;
import java.util.Comparator;

/**
 * Custom priority queue.
 */
public class PriorityQueue implements EventQueue {

    java.util.PriorityQueue<Event> queue;

    public void dequeueAll(FastList list) {
        list.clear();

        if (queue.size() > 0) {
            Event min = queue.poll();
            list.add(min);
            double time = min.getTimeout();
            while ((queue.size() > 0) && time == queue.peek().getTimeout())
                list.add(queue.poll());
        }
    }

    @Override
    public void requeue(Event event) {
        queue.remove(event);
        queue.add(event);
    }

    @Override
    public void enqueue(Event event) {
        queue.add(event);
    }

    @Override
    public double getMin() {
        if (queue.size() > 0)
            return queue.peek().getTimeout();
        else
            return Double.POSITIVE_INFINITY;
    }

    @Override
    public int size() {
        return queue.size();
    }

    public PriorityQueue(int initialSize) {
        queue = new java.util.PriorityQueue<Event>(initialSize,
                new Comparator<Event>() {
                    public int compare(Event x, Event y) {
                        double a = x.getTimeout();
                        double b = y.getTimeout();
                        return a < b ? -1 : a > b ? 1 : 0;
                    }
                });
    }

}