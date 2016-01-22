/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.util.visitor;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.core.Wrapper;
import tri.lithium.meta.pdevs.core.Visitor;
import tri.lithium.sim.api.Simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Visitor instance that initializes all atomic
 * models and returns them as a list.
 */
public class Initializer extends Visitor.Base {

    private final Simulator simulator;
    private List<Wrapper> models = new ArrayList<Wrapper>(100);

    @Override
    public void visitAtomic(Atomic atomic) {
        initializeModel(atomic);
        models.add(new Wrapper(atomic, simulator));
    }

    private void initializeModel(Atomic atomic) {

    }

    public List<Wrapper> getModelList() {
        return models;
    }

    public Initializer(Simulator simulator) {
        Objects.requireNonNull(simulator, "Visitor needs non-null simulator instance to initialize models");
        this.simulator = simulator;
    }

}
