/*
 * Copyright (C) Martin Schade 2015. All rights reserved. No commercial and non-commerical usage.
 */

package tri.lithium.meta.pdevs.library.resource;

import tri.lithium.meta.pdevs.library.basic.Compute;

import java.util.ArrayDeque;
import java.util.Deque;

public class Detach<E,R> extends Compute {

    private final ReleaseProvider<E,R> provider;

    public final Inport<E>  input    = new Inport<E>("input", this);
    public final Outport<E> output   = new Outport<E>("output", this);
    public final Outport<R> released = new Outport<R>("input", this);

    Deque<R> dequeRes = new ArrayDeque<R>();
    Deque<E> dequeEnt = new ArrayDeque<E>();

    @Override
    protected void deltaExternal(double elapsedTime) {
        while (input.hasInputs()) {
            E e = input.receive();
            dequeEnt.add(e);

            R r = provider.detachFrom(e);
            if (r != null) dequeRes.add(r);
        }

        if (dequeEnt.isEmpty() && dequeRes.isEmpty())
            passivate();
        else
            activate();
    }

    @Override
    protected void outputFunction() {
        while (!dequeEnt.isEmpty())
            output.send(dequeEnt.removeFirst());
        while (!dequeRes.isEmpty())
            released.send(dequeRes.removeFirst());
    }

    public Detach(String name, ReleaseProvider<E, R> provider) {
        this.setName(name);
        this.provider = provider;
    }

    @Override
    public String toString() {
        return getName() + " : Detach()";
    }

    public interface ReleaseProvider<E,R> {
        R detachFrom(E e);
    }
}
