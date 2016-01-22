package tri.lithium.sim.core.util;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.core.Entity;
import tri.lithium.meta.pdevs.core.Visitor;
import tri.lithium.meta.pdevs.util.visitor.Initializer;
import tri.lithium.meta.pdevs.util.visitor.NameFilter;
import tri.lithium.sim.api.Monitor;
import tri.lithium.sim.api.Simulator;
import tri.lithium.sim.core.monitor.DebugMonitor;
import tri.lithium.sim.core.monitor.ProfilingMonitor;
import tri.lithium.sim.core.simulator.FastSimulator;

/**
 * Simulation run scaffolding.
 */
public class Simulations {

    public static Runnable profile(Composite root, double time) {
        final Simulator simulator = new FastSimulator();
        Initializer visitor = new Initializer(simulator);

        visitor.visitComposite(root);

        simulator.useEvents(visitor.getModelList());
        simulator.setEndTime(time);

        return new Runnable() {
            @Override
            public void run() {
                Monitor profile = new ProfilingMonitor();
                simulator.runSimulation(profile);
                System.out.println(profile);
            }
        };
    }

    public static Runnable debug(Composite model, double time) {
        final Simulator simulator = new FastSimulator();
        Initializer visitor = new Initializer(simulator);

        visitor.visitComposite(model);

        simulator.useEvents(visitor.getModelList());
        simulator.setEndTime(time);

        return new Runnable() {
            @Override
            public void run() {
                Monitor profile = new DebugMonitor();
                simulator.runSimulation(profile);
                System.out.println(profile);
            }
        };
    }

    public static Runnable create(Composite root, double time) {
        final Simulator simulator = new FastSimulator();
        Initializer visitor = new Initializer(simulator);

        visitor.visitComposite(root);

        simulator.useEvents(visitor.getModelList());
        simulator.setEndTime(time);

        return new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                simulator.runSimulation(null);
                System.out.println("Run took "+ (System.currentTimeMillis() - now) + " ms");
            }
        };
    }

    public static Entity findChild(String name, Composite composite) {
        NameFilter visitor = new NameFilter(name);
        visitor.visitComposite(composite);
        return visitor.getFirstFound();
    }

    /**
     *
     */
    public static void finalize(Composite c) {
        Visitor finalizer = new Visitor.Base() {
            public void visitAtomic(Atomic atomic) {
                atomic.exitSimulation();
            }
        };

        finalizer.visitComposite(c);
    }

}
