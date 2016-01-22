package tri.lithium.meta.pdevs.api;

/**
 * Port interface.
 */
public interface IPort<T> {
    String getName();
    void setName(String name);
    void post(T data);
}
