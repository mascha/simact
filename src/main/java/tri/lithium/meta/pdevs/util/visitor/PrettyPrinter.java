/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.util.visitor;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.core.Visitor;

import java.util.Objects;

/**
 * Pretty print the model tree.
 */
public class PrettyPrinter extends Visitor.Base {

    private StringBuilder builder;
    private int level;
    private String indent = "";

    public PrettyPrinter(Composite root) {
        builder = new StringBuilder(1000);
        this.visitComposite(root);
    }

    protected void increaseIndent() {
        indent = indent + "\t";
    }

    protected void decreaseIndent() {
        if (level > 0)
            indent = indent.substring(0, (level - 1));
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    protected void enterComposite(Composite composite) {
        builder
                .append(indent)
                .append("+ ")
                .append(composite.getName().toUpperCase())
                .append(" (").append(composite.getChildren().size()).append(")")
                .append("\n");

        increaseIndent();

        for (int i = 0; i < composite.getOutports().size(); i++) {
            builder.append(indent).append("o ").append(composite.getOutports().get(i).getName()).append("\n");
        }

        for (int i = 0; i < composite.getInports().size(); i++) {
            builder.append(indent).append("* ").append(composite.getInports().get(i).getName()).append("\n");
        }

        level++;
    }

    @Override
    public void visitAtomic(Atomic atomic) {
        builder
                .append(indent)
                .append("- ")
                .append(atomic.getName())
                .append("\n");
    }

    @Override
    protected void leaveComposite(Composite composite) {
        decreaseIndent();
        level--;
    }


}
