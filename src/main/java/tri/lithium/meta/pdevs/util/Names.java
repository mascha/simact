/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.util;

import tri.lithium.meta.pdevs.core.Entity;

/**
 * Naming utility class
 */
public class Names {

    /**
     * Build a constant string from the supplied input parameters
     *
     * E.g for a queue, you might want to measure the queue length
     * so you can create a constant by
     *
     * @param entity
     *      The full name and path of the entity to be considered
     * @param block
     *      An arbitrary name of the constant block, e.g for grouping of parameters
     * @param parameter
     *      The name of the concrete constant to be configured. There needs to be
     *      a field entity.constant annotated with @Parameter
     * @return
     *      Return a constant ID "root/full/path/to/entity.block.constant"
     */
    public static String parameter(Entity entity, String block, String parameter) {
        return entity.getFullName() + "." + block + "." + parameter;
    }

    /**
     * Build a constant string from the supplied input parameters
     *
     * @param entity
     *      The entity to be considered
     * @param parameter
     *      The name of the concrete constant to be configured. There needs to be
     *      a field entity.constant annotated with @Parameter
     * @return
     *      Return a constant ID "root/full/path/to/entity.constant"
     */
    public static String parameter(Entity entity, String parameter) {
        return entity.getFullName() + "." + parameter;
    }
}
