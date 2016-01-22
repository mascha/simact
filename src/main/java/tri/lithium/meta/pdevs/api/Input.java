package tri.lithium.meta.pdevs.api;

import tri.lithium.meta.pdevs.api.hints.InputHandling;

import java.lang.annotation.*;

/**
 * Marks this field as an output port, whose
 * implementation will be injected at runtime
 *
 * Field must implement input interface.
 *
 * Annotated ports will automatically be added to
 * the input list of the model.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited @Documented
public @interface Input {
    Class<?> type() default double.class;
    String value() default "";
    boolean primitive() default true;
    InputHandling use() default InputHandling.ALL;
}
