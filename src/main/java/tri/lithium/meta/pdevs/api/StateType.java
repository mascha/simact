package tri.lithium.meta.pdevs.api;

/**
 * Indicates which type this state represents.
 *
 * This might be useful for visualization purposes,
 * because on the lowest behaviour level variables
 * are just time-value pairs which might be represented
 * using different plotting and simplification algorithms
 *
 */
public enum StateType {
    /**
     * Discrete states.
     */
    DISCRETE,

    /**
     * Continuous state variables can assume infinitely many .
     * intermediate values
     */
    CONTINUOUS,

    /**
     * Modal states for state partitioning.
     */
    MODAL,

    /**
     * Complex states usually are objects, lists or
     * non-primitive data types.
     */
    COMPLEX,

    /**
     * Use if no special treatment is necessary.
     *
     * Possible algorithms will be derived from the
     */
    NO_TYPE
}
