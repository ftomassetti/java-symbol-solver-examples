
import com.github.javaparser.ast.Node
import java.io.File
import java.util.*

fun findJavaFiles(dir: File) : List<File> {
    val files = LinkedList<File>()
    DirExplorer(object : DirExplorer.Filter {
        override fun interested(level: Int, path: String, file: File): Boolean = file.path.endsWith(".java")
    }, object : DirExplorer.FileHandler {
        override fun handle(level: Int, path: String, file: File) {
            files.add(file)
        }
    }).explore(dir)
    return files
}

class NodeIterator(private val nodeHandler: NodeIterator.NodeHandler) {
    interface NodeHandler {
        fun handle(node: Node): Boolean
    }

    fun explore(node: Node) {
        if (nodeHandler.handle(node)) {
            for (child in node.childrenNodes) {
                explore(child)
            }
        }
    }
}

class SpecificNodeIterator<T>(private val type: Class<T>, private val nodeHandler: SpecificNodeIterator.NodeHandler<T>) {
    interface NodeHandler<T> {
        fun handle(node: T): Boolean
    }

    fun explore(node: Node) {
        if (type.isInstance(node)) {
            if (!nodeHandler.handle(type.cast(node))) {
                return
            }
        }
        for (child in node.childrenNodes) {
            explore(child)
        }
    }
}

fun <T> Node.descendantsOfType(type: Class<T>) : List<T> {
    val descendants = LinkedList<T>()
    SpecificNodeIterator(type, object : SpecificNodeIterator.NodeHandler<T> {
        override fun handle(node: T): Boolean {
            descendants.add(node)
            return true
        }
    }).explore(this)
    return descendants
}
