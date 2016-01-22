package tri.lithium.meta.qss.util;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.qss.core.DoubleInport;
import tri.lithium.meta.qss.core.DoubleSink;
import tri.lithium.meta.qss.core.Integrator;
import tri.lithium.meta.qss.core.Function;

import tri.lithium.meta.pdevs.core.Link;

/**
 * Helper class for connecting ports
 */
public class QSSLink {
    public static boolean  hasVariable(String variable, Function target) {
        return target.getEquation().getVariables().contains(variable);
    }

    public static boolean check(Function target, Atomic source) {
        if (!hasVariable(source.getName(), target))
            throw  new RuntimeException(source.getName() + " -> " + target.getName() + "::" + source.getName() + " not found");
        return true;
    }

    public static void connect(Function source, Function target) {
        check(target,source);
        DoubleInport port = target.getInput(source.getName());
        Link.connect(source.output, port);
    }

    public static void  biconnect(Function integrator, Integrator derivative) {
        connect(integrator, derivative);
        connect(derivative, integrator);
    }

    public static void connect(Integrator integrator, Function function) {
        check(function,integrator);
        Link.connect(integrator.state, function.getInput(integrator.getName()));
    }

    public static void connect(Function function, Integrator integrator) {
        Link.connect(function.output, integrator.input);
    }

    public static void connect(Function source, Function... targets) {
        for (int i = 0; i < targets.length; i++) {
            connect(source, targets[i]);
        }
    }

    public static void connect(Integrator integrator, Function... function) {
        for (int i = 0; i < function.length; i++) {
            connect(integrator, function[i]);
        }
    }

    public static void connect(Function function, DoubleSink sink) {
        Link.connect(function.output, sink.input);
    }

    public static void connect(Integrator function, DoubleSink sink) {
        Link.connect(function.state, sink.input);
    }
}
