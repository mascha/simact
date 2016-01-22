package tri.lithium.meta.pdevs.api;

/**
 * Input port interface.
 */
public interface IInport<T> extends IPort<T> {
    boolean hasInputs();
    void post(T t);
    void postFast(double d);
    void postFast(double d, double d2);
    void postFast(double d, double d2, double d3);

    /**
     * Receive a single item from the port.
     * If the port is empty it returns null.
     * @return
     */
    T receive();
}
