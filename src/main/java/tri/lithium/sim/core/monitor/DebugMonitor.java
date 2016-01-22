/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.core.monitor;

import tri.lithium.sim.api.Monitor;
import tri.lithium.sim.api.StoppingCondition;
import tri.lithium.sim.api.Event;

/**
 * A monitor for logging to the console.
 */
public class DebugMonitor implements Monitor {

    private double currentTime;
    private int iterationCount;
    private Event currentEvent;

    @Override
    public void signalStart() {
        System.out.println("Starting simulation\n");
    }

    @Override
    public void signalFinish(double simulationTime) {
        System.out.println("Simulation finished");
    }

    @Override
    public void signalError(double simulationTime, Throwable throwable, Event currentModel) {
        System.out.println("Error occurred at" + simulationTime);
        throwable.printStackTrace(System.out);
    }

    @Override
    public void signalStoppingCondition(StoppingCondition finalTime) {
        System.out.println("Stopping condition reached");
    }

    @Override
    public void signalIterationStart(double currentTime) {
        if (currentTime < Double.POSITIVE_INFINITY) {
            iterationCount++;
            System.out.println("Iteration " + iterationCount + " (" + currentTime + ") \n.....................");
        }
    }

    @Override
    public void signalIterationEnded() {
        System.out.println();
    }

    @Override
    public void signalCycleStart() {}

    @Override
    public void signalImminentEvents(int numberOfEvents) {
        if (numberOfEvents > 0)
            System.out.println(" Imminent events: " + numberOfEvents);
    }

    @Override
    public void signalOutputsStarted() {}

    @Override
    public void signalOutputsFinished() {}

    @Override
    public void signalImminentEvent(Event event) {
        currentEvent = event;
        System.out.println("\t> " + event.getModel());
    }

    @Override
    public void signalImminentsProcessed() {
    }

    @Override
    public void signalInfluencedEvent(Event event) {
        System.out.println("\t> " + event.getModel());
    }

    @Override
    public void signalRescheduling() {

    }

    @Override
    public void signalRescheduled(Event event, double nextEventTime) {
        System.out.println("\t# " + event.getModel());
        if (nextEventTime == 0) {
            System.out.println("\t@ imminent");
        } else if (Double.isInfinite(nextEventTime)) {
            System.out.println("\t@ passive");
        } else {
            System.out.println("\t@ "+nextEventTime);
        }
    }

    @Override
    public void signalCycleEnded() {}

    @Override
    public void signalInfluencedProcessed() {

    }

    @Override
    public void signalDequeueFinished() {

    }

    @Override
    public void signalInfluencedEvents(int numberOfEvents) {
        if (numberOfEvents > 0) System.out.println(" Influenced events: " + numberOfEvents);
    }
}
