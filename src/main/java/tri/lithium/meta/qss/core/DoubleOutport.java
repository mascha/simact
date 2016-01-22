/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.meta.qss.core;

import tri.lithium.meta.pdevs.api.IOutport;
import tri.lithium.meta.pdevs.api.IPort;

import java.util.ArrayList;
import java.util.List;

public class DoubleOutport implements IOutport<Double> {

    int currentPort = -1;

    List<IPort<Double>> objectPorts;

    DoubleInport[] primitivePorts = new DoubleInport[2];

    protected String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void post(Double data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRemotePort(IPort<Double> port) {
        if (port instanceof DoubleInport) {
            currentPort++;
            if (currentPort >= primitivePorts.length) realloc();
            primitivePorts[currentPort] = (DoubleInport) port;
        } else {
            if (objectPorts == null)
                objectPorts = new ArrayList<IPort<Double>>(1);
            objectPorts.add(port);
        }
    }

    private void realloc() {
        DoubleInport[] ports = new DoubleInport[primitivePorts.length * 2];
        System.arraycopy(primitivePorts, 0, ports, 0, primitivePorts.length);
        primitivePorts = ports;
    }

    @Override
    public void send(Double data) {
        for (int i = 0; i < objectPorts.size(); i++) {
            objectPorts.get(i).post(data);
        }
    }

    @Override
    public final void sendPrimitive(final double data) {
        for (int i = 0; i <= currentPort; i++) {
            primitivePorts[i].postFast(data);
        }
        if (objectPorts != null) send(data);
    }

    @Override
    public final void sendPrimitive(final double val1, final double val2) {
        for (int i = 0; i <= currentPort; i++) {
            primitivePorts[i].postFast(val2, val1);
        }

        if (objectPorts != null) send(val1);
    }

    @Override
    public final void sendPrimitive(final double val1, final double val2, final double val3) {
        for (int i = 0; i <= currentPort; i++) {
            primitivePorts[i].postFast(val3, val2, val1);
        }
        if (objectPorts != null) send(val1);
    }

    @Override
    public List<IPort<Double>> getRemotePorts() {
        List<IPort<Double>> list = new ArrayList<>();

        for(DoubleInport port : primitivePorts) {
            list.add(port);
        }

        if (objectPorts != null)
            list.addAll(objectPorts);

        return list;
    }


    public static DoubleOutport Double(Block hostModel) {
        DoubleOutport out = new DoubleOutport();
        hostModel.setOutport(out);
        return out;
    }

    public static DoubleOutport Double(String name, Block hostModel) {
        DoubleOutport out = new DoubleOutport();
        out.name = name;
        hostModel.setOutport(out);
        return out;
    }

    DoubleOutport() {}
}
