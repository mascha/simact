/*
 * Copyright (C) Martin Schade 2015. All rights reserved. No commercial and non-commerical usage.
 */

package tri.lithium.meta.pdevs.library.resource;

import tri.lithium.meta.pdevs.library.basic.Compute;

public class Attach<E> extends Compute {

    private final AttachProvider<E> provider;

    public final Inport  input       = new Inport("input", this);
    public final Outport<E> output   = new Outport<E>("output", this);

    E result;

    private int batchSize = -1;
    private Object[] objects;

    @Override
    protected void deltaExternal(double elapsedTime) {
        if (input.hasInputs()) {

            int i = 0;
            while (input.hasInputs()) {
                objects[i] = input.receive();
                i++;
            }

            result = provider.coalesce(objects);
        }
        activate();
    }

    @Override
    public String toString() {
        return getName() + " : Attach()";
    }

    @Override
    protected void outputFunction() {
        if (result != null) {
            output.send(result);
            result = null;
        }
    }

    public Attach(String name, AttachProvider<E> provider, int numberOfSequentialObjects) {
        this.provider  = provider;
        this.setName(name);
        this.batchSize = numberOfSequentialObjects;
        this.objects   = new Object[batchSize];
    }



    public interface AttachProvider<E> {
        E coalesce(Object[] objects);
    }
}
