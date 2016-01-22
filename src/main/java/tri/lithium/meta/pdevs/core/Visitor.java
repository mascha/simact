/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.meta.pdevs.core;

/**
 * Abstract visitor interface which can be used
 * to traverse the model tree.
 */
public interface Visitor {

    /**
     * Visiting a composite model.
     * @param composite
     *      The composite model to be visited.
     */
    void visitComposite(Composite composite);

    /**
     * Visiting a single atomic model.
     * @param atomic
     *      The atomic model to be visited.
     */
    void visitAtomic(Atomic atomic);
    
    /**
     * Base visitor implementation, which hides
     * implementation details and reduces boilerplate
     * code. All visitor must derive from this base class.
     */
    public static abstract class Base implements Visitor {

        protected void enterComposite(Composite composite) {}
        protected void leaveComposite(Composite composite) {}

        @Override
        public final void visitComposite(Composite composite) {
            if (composite == null) {
                System.err.println("Warning: Visitor cannot visit null reference");
            }

            enterComposite(composite);

            for (int i = 0; i < composite.getChildren().size(); i++)
                composite.getChildren().get(i).accept(this);

            leaveComposite(composite);
        }
    }
}