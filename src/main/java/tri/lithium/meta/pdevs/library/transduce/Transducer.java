/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.transduce;

import tri.lithium.meta.pdevs.core.Atomic;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A transducer converts an entity of type A to type B.
 * @param <A> Original type
 * @param <B> New type
 */
public class Transducer<A,B> extends Atomic {

    private final Converter<A,B> converter;

    public final Outport<B> output = new Outport<>("output", this);
    public final Inport<A> input = new Inport<>("input", this);

    Deque<B> toSend = new ArrayDeque<>();

    @Override
    protected void deltaExternal(double elapsedTime) {
        while (input.hasInputs())
            toSend.add(converter.convert(input.receive()));

        if (toSend.isEmpty())
            passivate();
        else
            activate();
    }

    @Override
    protected void deltaInternal() {
        if (toSend.isEmpty())
            passivate();
        else
            activate();
    }

    @Override
    protected void outputFunction() {
        while (!toSend.isEmpty())
            output.send(toSend.removeFirst());
    }

    public Transducer(Converter<A,B> converter) {
        this.converter = converter;
    }

    public interface Converter<A,B> {
        B convert(A a);
    }
}
