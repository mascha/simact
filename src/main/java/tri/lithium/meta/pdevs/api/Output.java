package tri.lithium.meta.pdevs.api;

import java.lang.annotation.*;

/**
 * Marks this field as an output port, whose
 * implementation will be injected at runtime.
 *
 * Annotated ports will automatically be added to
 * the outport list of the model.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
@Inherited @Documented
public @interface Output {
    Class<?> type() default double.class;
    String value() default "";
    boolean primitive() default true;
}
