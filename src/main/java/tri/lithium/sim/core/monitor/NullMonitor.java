/*
 * Copyright (C) Martin Schade 2015. All rights reserved. No commercial and non-commerical usage.
 */

package tri.lithium.sim.core.monitor;

import tri.lithium.sim.api.Event;
import tri.lithium.sim.api.Monitor;
import tri.lithium.sim.api.StoppingCondition;

public class NullMonitor implements Monitor {
    @Override
    public void signalStart() {

    }

    @Override
    public void signalFinish(double simulationTime) {

    }

    @Override
    public void signalError(double simulationTime, Throwable throwable, Event currentModel) {

    }

    @Override
    public void signalStoppingCondition(StoppingCondition condition) {

    }

    @Override
    public void signalIterationStart(double currentTime) {

    }

    @Override
    public void signalIterationEnded() {

    }

    @Override
    public void signalCycleStart() {

    }

    @Override
    public void signalImminentEvents(int numberOfEvents) {

    }

    @Override
    public void signalOutputsStarted() {

    }

    @Override
    public void signalOutputsFinished() {

    }

    @Override
    public void signalImminentEvent(Event event) {

    }

    @Override
    public void signalImminentsProcessed() {

    }

    @Override
    public void signalInfluencedEvent(Event event) {

    }

    @Override
    public void signalRescheduling() {

    }

    @Override
    public void signalRescheduled(Event event, double nextEventTime) {

    }

    @Override
    public void signalCycleEnded() {

    }

    @Override
    public void signalInfluencedProcessed() {

    }

    @Override
    public void signalDequeueFinished() {

    }

    @Override
    public void signalInfluencedEvents(int size) {

    }
}
