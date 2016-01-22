/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.meta.pdevs.core;

/**
 * Entity class implementation.
 */
public abstract class Entity {
    /**
     * The name of the agent
     */
    private String name;

    /**
     * Get the agent's local name
     * @return Local name of the agent
     */
    public final String getName() {
        return name;
    }

    /**
     * Set the agent's name
     * @param name Local name of the agent
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieve the full qualified name
     */
    public final String getFullName() {
        return parent != null ? parent.getFullName() + "/" + name : name;
    }

    /**
     * The current parent of the model
     */
    private Composite parent;

    /**
     * Retrieve the parent of the agent
     * @return Parent of the agent
     */
    public final Composite getParent() {
        return parent;
    }

    /**
     * Set the parent of the agent
     * @param parent New parent, not null
     */
    public final void setParent(Composite parent) {
        if (this.parent != parent) this.parent = parent;
    }

    /**
     * Visitor support.
     */
    abstract void accept(Visitor visitor);

}
