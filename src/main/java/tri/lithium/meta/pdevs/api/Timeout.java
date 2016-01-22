package tri.lithium.meta.pdevs.api;

import java.lang.annotation.*;

/**
 * Marks this variable as the timeout value of the model.
 *
 * This variable will usually be a real variable called sigma,
 * which marks the next point in time where the model will undergo
 * an internal (or, in the case of incoming event, confluent)
 * transition which then updates the state of the model.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented @Inherited
public @interface Timeout {
}
