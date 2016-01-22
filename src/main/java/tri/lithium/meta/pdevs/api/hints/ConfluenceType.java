package tri.lithium.meta.pdevs.api.hints;

/**
 * Specifies the tpe of the used conflict
 * resolution strategies within confluent transitions.
 */
public enum ConfluenceType {
    /**
     * Ignore the conflicting events.
     *
     * Appropriate for empty or abstract functions.
     */
    IGNORE,

    /**
     * Only do an internal transition.
     */
    TIMEOUT_ONLY,

    /**
     * Process only the incoming events.
     */
    INPUT_ONLY,

    /**
     * First process the incoming events, then do an internal transition.
     */
    INPUT_FIRST,

    /**
     * First do an internal transition, then process the incoming events.
     */
    TIMEOUT_FIRST,

    /**
     * Custom function which might use the external transition, the internal transition, or none.
     */
    CUSTOM
}
