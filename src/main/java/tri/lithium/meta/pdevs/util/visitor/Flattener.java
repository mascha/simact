/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.util.visitor;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.core.Visitor;

/**
 * Visitor that recursively flattens the tree structure
 * into a flat one.
 */
public class Flattener extends Visitor.Base {

    @Override
    public void visitAtomic(Atomic atomic) {

    }

    @Override
    public void enterComposite(Composite composite) {

    }

    @Override
    public void leaveComposite(Composite composite) {

    }
}
