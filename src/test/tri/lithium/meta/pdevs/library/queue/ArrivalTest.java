package tri.lithium.meta.pdevs.library.queue;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.junit.Test;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.core.Link;
import tri.lithium.meta.pdevs.library.sources.Arrival;
import tri.lithium.meta.pdevs.library.sinks.Counter;
import tri.lithium.meta.pdevs.library.sinks.Sink;
import tri.lithium.sim.core.util.Charts;
import tri.lithium.sim.core.util.Simulations;

import static tri.lithium.meta.pdevs.core.Link.connect;

/**
 *
 */
public class ArrivalTest {
    @Test
    public void delayRateEquiv() {

        RealDistribution dist   = new ExponentialDistribution(1/15d);
        Arrival<Object> arrival = new Arrival<>("Arrivals", Object.class, dist::sample);
        Counter counter         = new Counter("Counter");
        Sink sink               = new Sink("Plot Counter");

        Link.connect(arrival.output, counter.input);
        Link.connect(counter.count, sink.input);

        RealDistribution dist2   = new ExponentialDistribution(1/4d);
        Arrival<Object> arrival2 = new Arrival<>("Arrivals 2", Object.class, dist2::sample);
        Counter counter2         = new Counter("Counter 2");
        Sink sink2               = new Sink("Plot Counter 2");

        Link.connect(arrival2.output, counter2.input);
        Link.connect(counter2.count, sink2.input);

        Composite root = new Composite("root",
                arrival, counter, sink, arrival2, sink2, counter2
        );

        Simulations.create(root, 10).run();
        Charts.plotAll(root);
    }

}