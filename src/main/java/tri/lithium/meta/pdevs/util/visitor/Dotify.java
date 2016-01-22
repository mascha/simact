/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.util.visitor;

import tri.lithium.meta.pdevs.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
public class Dotify extends Visitor.Base {

    public static class Tuple {

        private final Class<? extends Entity> from;
        private final Class<? extends Entity> to;

        public Tuple(Class<? extends Entity> from, Class<? extends Entity> to) {
            this.from = from;
            this.to = to;
        }
    }

    private static final String DEFAULT_SHAPE = ",shape=component";
    private static final String GRAPH = "digraph G {\n";
    private static final String SUB_GRAPH = "subgraph";
    private static final String NODE  = "node";
    private static final String NODE_STYLE = "style=filled,fontname=\"Arial\",";
    private static final String END   = "}\n";
    private static final String LABEL = "label = \"";

    private Map<Entity,String> idMap = new HashMap<Entity,String>();

    private Map<Class<? extends Entity>, String> classStyleMap = new HashMap<Class<? extends Entity>, String>();

    private int level     = 0;
    private int color     = 0;
    private int atomicID  = 0;
    private String indent = "";

    private String graphOptions = "rankdir=LR,fontname=\"Arial\"";

    private String nodeStyle = NODE_STYLE;

    StringBuilder graph = new StringBuilder();

    public void setNodeStyleForClass(Class<? extends Entity> clazz, String classStyle) {
        classStyleMap.put(clazz, classStyle);
    }

    protected void increaseIndent() {
        indent = indent + "\t";
    }

    protected void decreaseIndent() {
        if (level > 0) indent = indent.substring(0, (level - 1) * 2);
    }

    @Override
    protected void enterComposite(Composite composite) {
        color++;
        level++;

        increaseIndent();
        graph.append(indent).append(SUB_GRAPH).append(" ").append("cluster").append(color).append(" ").append("{\n");
        increaseIndent();
        graph.append(indent).append(NODE).append("[").append(nodeStyle).append("color=gray").append(45 + level).append("];\n");
        graph.append(indent).append("style=\"filled,rounded\";").append(" color=gray").append(95 - 8 * level).append(";\n");
        graph.append(indent).append(LABEL).append(composite.getName()).append("\";\n\n");
    }

    @Override
    public void visitAtomic(Atomic atomic) {
        String name = atomic.getName();

        if (!idMap.containsKey(atomic))
            addToIdMap(atomic);

        String currentID = idMap.get(atomic);

        for (Atomic.Outport port : atomic.getOutports()) {

            List<Port<?>> remotePorts = port.getRemotePorts();

            if (remotePorts != null) {

                for (int i = 0; i <= remotePorts.size(); i++) {

                    Entity remoteEntity = remotePorts.get(i).getHost();

                    if (remoteEntity != null) {

                        if (!idMap.containsKey(remoteEntity)) addToIdMap(remoteEntity);
                        String remoteID = idMap.get(remoteEntity);

                        graph.append(indent).append(currentID).append(" -> ").append(remoteID).append("").append(";\n");
                    }
                    else
                        graph.append(indent).append(currentID).append(" -> ").append("UNKNOWN").append(";\n");
                }
            }
        }

        graph.append(indent).append(idMap.get(atomic)).append(" [xlabel=\"").append(name).append("\"").append(getStyle(atomic)).append("]\n");
    }

    private String getStyle(Atomic atomic) {
        String style = classStyleMap.get(atomic.getClass());
        if (style != null)
            return style;
        else
            return DEFAULT_SHAPE;
    }

    private void addToIdMap(Entity entity) {
        idMap.put(entity, String.valueOf(atomicID++));
    }

    @Override
    protected void leaveComposite(Composite composite) {
        decreaseIndent();
        decreaseIndent();
        graph.append(indent).append(END);

        level--;

        if (level == 0) graph.append(END);
    }

    @Override
    public String toString() {
        return graph.toString();
    }

    public Dotify() {
        graph.append(GRAPH);
        graph.append("\tgraph [").append(graphOptions).append("];\n");
    }

    public void setDefaultNodeStyle(String nodeStyle) {
        this.nodeStyle = nodeStyle;
    }

}
