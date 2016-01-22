/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.branch;

import org.junit.Test;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.core.Link;
import tri.lithium.meta.pdevs.library.queue.Delay;
import tri.lithium.meta.pdevs.library.queue.Join;
import tri.lithium.meta.pdevs.library.queue.Queue;
import tri.lithium.meta.pdevs.library.sinks.Counter;
import tri.lithium.meta.pdevs.library.sources.Arrival;
import tri.lithium.sim.core.util.Simulations;

public class CoordinatorTest {

    @Test
    public void testMultipleInAtTheSameTime() {
        Arrival<Object> generatorA = new Arrival<>("Test generator A", Object.class, 10);
        Arrival<Object> generatorB = new Arrival<>("Test generator B", Object.class, 10);

        Queue<Object> queueA = new Queue<>("Queue A");
        Queue<Object> queueB = new Queue<>("Queue B");

        Join<Object> coord = new Join<>("Coordinator");

        Delay<Object> delay = new Delay<Object>("Processor", 4);

        Counter counter = new Counter("Counter");

        Link.connect(generatorA.output, queueA.input);
        Link.connect(generatorB.output, queueB.input);

        queueA.addTarget(coord);
        queueB.addTarget(coord);
        coord.addSource(queueA);
        coord.addSource(queueB);

        Link.connect(coord.output, delay.input);

        Link.connect(delay.output, counter.input);

        Composite root = new Composite("TestMultiple",
                queueA, queueB, generatorA, generatorB, delay, coord, counter
        );

        Simulations.debug(root, 20).run();

    }

    @Test
    public void testMultipleDifferentTimes() {
        Arrival<Object> generatorA = new Arrival<>("Test generator A", Object.class, 10);
        Arrival<Object> generatorB = new Arrival<>("Test generator B", Object.class, 15);

        Queue<Object> queueA = new Queue<>("Queue A");
        Queue<Object> queueB = new Queue<>("Queue B");

        Join<Object> coord   = new Join<>("Coordinator");

        Delay<Object> delay  = new Delay<Object>("Processor", 4);

        Counter counter      = new Counter("Counter");

        Link.connect(generatorA.output, queueA.input);
        Link.connect(generatorB.output, queueB.input);

        queueA.addTarget(coord);
        queueB.addTarget(coord);
        coord.addSource(queueA);
        coord.addSource(queueB);

        Link.connect(coord.output, delay.input);

        Link.connect(delay.output, counter.input);

        Composite root = new Composite("TestMultiple",
                queueA, queueB, generatorA, generatorB, delay, coord, counter
        );

        Simulations.debug(root, 40).run();

    }

    @Test
    public void testMultipleDifferentTimesWithConflict() {
        Arrival<Object> generatorA = new Arrival<>("Test generator A", Object.class, 10);
        Arrival<Object> generatorB = new Arrival<>("Test generator B", Object.class, 15);

        Queue<Object> queueA  = new Queue<>("Queue A");
        Queue<Object> queueB  = new Queue<>("Queue B");

        Join<Object> coordA   = new Join<>("Coordinator A");
        Join<Object> coordB   = new Join<>("Coordinator B");

        Counter counterA      = new Counter("Counter A");
        Counter counterB      = new Counter("Counter B");

        Link.connect(generatorA.output, queueA.input);
        Link.connect(generatorB.output, queueB.input);

        queueA.addTarget(coordA);
        queueB.addTarget(coordA);
        queueA.addTarget(coordB);
        queueB.addTarget(coordB);
        coordA.addSource(queueA);
        coordA.addSource(queueB);
        coordB.addSource(queueA);
        coordB.addSource(queueB);

        Link.connect(coordA.output, counterA.input);
        Link.connect(coordB.output, counterB.input);


        Composite root = new Composite("TestMultiple",
                queueA, queueB, generatorA, generatorB, counterA, counterB, coordA, coordB
        );

        Simulations.profile(root, 200).run();

    }

    @Test
    public void testConflict() {
        Arrival<Object> generatorA = new Arrival<>("Test generator A", Object.class, 10);

        Queue<Object> queueA = new Queue<>("Queue A");

        Join<Object> coordA = new Join<>("Coordinator A");
        Join<Object> coordB = new Join<>("Coordinator B");

        Delay<Object> delayA = new Delay<Object>("DelayA", 4);
        Delay<Object> delayB = new Delay<Object>("DelayB", 4);

        Counter counterA = new Counter("Counter A");
        Counter counterB = new Counter("Counter B");

        Link.connect(generatorA.output, queueA.input);

        queueA.addTarget(coordA);
        queueA.addTarget(coordB);
        coordA.addSource(queueA);
        coordB.addSource(queueA);

        Link.connect(coordA.output, delayA.input);
        Link.connect(coordB.output, delayB.input);

        Link.connect(delayA.output, counterA.input);
        Link.connect(delayB.output, counterB.input);

        Composite root = new Composite("TestMultiple",
                queueA, generatorA, delayA, delayB, coordA, coordB, counterA, counterB
        );

        Simulations.debug(root, 30).run();

    }


    @Test
    public void testSimple() {
        Arrival<Object> generatorA = new Arrival<>("Test generator A", Object.class, 10);

        Queue<Object> queueA = new Queue<>("Queue A");

        Join<Object> joinA = new Join<>("Coordinator");

        Counter counterA = new Counter("Counter A");

        Link.connect(generatorA.output, queueA.input);

        queueA.addTarget(joinA);
        joinA.addSource(queueA);

        Link.connect(joinA.output, counterA.input);

        Composite root = new Composite("TestMultiple",
                queueA, generatorA, joinA, counterA
        );

        Simulations.profile(root, 200).run();

    }
}