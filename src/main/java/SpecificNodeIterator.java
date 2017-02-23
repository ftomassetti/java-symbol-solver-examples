import com.github.javaparser.ast.Node;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class SpecificNodeIterator {
    private final Class type;
    private final SpecificNodeIterator.NodeHandler nodeHandler;

    public final void explore(@NotNull Node node) {
        Intrinsics.checkParameterIsNotNull(node, "node");
        if(!this.type.isInstance(node) || this.nodeHandler.handle(this.type.cast(node))) {
            Iterator var3 = node.getChildrenNodes().iterator();

            while(var3.hasNext()) {
                Node child = (Node)var3.next();
                Intrinsics.checkExpressionValueIsNotNull(child, "child");
                this.explore(child);
            }

        }
    }

    public SpecificNodeIterator(@NotNull Class type, @NotNull SpecificNodeIterator.NodeHandler nodeHandler) {
        Intrinsics.checkParameterIsNotNull(type, "type");
        Intrinsics.checkParameterIsNotNull(nodeHandler, "nodeHandler");
        super();
        this.type = type;
        this.nodeHandler = nodeHandler;
    }

    @Metadata(
            mv = {1, 1, 1},
            bv = {1, 0, 0},
            k = 1,
            d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\bf\u0018\u0000*\u0004\b\u0001\u0010\u00012\u00020\u0002J\u0015\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00028\u0001H&¢\u0006\u0002\u0010\u0006¨\u0006\u0007"},
            d2 = {"LSpecificNodeIterator$NodeHandler;", "T", "", "handle", "", "node", "(Ljava/lang/Object;)Z", "production sources for module code-search-parser_main"}
    )
    public interface NodeHandler {
        boolean handle(Object var1);
    }
}