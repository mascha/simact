package tri.lithium.meta.pdevs.api;

import java.lang.annotation.*;

/**
 * Indicates that this is a state variable.
 *
 * Other names could be phase or mode.
 *
 * States are central to the {@link tri.lithium.meta.pdevs.ParallelDEVS} formalism:
 * The states span the state space and in combination with transitions and
 * an input trajectory uniquely determine the behaviour
 * of the model. States can be modal, e.g. partition the
 * state space, discrete, continuous, primitive or complex.
 *
 * States may only be updated during internal, confluent and
 * external transitions. Otherwise the simulator cannot guarantee
 * the correctness of the operational behaviour and hard-to-findChild
 * bugs might arise.
 *
 * {@link Mode} and {@link Variable} are convenience annotations
 * for special cases of state types.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited @Documented
public @interface State {
    /**
     * The name of the state.
     * @return the name of the state.
     */
    String value() default "";

    /**
     * An abbreviation or short name of the state.
     * @return the short name.
     */
    String terse() default "";

    /**
     * The type of the state. States with no type will
     * be determined by their java types:
     *
     *   - double, float will be mapped to continuous,
     *   - long, int, short, boolean to discrete,
     *   - objects to complex,
     *   - enums, boolean to modal,
     *
     * @return the type of the state.
     */
    StateType type() default StateType.DISCRETE;
}
