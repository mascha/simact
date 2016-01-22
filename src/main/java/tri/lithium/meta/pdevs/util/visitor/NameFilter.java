/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.util.visitor;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.core.Entity;
import tri.lithium.meta.pdevs.core.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor class for finding all entities with a given name.
 */
public class NameFilter extends Visitor.Base {
    private final String name;
    private final String[] names;
    private List<Entity> foundEntities = new ArrayList<Entity>();

    public List<Entity> getFoundEntities() {
        return foundEntities;
    }

    public Entity getFirstFound() {
        return foundEntities.isEmpty() ? null : foundEntities.get(0);
    }

    @Override
    public void visitAtomic(Atomic atomic) {
        if (name != null) {
           if (name.equals(atomic.getName()))
               foundEntities.add(atomic);
        } else {
            for (int i = 0; i < names.length; i++) {
                if (hasName(atomic)) foundEntities.add(atomic);
            }
        }
    }

    private boolean hasName(Entity entity) {
        for (String name : names)
            if (name.equals(entity.getName()))
                return true;
        return false;
    }

    public NameFilter(String childName) {
        this.name  = childName;
        this.names = null;
    }

    public NameFilter(String... childrenNames) {
        this.names = childrenNames;
        this.name = null;
    }
}
