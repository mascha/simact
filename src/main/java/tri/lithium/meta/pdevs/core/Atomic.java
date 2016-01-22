/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.meta.pdevs.core;

import tri.lithium.meta.pdevs.api.*;
import tri.lithium.meta.pdevs.api.hints.*;
import tri.lithium.meta.qss.core.DoubleInport;
import tri.lithium.sim.api.Event;
import tri.lithium.sim.api.constants.Time;

import java.util.*;

/**
 * Atomic model class.
 *
 * This class represents a single, indivisible dynamic system,
 * which behaves according to the @ParallelDEVS specification.
 *
 * Currently this class is implemented for non-message based, event
 * scheduling simulators and not for the usual hierarchical version.
 *
 */
public abstract class Atomic extends Entity {

    public static final double INFINITY  = Time.INFINITY;
    public static final double IMMEDIATE = Time.IMMEDIATE;

    /**
     * DeltaConfluent is called exactly when the atomic model is
     * about to do an internal transition and receives external inputs
     * at exactly the same time (Elapsed time equals time advance).
     *
     * As with the internal transition, outputs are produced before
     * the model undergoes the confluent transition.
     *
     * The default implementation yes processes the inputs and afterwards
     * makes the internal transition.
     *
     * Specification:
     *  con : (S x Q) -> S with Q = {(s,e) | s in S, 0 < e < ta(s)}
     *
     * Possible implementations:
     *  empty               ~ ignore
     *  int(s)              ~ do internal only, ignore inputs
     *  ext(s, X, 0)        ~ do external only, ignore internal
     *  int(ext(s, X, 0))   ~ call external, then do internal transition
     *  ext(int(s), X, 0)   ~ call internal, then do external transition
     *  f(s, X, 0)          ~ custom function
     *
     *  e is always zero.
     */
    @Confluence(ConfluenceType.INPUT_FIRST)
    protected void deltaConfluent() {
        deltaExternal(0.0d);
        deltaInternal();
    }

    /**
     * The external transition is invoked if inputs arrive or the confluent
     * transition function invokes it.
     *
     * ext : Q x X -> S   =  (state, elapsedTime) x (inputs) -> state
     *
     * External transitions represent the behaviour of the model in case of external
     * events which are delivered to it's ports.
     *
     * @param elapsedTime
     *      The time since the last transition. If invoked only due to
     *      external inputs, it will take on values > 0.  If it is invoked
     *      by the confluent transition function the elapsedTime will equal zero.
     */
    @ExternalTransition
    protected abstract void deltaExternal(final double elapsedTime);

    /**
     * The internal transition represents the autonomous behaviour of the model
     * if it's not interrupted. The model stays in state s until the simulation
     * time equals the timeout value and then undergoes another internal transition
     * to state s' with another timeout value ta(s').
     *
     * int : S -> S    =   state -> state
     *
     * It is called after outputs have been produced.
     */
    @InternalTransition
    protected abstract void deltaInternal();

    /**
     * The output function, also called lambda, which is responsible
     * to calculate outputs from the internal state only.
     *
     * The produced output trajectories must only depend on
     * pre-transition internal state
     *
     * h : S -> Y (of type Moore)
     *
     * An output function should not change the internal state S, because
     * the wrapper cannot guarantee the correctness of models outside of
     * the discrete event specification otherwise. However if the state modification
     * is side-effect free it is possible to reduce redundant computations
     * between output function and internal and confluent transition functions.
     */
    @OutputFunction
    protected abstract void outputFunction();

    /**
     * Time until next timeout event (internal transition)
     */
    @Timeout
    protected double timeout;

    /**
     * Makes the current model inactive until inputs arrive.
     */
    protected final void passivate() {
        timeout = INFINITY;
    }

    /**
     * Schedules the model to undergo an internal transition in
     * a specific time advance into the future.
     *
     * @param value
     *      The timeout value. The model will be scheduled
     *      at time = (now + value)
     */
    protected final void timeout(final double value) {
        timeout = value;
    }

    /**
     * The model will be scheduled to be immediately
     * executed by setting it's timeout to zero.
     */
    protected final void activate() {
        timeout = IMMEDIATE;
    }

    /**
     * Return the timeout of the current state
     * @return the current timeout value within [0, INFINITY]
     */
    public final double timeAdvance() {
        return timeout;
    }

    /**
     * The simulation container instance.
     */
    Event wrapper;

