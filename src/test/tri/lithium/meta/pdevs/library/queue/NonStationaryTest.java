/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.queue;

import org.junit.Test;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.core.Link;
import tri.lithium.meta.pdevs.library.sources.Arrival;
import tri.lithium.meta.pdevs.library.transduce.Average;
import tri.lithium.meta.pdevs.library.sinks.Sink;
import tri.lithium.sim.core.util.Charts;
import tri.lithium.sim.core.util.Simulations;

public class NonStationaryTest  {

    @Test
    public void testNonstationaryArrival() throws InterruptedException {

        Arrival.IntensityFunction intensity = time -> {
            //double time = currentTime % 24.0;
            if (time < 4) return 4.0;
            else if (time < 8.5) return 2.0;
            else if (time < 11) return 20;
            else if (time < 20) return 10;
            else return 7.5;
        };

        Composite root = new Composite("Root");

        for (int i = 0; i < 10; i++) {
            Arrival<Object> arrival = new Arrival.NonStationary<>("Arrival", (time) -> new Object(), intensity, 20.0);

            Average counter = new Average("Counter", 1);

            Sink sink = new Sink("Plot Arrivals " + i);

            Link.connect(arrival.output, counter.input);
            Link.connect(counter.average, sink.input);

            root.add(arrival, counter, sink);
        }

        Simulations.create(root, 24).run();

        Charts.plotAll(root);

        Thread.sleep(50000);

    }
}