/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.core.simulator;

import tri.lithium.sim.api.Monitor;
import tri.lithium.sim.api.Simulator;
import tri.lithium.sim.api.StoppingCondition;
import tri.lithium.sim.api.Event;
import tri.lithium.sim.core.simulator.queue.EventQueue;
import tri.lithium.sim.core.simulator.queue.FastList;
import tri.lithium.sim.core.simulator.queue.FastListImpl;
import tri.lithium.sim.core.simulator.queue.PriorityQueue;

import java.util.Collection;

/**
 * Stand-alone simulator.
 *
 * Can be run to simulate a flat model of {@link tri.lithium.sim.api.Event} instances.
 *
 */
public final class FastSimulator implements Simulator {

    private double currentTime;
    private double endTime        = 100;
    private int maximumIterations = Integer.MAX_VALUE;

    /*
     * Event queue instance.
     */
    private EventQueue eventQueue;

    /*
     * Slack time which makes all events inside the interval
     * (currentTime ... currentTime + slackTime) imminent.
     */
    @Deprecated
    private double slackTime;

    FastList influencedSet;

    @Override
    public void runSimulation(Monitor monitor) {
        if (monitor != null)
            runMonitored(monitor);
        else
           runFast();
    }

    private void runFast() {
        influencedSet             = new FastListImpl(eventQueue.size());
        FastList scheduledEvents  = new FastListImpl(eventQueue.size());

        try {

            int iterations = 0;

            while (iterations < maximumIterations) {
                iterations++;

                currentTime = eventQueue.getMin();

                if (currentTime > endTime) {
                    break;
                }

                eventQueue.dequeueAll(scheduledEvents);

                int i = 0;
                while (i < scheduledEvents.size()) {
                    scheduledEvents.get(i).produceOutputs();
                    i++;
                }

                i = 0;
                while (i < scheduledEvents.size()) {
                    Event imminent = scheduledEvents.get(i);

                    /** Make the event execute its internal/confluent state transition */
                    imminent.processImminent(currentTime);

                    /** Reschedule event with new time */

                    double timeout = imminent.getTimeout();
                    if (timeout <= endTime)
                        eventQueue.requeue(imminent);

                    i++;
                }


                for (i = 0; i < influencedSet.size(); i++) {
                    Event influenced = influencedSet.get(i);

                    /** Make the event execute its internal state transition */
                    influenced.processInfluenced(currentTime);

                    /** Reschedule event with new time */

                    double timeout = influenced.getTimeout();
                    if (timeout <= endTime)
                        eventQueue.requeue(influenced);
                }

                influencedSet.clear();

            }
        } catch (Exception e) {
            System.err.println("Error at " + currentTime);
            e.printStackTrace(System.err);
            System.err.flush();
        }
    }

    private void runMonitored(Monitor monitor) {
        monitor.signalStart();

        influencedSet             = new FastListImpl(eventQueue.size());
        FastList scheduledEvents  = new FastListImpl(eventQueue.size());

        Event currentModel = null;

        try {

            int iterations = 0;

            while (iterations < maximumIterations) {
                iterations++;

                currentTime = eventQueue.getMin();

                monitor.signalIterationStart(currentTime);

                if (currentTime > endTime) {
                    monitor.signalStoppingCondition(StoppingCondition.FINAL_TIME);
                    break;
                }

                eventQueue.dequeueAll(scheduledEvents);

                monitor.signalDequeueFinished();
                monitor.signalOutputsStarted();

                int i = 0;
                while (i < scheduledEvents.size()) {
                    scheduledEvents.get(i).produceOutputs();
                    i++;
                }

                monitor.signalOutputsFinished();
                monitor.signalCycleStart();
                monitor.signalImminentEvents(scheduledEvents.size());

                i = 0;
                while (i < scheduledEvents.size()) {
                    currentModel = scheduledEvents.get(i);
                    monitor.signalImminentEvent(currentModel);

                    /** Make the event execute its internal/confluent state transition */
                    currentModel.processImminent(currentTime);

                    /** Reschedule event with new time */
                    monitor.signalRescheduling();

                    double timeout = currentModel.getTimeout();
                    if (timeout < endTime)
                        eventQueue.requeue(currentModel);

                    monitor.signalRescheduled(currentModel, timeout);
                    i++;
                }

                monitor.signalImminentsProcessed();
                monitor.signalInfluencedEvents(influencedSet.size());

                for (i = 0; i < influencedSet.size(); i++) {
                    currentModel = influencedSet.get(i);

                    monitor.signalInfluencedEvent(currentModel);

                    /** Make the event execute its internal state transition */
                    currentModel.processInfluenced(currentTime);

                    /** Reschedule event with new time */
                    monitor.signalRescheduling();

                    double timeout = currentModel.getTimeout();
                    if (timeout < endTime)
                        eventQueue.requeue(currentModel);

                    monitor.signalRescheduled(currentModel, timeout);
                }

                influencedSet.clear();

                monitor.signalInfluencedProcessed();
                monitor.signalCycleEnded();
                monitor.signalIterationEnded();
            }
        } catch (Exception exception) {
            monitor.signalError(currentTime, exception, currentModel);
        }

        monitor.signalFinish(currentTime);
    }

    public void useEvents(Collection<? extends Event> events) {
        for (Event event : events) eventQueue.enqueue(event);
    }

    public FastSimulator setMaximumIterations(int maximumIterations) {
        this.maximumIterations = maximumIterations;
        return this;
    }

    @Deprecated
    public void setSlack(double slack) {
        if (slack < 0) throw new RuntimeException("Slack cannot be negative");
        this.slackTime = slack;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    @Override
    public void addToSet(Event event) {
        influencedSet.add(event);
    }

    /* constructors */

    public FastSimulator() {
        eventQueue = new PriorityQueue(256);
    }

}
