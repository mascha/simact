/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.meta.pdevs.core;

import tri.lithium.meta.pdevs.api.IInport;
import tri.lithium.meta.pdevs.api.IOutport;
import tri.lithium.meta.qss.core.DoubleInport;
import tri.lithium.meta.qss.core.DoubleOutport;

import java.util.Objects;

/**
 * Link helper class.
 */
public class Link {
    public static void connect(DoubleOutport outport, DoubleInport inport) {
        Objects.requireNonNull(inport,  "Could not connect because target port was null");
        Objects.requireNonNull(outport, "Could not connect because source port was null");
        outport.addRemotePort(inport);
    }

    public static void connect(IOutport outport, IInport inport) {
        Objects.requireNonNull(inport ,"Could not connect because target port was null");
        Objects.requireNonNull(outport,"Could not connect because source port was null");
        outport.addRemotePort(inport);
    }

    public static void connect(IOutport outport, Composite.Inport inport) {
        Objects.requireNonNull(inport ,"Could not connect because target port was null");
        Objects.requireNonNull(outport,"Could not connect because source port was null");
        outport.addRemotePort(inport);
    }

    public static void connect(Composite.Inport input, Atomic.Inport input2) {
        Objects.requireNonNull(input2 , "Could not connect because target port was null");
        Objects.requireNonNull(input, "Could not connect because source port was null");
        input.addRemotePort(input2);
    }

    public static void connect(Atomic.Outport output, Composite.Outport output2) {
        Objects.requireNonNull(output2 , "Could not connect because target port was null");
        Objects.requireNonNull(output, "Could not connect because source port was null");
        output.addRemotePort(output2);
    }

    public static void connect(DoubleOutport primitivePort, Composite.Outport outport) {
        Objects.requireNonNull(outport , "Could not connect because target port was null");
        Objects.requireNonNull(primitivePort, "Could not connect because source port was null");
        primitivePort.addRemotePort(outport);
    }

    public static void connect(Composite.Inport inport, Composite.Outport<?> outport) {
        Objects.requireNonNull(outport , "Could not connect because target port was null");
        Objects.requireNonNull(inport, "Could not connect because source port was null");
        inport.addRemotePort(outport);
    }

    public static void connect(Composite.Inport source, Composite.Inport target) {
        Objects.requireNonNull(source , "Could not connect because target port was null");
        Objects.requireNonNull(target, "Could not connect because source port was null");
        source.addRemotePort(target);
    }

    public static void connect(Composite.Inport<Double> inport, DoubleInport target) {
        Objects.requireNonNull(target , "Could not connect because target port was null");
        Objects.requireNonNull(inport, "Could not connect because source port was null");
        inport.addRemotePort(target);
    }

}
