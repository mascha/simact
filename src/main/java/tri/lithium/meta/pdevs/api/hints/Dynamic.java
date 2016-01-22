package tri.lithium.meta.pdevs.api.hints;

import java.lang.annotation.*;

/**
 * The annotation type {@code Dynamic} is used to indicate
 * that this composite model uses variable structure methods.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited @Documented
public @interface Dynamic {
}
