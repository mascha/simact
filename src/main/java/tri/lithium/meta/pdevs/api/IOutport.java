package tri.lithium.meta.pdevs.api;

import tri.lithium.meta.pdevs.core.Port;

import java.util.List;

/**
 * Outport interface.
 */
public interface IOutport<T> extends IPort<T> {
    void send(T t);
    void sendPrimitive(double d);
    void sendPrimitive(double d, double d2);
    void sendPrimitive(double d, double d2, double d3);
    List<IPort<T>> getRemotePorts();
    void addRemotePort(IPort<T> port);
}
