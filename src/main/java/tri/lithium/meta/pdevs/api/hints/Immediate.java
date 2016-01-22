package tri.lithium.meta.pdevs.api.hints;

import java.lang.annotation.*;

/**
 * Indicates that this external transition function
 * directly emits output after receiving events. The model
 * thus represents a mealy state automaton.
 *
 * This is primarily an optimization hint.
 *
 * Will be ignored if used on any other method but
 * {@link tri.lithium.meta.pdevs.core.Atomic}
 *
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited @Documented
public @interface Immediate {
}
