import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomGraph<T> {
    private final Map<T, Set<T>> adjList;

    public CustomGraph() {
        this.adjList = new HashMap<>();
    }

    public void addNode(T node) {
        adjList.putIfAbsent(node, new HashSet<>());
    }

    public void addEdge(T source, T destination) {
        addNode(source);
        addNode(destination);
        adjList.get(source).add(destination);
        adjList.get(destination).add(source);
    }

    public void clear() {
        adjList.clear();
    }
}