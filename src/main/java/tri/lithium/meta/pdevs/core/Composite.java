/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.meta.pdevs.core;

import tri.lithium.meta.pdevs.api.IInport;
import tri.lithium.meta.pdevs.api.IOutport;
import tri.lithium.meta.pdevs.api.IPort;
import tri.lithium.meta.qss.core.DoubleInport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite are composed of {@link tri.lithium.meta.pdevs.core.Atomic},
 * {@link tri.lithium.meta.pdevs.core.Composite} and ports.
 */
public class Composite extends Entity {

    private List<Entity> children;

    private ArrayList<Inport<?>> inports;
    private ArrayList<Outport<?>> outports;

    public List<Entity> getChildren() {
        if (children == null) children = new ArrayList<Entity>();
        return children;
    }

    public final Composite add(Entity entity) {
        if (this != entity)
            getChildren().add(entity);
        return this;
    }

    public final Composite add(Entity... entities) {
        for (Entity entity : entities) {
            entity.setParent(this);
            getChildren().add(entity);
        }
        return this;
    }

    public final Entity findChild(String name, boolean recursive) {
        for (int i = 0; i < children.size(); i++) {
            if (name.equals(children.get(i).getName()))
                return children.get(i);
        }
        return null;
    }


    /**
     * Return the input list of the model.
     * @return the input port list of the model
     */
    public final List<Inport<?>> getInports() {
        if (inports == null)
            inports = new ArrayList<Inport<?>>();
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
     * Add outport to composite model.
     * @param outport
     */
    public void addOutport(Outport<?> outport) {
        getOutports().add(outport);
    }

    /**
     * Add input to composite model.
     * @param inport
     */
    public void addInport(Inport<?> inport) {
        getInports().add(inport);
    }

    public final Entity findChild(String name) {
        return findChild(name, false);
    }

    /**
     * Find input by name.
     * @param name
     * @return
     */
    public final Inport<?> findInport(String name) {
        if (inports == null || inports.isEmpty())
            return null;
        else
            for (Inport<?> inport : inports)
                if (inport.getName().equals(name))
                    return inport;
        return null;
    }

    /**
     * Find outport by name.
     * @param name
     * @return
     */
    public final Outport<?> findOutport(String name) {
        if (outports == null || outports.isEmpty())
            return null;
        else
            for (Outport<?> outport : outports)
                if (outport.getName().equals(name))
                    return outport;
        return null;
    }

    @Override
    final void accept(Visitor visitor) {
        visitor.visitComposite(this);
    }

    public Composite(String name, Entity... children) {
        this(name);
        add(children);
    }

    public Composite(String name) {
        setName(name);
    }




    /**
     * Coupled port base class.
     * @param <T>
     */
    public static abstract class CoupledPort<T> extends Port<T> implements IPort<T> {

        private Composite model;

        protected List<CoupledPort<T>> remotePorts = new ArrayList<CoupledPort<T>>();

        public void addRemotePort(IPort<T> port) {
            if (port instanceof CoupledPort)
                remotePorts.add((CoupledPort<T>) port);
        }

        @Override
        public Entity getHost() {
            return model;
        }

        @Override
        public void post(T e) {
            for (int i = 0; i < remotePorts.size(); i++) {
                remotePorts.get(i).post(e);
            }
        }

        protected final Composite getModel() {
            return model;
        }

        protected CoupledPort(String name, Composite composite) {
            this.model = composite;
            this.setName(name);
        }

    }

    /**
     *Coupled outport class.
     * @param <T>
     */
    public static final class Outport<T> extends CoupledPort<T> implements IOutport<T>{

        private List<Atomic.Inport<T>> terminalPorts = new ArrayList<Atomic.Inport<T>>();

        @Override
        public void send(T t) {
            throw new UnsupportedOperationException();
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

        @Override
        public List<IPort<T>> getRemotePorts() {
            List<IPort<T>> list = new ArrayList<>(terminalPorts.size() + remotePorts.size());
            list.addAll(terminalPorts);
            list.addAll(remotePorts);
            return list;
        }

        @Override
        public final void addRemotePort(IPort<T> port) {
            if (port instanceof Atomic.Inport)
                terminalPorts.add((Atomic.Inport<T>) port);
            else if (port instanceof Atomic.Outport)
                System.err.println("Warning: Cannot connect input to output port [" + port.getName() + " for " + getModel() +"]");
            else
                super.addRemotePort(port);
        }

        @Override
        public void post(T e) {
            super.post(e);

            for (int i = 0; i < terminalPorts.size(); i++) {
                terminalPorts.get(i).post(e);
            }
        }

        public Outport(Composite composite) {
            this(null, composite);
        }

        public Outport(String name, Composite composite) {
            super(name, composite);
            composite.getOutports().add(this);
        }
    }

    /**
     * Coupled port inport class.
     * @param <T>
     */
    public static final class Inport<T> extends CoupledPort<T> implements IInport<T> {

        private List<IInport<T>> terminalPorts = new ArrayList<IInport<T>>();

        public Inport(Composite composite) {
            this(null, composite);
        }

        public Inport(String name, Composite composite) {
            super(name, composite);
            composite.addInport(this);
        }

        @Override
        public final void addRemotePort(IPort<T> port) {
            if (port instanceof Atomic.Inport)
                terminalPorts.add((Atomic.Inport<T>) port);
            else if (port instanceof Atomic.Outport)
                System.err.println("Warning: Cannot connect input to output port [" + port.getName() + " for " + getModel() +"]");
            else if (port instanceof DoubleInport) {
                terminalPorts.add((IInport<T>) port);
            } else
                super.addRemotePort(port);
        }

        @Override
        public boolean hasInputs() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void post(T e) {
            super.post(e);

            for (int i = 0; i < terminalPorts.size(); i++)
                terminalPorts.get(i).post(e);

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

        @Override
        public T receive() {
            throw new UnsupportedOperationException();
        }
    }
}
