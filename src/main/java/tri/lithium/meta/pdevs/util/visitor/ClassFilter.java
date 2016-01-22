/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.util.visitor;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.core.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor that finds all instances of a given class type or
 * subclasses thereof.
 */
public final class ClassFilter<T> extends Visitor.Base {

    public void setClazz(Class<? extends T> clazz) {
        this.clazz = clazz;
    }

    private Class<? extends T> clazz;
    private final boolean subClasses;

    public List<T> getFoundInstances() {
        return instances;
    }

    public List<T> instances;

    public void visitAtomic(Atomic atomic) {
        if (subClasses) {
            if (atomic.getClass().isAssignableFrom(clazz))
                instances.add((T) atomic);
        } else {
            if (atomic.getClass().equals(clazz))
                instances.add((T) atomic);
        }
    }

    public ClassFilter(Class<T> clazz, boolean subClasses) {
        this.clazz = clazz;
        this.subClasses = subClasses;
        instances = new ArrayList<T>(16);
    }

    public ClassFilter(Class<T> clazz) {
        this(clazz, false);
    }

    public static <T> ClassFilter<T> create(Class<T> clazz) {
        return new ClassFilter<T>(clazz);
    }

    public static<T> ClassFilter<T> create(Class<T> clazz, boolean findSubclasses) {
        return new ClassFilter<T>(clazz, findSubclasses);
    }
}
