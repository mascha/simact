/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.core.monitor;

import tri.lithium.sim.api.Monitor;
import tri.lithium.sim.api.StoppingCondition;
import tri.lithium.sim.api.Event;

/**
 * A monitor to profile simulation runs.
 */
public final class ProfilingMonitor implements Monitor {

    private long executionTime;
    private long iterationStart;
    private double averageIterationTime;
    private double iterationVariance;
    private int  iterations;
    private long imminentsStart;
    private long outputTime;
    private long dequeueTime;
    private long rescheduleTime;
    private long cycleStart;
    private long imminentTime;
    private long rescheduleStart;
    private int rescheduleCount;
    private long influencedStart;
    private long influenceTime;
    private long lastReschedule;
    private int cycles;
    private long outputsStart;

    @Override
    public void signalStart() {
        executionTime = System.nanoTime();
    }

    @Override
    public void signalIterationStart(double currentTime) {
        iterations++;
        iterationStart = System.nanoTime();
    }

    @Override
    public void signalDequeueFinished() {
        dequeueTime += System.nanoTime() - iterationStart;
    }

    @Override
    public void signalInfluencedEvents(int size) {

    }


    @Override
    public void signalCycleStart() {
        cycles++;
    }

    @Override
    public void signalOutputsStarted() {
        outputsStart = System.nanoTime();
    }


    @Override
    public void signalOutputsFinished() {
        outputTime += System.nanoTime() - outputsStart;
    }

    @Override
    public void signalImminentEvents(int numberOfEvents) {
        imminentsStart = System.nanoTime();
    }

    @Override
    public void signalImminentEvent(Event event) {

    }

    @Override
    public void signalImminentsProcessed() {
        long time = System.nanoTime();
        imminentTime   += time - imminentsStart - lastReschedule;
        lastReschedule  = 0;
        influencedStart = time;
    }

    @Override
    public void signalInfluencedEvent(Event event) {

    }

    @Override
    public void signalInfluencedProcessed() {
        influenceTime += System.nanoTime() - influencedStart - lastReschedule;
        lastReschedule = 0;
    }

    @Override
    public void signalRescheduling() {
        rescheduleStart = System.nanoTime();
    }

    @Override
    public void signalRescheduled(Event event, double nextEventTime) {
        long time = System.nanoTime() - rescheduleStart;
        lastReschedule += time;
        rescheduleTime += time;
        rescheduleCount++;
    }


    @Override
    public void signalCycleEnded() {

    }


    @Override
    public void signalIterationEnded() {
        long time = System.nanoTime();
        averageIterationTime = incrementalAverage(averageIterationTime, time - iterationStart, iterations);
    }


    @Override
    public void signalStoppingCondition(StoppingCondition condition) {
        switch (condition) {
            case FINAL_TIME:
                break;
            case MAXIMUM_ITERATION:
                break;
            case STATISTIC:
                break;
            case STEADY_STATE:
                break;
        }
    }

    @Override
    public void signalFinish(double simulationTime) {
        executionTime = System.nanoTime() - executionTime;
    }


    @Override
    public void signalError(double simulationTime, Throwable throwable, Event currentModel) {
        System.err.println("Error occurred at " + simulationTime);
        System.err.println("Current scope : " + currentModel.getModel());
        throwable.printStackTrace();
        System.err.flush();
    }

    private final static class ProfilingData {

        private int timesImminent;
        private int timesInfluenced;

        private int timesTransient;
        private int timesPassive;

        private double averageExternalTime;
        private double sumExternalTime;
        private double externalTimeVariance;

        private double averageinternalTime;
        private double sumInternalTime;
        private double internalVariance;

        private double averageScheduleTime;
        private double scheduleVariance;

        private final Event model;

        public ProfilingData(Event event) {
            this.model = event;
        }
    }

    public static double incrementalAverage(double previousAverage, double value, int iterationIndex) {
        return ( (value - previousAverage) / (iterationIndex + 1)) + previousAverage;
    }

    public static double incrementalVariance(double previousVariance, double value, double previousMean, int iteration) {
        return ( (iteration - 2) / (iteration - 1) * previousVariance + (value - previousMean) * (value - previousMean) / iteration);
    }

    public static double incrementalSkew(double previousSkew, double value, double previousVariance, int iteration) {
        return previousSkew;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(1024);
        builder
                .append("==================================\n")
                .append("Profiling results\n")
                .append(("==================================\n\n"))
                .append("Run took     ").append(executionTime / 1000000f).append(" ms\n\n")
                .append("Iterations   ").append(iterations).append("\n")
                .append("   Average   ").append(averageIterationTime / 1000000f).append(" ms").append("\n\n")
                .append("Cycles       ").append(cycles).append("\n")
                .append("   Iteration ").append((float )cycles / iterations).append("\n\n")
                .append("Outputs      ").append(outputTime / 1000000f).append(" ms").append(" ~ ").append((float) outputTime * 100 / executionTime).append("%\n\n")
                .append("Scheduling   ").append((dequeueTime + rescheduleTime) / 1000000f).append(" ms").append(" ~ ").append((float) (dequeueTime + rescheduleTime) * 100 / executionTime).append("%\n")
                .append("   dequeue     ").append(dequeueTime / 1000000f).append(" ms").append(" ~ ").append((float) (dequeueTime) * 100 / executionTime).append("%\n")
                .append("    update     ").append(rescheduleTime / 1000000f).append(" ms").append(" ~ ").append((float) (rescheduleTime) * 100 / executionTime).append("%\n")
                .append("    events     ").append(rescheduleCount).append("\n\n")
                .append("Int/Con      ").append(imminentTime / 1000000f).append(" ms").append(" ~ ").append((float) imminentTime * 100 / executionTime).append("%\n\n")
                .append("External     ").append(influenceTime / 1000000f).append(" ms").append(" ~ ").append((float) influenceTime * 100 / executionTime).append("%\n\n")
                .append("Profiling    ").append(100 * (1 - (influenceTime + imminentTime + dequeueTime + rescheduleTime + outputTime) / (float ) executionTime)).append(" %")
                //.append("    Avg.     ").append(averageOutputTime / 1000000f).append(" ms\n")
        ;
        return builder.toString();
    }
}
