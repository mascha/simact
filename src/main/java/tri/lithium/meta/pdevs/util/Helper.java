/*
 * Copyright (C) Martin Schade 2015. All rights reserved. No commercial and non-commerical usage.
 */

package tri.lithium.meta.pdevs.util;

import tri.lithium.meta.pdevs.api.IOutport;
import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.core.Link;
import tri.lithium.meta.pdevs.library.sinks.Counter;
import tri.lithium.meta.pdevs.library.sinks.Sink;
import tri.lithium.meta.pdevs.library.transduce.Average;

public class Helper {
    public static void plot(Composite composite, Atomic.Outport outport, String patients) {
        Sink sink = new Sink(patients != null ? patients : outport.getHost().getName());
        Link.connect(outport, sink.input);
        composite.add(sink);
    }

    public static void plotAverage(Composite composite, IOutport<?> outport) {
        Sink sink = new Sink(outport.getName());
        Average avg = new Average("Average " + outport.getName());
        Link.connect(outport, avg.input);
        Link.connect(avg.average, sink.input);
        composite.add(avg);
        composite.add(sink);
    }

    public static void plotAverage(Composite composite, IOutport<?> outport, String name) {
        Sink sink = new Sink(name);
        Average avg = new Average("Average " + outport.getName());
        Link.connect(outport, avg.input);
        Link.connect(avg.average, sink.input);
        composite.add(avg);
        composite.add(sink);
    }

    public static void plotTotal(Composite composite, Composite.Outport outport) {
        Sink sink = new Sink(outport.getName());
        Counter cnt = new Counter("Total " + outport.getName());
        Link.connect(outport, cnt.input);
        Link.connect(cnt.count, sink.input);
        composite.add(cnt);
        composite.add(sink);
    }

    public static void plotTotal(Composite composite, Atomic.Outport outport, String plotName) {
        Sink sink = new Sink(plotName);
        Counter cnt = new Counter("Total " + outport.getName());
        Link.connect(outport, cnt.input);
        Link.connect(cnt.count, sink.input);
        composite.add(cnt);
        composite.add(sink);
    }

}
