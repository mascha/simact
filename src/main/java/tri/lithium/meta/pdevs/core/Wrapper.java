/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.meta.pdevs.core;

import tri.lithium.sim.api.Event;
import tri.lithium.sim.api.Simulator;
import tri.lithium.sim.api.exceptions.simulator.SimulatorLogicBreachException;
import tri.lithium.sim.api.exceptions.simulator.SimulatorScheduleException;
import tri.lithium.sim.core.simulator.queue.FastList;

import java.util.Objects;

/**
 * Event wrapper class.
 *
 * Implements fields and methods for usage in
 * a monolithic event scheduling engine.
 */
public final class Wrapper implements Event {

    /*
     * The wrapped atomic model.
     */
    private final Atomic model;

    /*
     * The parent wrapper instance.
     */
    private final Simulator simulator;

    /**
     * Point in time where the atomic model
     * will undergo an internal transition.
     */
    public double nextTime;

    /**
     * Time when the last internal
     * transition took place
     */
    public double lastTime;

    /*
     * Imminent model flag.
     *
     * Imminence means that this timeout value
     * equals the current simulation clock.
     */
    private boolean imminentFlag;

    /*
     * Influence flag.
     *
     * Marks this model as influenced by
     * external events so it can undergo an
     * external or confluent transition.
     */
    private boolean influencedFlag;

    /*
     * Influence set cache.
     */
    @Deprecated
    private static FastList influenceSet;

    /**
     * Callback method.
     */
    public final void markAsInfluenced() {
        if (influencedFlag) return;
        influencedFlag = true;
        simulator.addToSet(this);
    }

    @Override
    public Object getModel() {
        return model;
    }

    @Override
    public double getTimeout() {
        return nextTime;
    }

    @Override
    public void produceOutputs() {
        imminentFlag = true;
        model.outputFunction();
    }

    @Override
    public void processImminent(final double currentTime) {

        if (!influencedFlag)
            model.deltaInternal();
        else {
            model.deltaConfluent();
            clearInports();
        }

        double advance = model.timeAdvance();

        checkTimeValue(advance);

        lastTime     = currentTime;
        nextTime     = currentTime + advance;
        imminentFlag = false;
    }

    @Override
    public final void processInfluenced(final double currentTime) {
        if (imminentFlag)
            throw new SimulatorLogicBreachException(model, SimulatorLogicBreachException.INFLUENCED_STILL_IMMINENT);

        model.deltaExternal(currentTime - lastTime);
        clearInports();

        double advance = model.timeAdvance();
        checkTimeValue(advance);

        lastTime = currentTime;
        nextTime = currentTime + advance;
        influencedFlag = false;
    }

    private void clearInports() {
        model.cleanseInports();
    }

    private void checkTimeValue(double advance) {
        if (advance < 0) throw new SimulatorScheduleException(model, SimulatorScheduleException.NEGATIVE_TIME, advance);
    }

    private void setupModel() {
        model.wrapper = this;
        nextTime = model.timeAdvance();
        lastTime = 0;
        checkTimeValue(nextTime);
    }

    public Wrapper(Atomic model, Simulator simulator) {
        Objects.requireNonNull(model, "Atomic model cannot be null");
        Objects.requireNonNull(simulator, "Simulator instance cannot be null");

        this.simulator = simulator;
        this.model = model;

        setupModel();
    }

}
