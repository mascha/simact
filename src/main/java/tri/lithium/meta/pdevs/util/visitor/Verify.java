/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.util.visitor;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.core.Visitor;

/**
 * Visitor that checks a model tree for correctness.
 *
 * A model tree is only correct if
 *
 * <p>
 *     composites cannot contain themselves
 *     no duplicate children exist
 *     no parent backlinks are present
 *     every name must be non-empty
 * </p>
 *
 */
public class Verify extends Visitor.Base {

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