    /**
     * Mark model as influenced.
     */
    @Deprecated
    public final void markAsInfluenced() {
        if (wrapper != null)
            wrapper.markAsInfluenced();
        else
            throw new RuntimeException(getName() + " : Model was not properly initialized (Missing simulation wrapper)");
    }

    /**
     * List of input ports.
     */
    List<Inport<?>> inports = new ArrayList<Inport<?>>();

    /**
     * List of output ports.
     */
    List<Outport<?>> outports = new ArrayList<Outport<?>>();

    /**
     * Return the input list of the model.
     * @return the input port list of the model
     */
    public final List<Inport<?>> getInports() {
        return inports;
    }

    /**
     * Retrieve outport list
     * @return Outport list of the model
     */
    public final List<Outport<?>> getOutports() {
        if (outports == null)
            outports = new ArrayList<Outport<?>>();
        return outports;
    }

    /**
     * Called by the finalizer visitor.
     */
    public void exitSimulation() {}

    @Override
    final void accept(Visitor visitor) {
        visitor.visitAtomic(this);
    }

    public void cleanseInports() {
        for (int i = 0; i < getInports().size(); i++) {
            getInports().get(i).clear();
        }
    }

    /**
     * Port with input characteristics.
     */
    public static final class Inport<T> extends AtomicPort<T> implements IInport<T> {

        private Deque<T> inputList = new ArrayDeque<T>();

        @Override
        public T receive() {
            return inputList.size() > 0 ? inputList.removeFirst() : null;
        }

        public void forAll(InputHandler<T> handler) {
            while (inputList.size() > 0) handler.process(inputList.removeFirst());
        }

        @Override
        public boolean hasInputs() {
            return inputList.size() > 0;
        }

        public void post(T e) {
            inputList.add(e);
            influenceHost();
        }

        @Override
        public void postFast(double d) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void postFast(double d, double d2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void postFast(double d, double d2, double d3) {
            throw new UnsupportedOperationException();
        }

        public Inport(String name, Atomic atomic) {
            super(name, atomic);
            atomic.getInports().add(this);
        }

        public int size() {
            return inputList.size();
        }

        public void clear() {
            inputList.clear();
        }
    }

    /**
     * Port with output characteristics.
     */
    public static class Outport<T> extends AtomicPort<T> implements IOutport<T> {

        private List<Inport<T>> terminalPorts = new ArrayList<Inport<T>>();
        private List<Composite.CoupledPort<T>> coupledPorts = new ArrayList<Composite.CoupledPort<T>>();
        private List<DoubleInport> primitivePorts = new ArrayList<DoubleInport>();

        @Override
        public List<IPort<T>> getRemotePorts() {
            List<IPort<T>> list = new ArrayList<IPort<T>>();
            list.addAll(coupledPorts);
            list.addAll(terminalPorts);
            return list;
        }

        @Override
        public final void addRemotePort(IPort<T> port) {
            if (port instanceof Composite.CoupledPort)
                coupledPorts.add((Composite.CoupledPort<T>) port);
            else if (port instanceof DoubleInport)
                primitivePorts.add((DoubleInport) port);
            else
                terminalPorts.add((Inport<T>) port);
        }

        public void send(T e) {
            for (int i = 0; i < coupledPorts.size(); i++) {
                coupledPorts.get(i).post(e);
            }

            for (int i = 0; i < terminalPorts.size(); i++) {
                terminalPorts.get(i).post(e);
            }

            for (int i = 0; i < primitivePorts.size(); i++) {
                primitivePorts.get(i).post((Double) e);
            }
        }

        @Override
        public void sendPrimitive(double d) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendPrimitive(double d, double d2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendPrimitive(double d, double d2, double d3) {
            throw new UnsupportedOperationException();
        }

        public Outport(String name, Atomic atomic) {
            super(name, atomic);
            atomic.getOutports().add(this);
        }

    }

    /**
     * General port class for atomic models.
     */
    public static class AtomicPort<T> extends Port<T> {
        private Atomic model;

        public Atomic getModel() {
            return model;
        }

        public AtomicPort(String name, Atomic atomic) {
            Objects.requireNonNull(atomic, "Each port needs to have a valid parent instance");
            this.model = atomic;
            this.setName(name);
        }

        @Override
        public Entity getHost() {
            return model;
        }

        @Deprecated
        protected final void influenceHost() {
            model.markAsInfluenced();
        }

    }

    public interface InputHandler<E> {
        void process(E e);
    }
}

