package tri.lithium.meta.pdevs.library.queue;

import org.junit.Test;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.core.Link;
import tri.lithium.meta.qss.core.DoubleSink;
import tri.lithium.meta.pdevs.library.sources.Step;
import tri.lithium.sim.core.util.Simulations;

/**
 */
public class DelayTest {

    @Test
    public void testDelay() {
        Composite root = new Composite("Composite");

        Delay<Object> deterministicDelay = new Delay<Object>("TestDelay", 10);

        Step step = new Step("TestInput", 10, 10);

        DoubleSink inputPlot = new DoubleSink("Step");
        DoubleSink delayPlot = new DoubleSink("Delay");

        Link.connect(step.output, inputPlot.input);

        //Link.connect(delay.delayOutput, delayPlot.input);

        root.add(deterministicDelay);

        Simulations.profile(root, 25).run();
    }

}