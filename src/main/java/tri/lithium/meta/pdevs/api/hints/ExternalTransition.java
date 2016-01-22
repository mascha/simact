package tri.lithium.meta.pdevs.api.hints;

import java.lang.annotation.*;

/**
 * Marks this function as the external transition function.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited @Documented
public @interface ExternalTransition {
}
