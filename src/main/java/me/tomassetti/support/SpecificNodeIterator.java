package me.tomassetti.support;

import com.github.javaparser.ast.Node;

public class SpecificNodeIterator<T> {
    public interface NodeHandler<T> {
        boolean handle(T node);
    }

    private NodeHandler nodeHandler;
    private Class<T> type;

    public SpecificNodeIterator(NodeHandler nodeHandler, Class<T> type) {
        this.nodeHandler = nodeHandler;
        this.type = type;
    }

    public void explore(Node node) {
        if (type.isInstance(node)) {
            if (!nodeHandler.handle(type.cast(node))) {
                return;
            }
        }
        for (Node child : node.getChildrenNodes()) {
            explore(child);
        }
    }
}
