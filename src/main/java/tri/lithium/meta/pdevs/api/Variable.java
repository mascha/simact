package tri.lithium.meta.pdevs.api;

import java.lang.annotation.*;

/**
 * Indicates that this is a variable field.
 *
 * Variables might be secondary fields or secondary
 * states, which are used as temporary variables and are
 * of small interest for an external view of the model.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited @Documented
public @interface Variable {
    String value() default "";
    StateType type() default StateType.CONTINUOUS;
}
