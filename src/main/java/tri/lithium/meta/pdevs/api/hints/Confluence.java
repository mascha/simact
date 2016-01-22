package tri.lithium.meta.pdevs.api.hints;

import java.lang.annotation.*;

/**
 * The {@link Confluence} annotations indicates the resolving behaviour
 * of the handler method in the case of simultaneous events.
 *
 * This might be used to generate custom accessor objects.
 *
 * Ignored if used on any other method than onConflict.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Confluence {
    /**
     * Return the confluent resolver strategy.
     * @return the confluent resolver strategy.
     */
    ConfluenceType value() default ConfluenceType.CUSTOM;
}
