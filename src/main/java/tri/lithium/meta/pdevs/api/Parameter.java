package tri.lithium.meta.pdevs.api;

import java.lang.annotation.*;

/**
 * Indicates that this field is a constant.
 *
 * Parameters are set once in a simulation run and then
 * stay constant thereafter. This process consists
 * of two distinct phases:
 *
 * 1) During parametrization the parameters are bound to a generic model hierarchy,
 *    thus producing a parametrized model.
 *
 * 2) During initialization, the bound parameters are initialized with a concrete value
 *    for this simulation run.
 *
 *
 * Parameters can be accessed by two naming pattern
 *
 * a) The actual path in the model, the entity name and the constant group and name where
 *    the grouping can be neglected.
 *
 *    /path/to/entity.group.name = value
 *
 *    /Market/Firm A/Production/Conveyor.queue.size = 20
 *
 * b) The uuid of the object, the entity name and the constant group and name where
 *    the grouping can be left out.
 *
 *    uuid.group.name
 *
 *    6ba7b810-9dad-11d1-80b4-00c04fd430c8.queue.size = 20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Inherited @Documented
public @interface Parameter {
    /**
     * Return the constant group. If group is empty, it will be ignored.
     * @return the constant group
     */
    String group() default "";

    /**
     * Return the constant name. If name is empty, the name of the field will be used.
     * @return the constant name
     */
    String value() default "";
}
