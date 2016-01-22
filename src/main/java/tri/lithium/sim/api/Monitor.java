/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.api;

/**
 * Execution monitor interface.
 *
 * A {@link tri.lithium.sim.api.Monitor} can be attached to a {@link tri.lithium.sim.api.Simulator} instance
 * and perform logging, profiling and other activities.
 *
 * @since 1.0.0
 */
public interface Monitor {

    /**
     * Signals the start of the run right after the
     * initialization is done.
     */
    void signalStart();

    /**
     * Signals the end of the run.
     * @param simulationTime
     */
    void signalFinish(double simulationTime);

    /**
     * An error occured during simulation.
     * @param simulationTime
     *      The simulated time at which the error happened,
     * @param throwable
     * @param currentModel
     */
    void signalError(double simulationTime, Throwable throwable, Event currentModel);

    /**
     *  Signals that the simulator has detected a specific
     *  stopping condition.
     *
     * @param condition
     *      The condition which was reached.
     */
    void signalStoppingCondition(StoppingCondition condition);

    /**
     * Signals the start of an iteration. Each iteration starts
     * at a different point in time and might contain many cycles.
     * @param currentTime
     */
    void signalIterationStart(double currentTime);

    /**
     * Marks the end of an iteration
     */
    void signalIterationEnded();

    /**
     * A cycle marks the processing of imminent and
     * external events during a specific simulation time.
     *
     * Time does not advance during a cycle and
     * there can be many.
     */
    void signalCycleStart();

    /**
     * Signals the end of the queue retrieval.
     *
     * The time between the signalCycleStart and the signalImminentEvents event
     * is the time used for retrieving items from the event queue.
     *
     * @param numberOfEvents
     *      The count of imminent or confluent event during this cycle.
     */
    void signalImminentEvents(int numberOfEvents);

    /**
     * Called when during a single cycle before
     * outputs are produced.
     */
    void signalOutputsStarted();

    /**
     * Called if all outputs have been produced.
     */
    void signalOutputsFinished();

    /**
     * A single imminent event is processed.
     *
     * @param event
     *      The event currently being processed.
     */
    void signalImminentEvent(Event event);

    /**
     * A single event has been processed.
     */
    void signalImminentsProcessed();

    /**
     * A single imminent event is processed.
     * @param event
     *      The event currently being processed.
     */
    void signalInfluencedEvent(Event event);


    /**
     * An event has changed it's time-out value.
     * Called right before the event enters the queue.
     */
    void signalRescheduling();

    /**
     * Signal the successful queueing of an event.
     *
     * @param event
     *      The event which has been queued.
     * @param nextEventTime
     *      The time at which it has been queued up.
     */
    void signalRescheduled(Event event, double nextEventTime);

    /**
     * Signals the end of a single simulation step cycle.
     */
    void signalCycleEnded();

    /**
     * Signals the end of the influenced events processing.
     */
    void signalInfluencedProcessed();

    /**
     * Signals that all events for the cycle have been extracted from the event queue.
     */
    void signalDequeueFinished();

    void signalInfluencedEvents(int size);
}

