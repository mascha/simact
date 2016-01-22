/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.queue;

import tri.lithium.meta.pdevs.core.Atomic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * The switch routes an entity to either it's yes or no output port
 * depending on the decision procedure.
 *
 * @param <E>
 */
public class Switch<E> extends Atomic {

    public final Outport<E> no = new Outport<E>("no", this);
    public final Outport<E> yes = new Outport<E>("yes", this);
    public final Inport<E>  input  = new Inport<E>("input", this);

    private final Deque<E> toFirst  = new ArrayDeque<E>();
    private final Deque<E> toSecond = new ArrayDeque<E>();

    private final Decision<E> provider;
    private double clock;

    @Override
    protected void deltaConfluent() {
        deltaExternal(0);
    }

    @Override
    protected void deltaExternal(double elapsedTime) {
        clock += elapsedTime;
        while (input.hasInputs()) {
            E e = input.receive();
            if (provider.decide(e, clock))
                toFirst.add(e);
            else
                toSecond.add(e);
        }

        if (toFirst.isEmpty() && toSecond.isEmpty())
            passivate();
        else
            activate();
    }

    @Override
    protected void deltaInternal() {
        passivate();
    }

    @Override
    protected void outputFunction() {
        while (!toFirst.isEmpty())
            yes.send(toFirst.removeFirst());

        while (!toSecond.isEmpty())
            no.send(toSecond.removeFirst());
    }

    public Switch(String name, Decision<E> provider) {
        Objects.requireNonNull(provider);
        this.setName(name);
        this.provider = provider;
        passivate();
    }


    public interface Decision<E> {
        boolean decide(E e, double time);
    }
}
