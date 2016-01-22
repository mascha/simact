/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.sim.api;

/**
 * Parameter map for binding and initializing parameters.
 */
public interface ParameterMap {
    ParameterMap put(String parameter, Object value);
    Object get(String parameter);
    void fork(ParameterMap parameterMap);
}
