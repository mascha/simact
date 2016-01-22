package tri.lithium.meta.pdevs.api;

import java.lang.annotation.*;

/**
 * Indicates that this field represents a mode or phase variable.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited @Documented
public @interface Mode {
    String[] value();
}
