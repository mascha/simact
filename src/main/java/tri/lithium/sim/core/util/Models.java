package tri.lithium.sim.core.util;

import tri.lithium.meta.qss.core.DoubleSink;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.util.visitor.Dotify;
import tri.lithium.meta.pdevs.util.visitor.PrettyPrinter;
import tri.lithium.meta.qss.core.Function;
import tri.lithium.meta.qss.core.Integrator;

/**
 * Model helper class.
 */
public class Models {
    public static void prettyPrint(Composite root) {

        System.out.println(new PrettyPrinter(root));
    }

    public static void dotify(Composite root) {
        Dotify dot = new Dotify();

        dot.setDefaultNodeStyle("style=filled,fontname=\"Arial\",colorscheme=paired12, fontsize=11");

        dot.setNodeStyleForClass(Function.class, ",shape=circle, fillcolor=3");
        dot.setNodeStyleForClass(DoubleSink.class, ",shape=Mdiamond, fillcolor=7");
        dot.setNodeStyleForClass(Integrator.class, ",style=\"rounded,filled\",shape=box, fillcolor=2");

        dot.visitComposite(root);
        System.out.println(dot);
    }
}
