package tri.lithium.sim.api;

public enum StoppingCondition {
    /**
     * The simulation time has reached the
     * terminating value.
     */
    FINAL_TIME,

    /**
     * The simulator has done the maximum
     * number of iterations allowed.
     */
    MAXIMUM_ITERATION,

    /**
     * The value of a statistical metric has
     * reached a terminating condition.
     */
    STATISTIC,

    /**
     * A steady state condition has been detected.
     */
    STEADY_STATE,

    /**
     * An error has occurred during simulation.
     */
    ERROR
}