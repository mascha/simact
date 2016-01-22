/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.meta.pdevs.core;

import tri.lithium.meta.pdevs.api.IPort;

/**
 * Port interface.
 */
public abstract class Port<T> implements IPort<T> {

    private String name;
    private Entity host;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void post(T data) {
        throw new UnsupportedOperationException();
    }

    public Entity getHost() {
        return host;
    }
}
