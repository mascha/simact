package tri.lithium.meta.pdevs.library.sources;

import org.junit.After;
import org.junit.Test;
import tri.lithium.meta.qss.core.DoubleSink;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.core.Link;
import tri.lithium.meta.pdevs.library.queue.Delay;
import tri.lithium.meta.pdevs.library.sinks.Sink;
import tri.lithium.sim.core.util.Charts;
import tri.lithium.sim.core.util.Simulations;

public class GeneratorTest {

    @Test
    public void testPulseVisually() {
        Composite root = new Composite("root");

        Pulse pulse = new Pulse("Pulse", true, 10, 0, 1);
        DoubleSink sink = new DoubleSink("plot(Pulse)");

        Link.connect(pulse.output, sink.input);

        root.add(pulse);
        root.add(sink);

        Simulations.profile(root, 10).run();
        Charts.plotAll(root);
    }

    @Test
    public void testRampVisually() {
        Composite root = new Composite("root");

        PeriodicRamp ramp = new PeriodicRamp("Ramp", 7, 21, 0);
        DoubleSink sink   = new DoubleSink("plot(Pulse)");

        Link.connect(ramp.output, sink.input);

        root.add(ramp);
        root.add(sink);

        Simulations.profile(root, 7 * 24).run();
        Charts.plotAll(root);
    }


    @Test
    public void testDelayedPulseVisually() {
        Composite root = new Composite("root");

        Pulse pulse = new Pulse("Pulse", true, 10, 0, 10);
        Sink  sink  = new Sink("plot(Pulse)");

        Delay<Double> delay = new Delay<>("delay", 5);

        Link.connect(pulse.output, delay.input);
        Link.connect(delay.output, sink.input);

        root.add(pulse, sink, delay);

        Simulations.debug(root, 40).run();
        Charts.plotAll(root);
    }

    @Test
    public void testSawVisually() {
        Composite root  = new Composite("root");
        Saw pulse       = new Saw("Pulse", true, 10, 0, 1);
        DoubleSink sink = new DoubleSink("plot(Pulse)");

        Link.connect(pulse.output, sink.input);

        root.add(pulse);
        root.add(sink);

        Simulations.debug(root, 10).run();
        Charts.plotAll(root);
    }

    @Test
    public void testStepVisually() {
        Composite root = new Composite("root");

        Step pulse = new Step("Step", 10, 5);
        Sink sink  = new Sink("plot(Step)");

        Link.connect(pulse.output, sink.input);

        root.add(pulse);
        root.add(sink);

        Simulations.profile(root, 10).run();
        Charts.plotAll(root);
    }


    @After
    public void after() throws InterruptedException {
        Thread.sleep(20000);
    }

}