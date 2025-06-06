import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomGraph<T> {

    private final Map<T, List<T>> adjacencyList;

    public CustomGraph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addNode(T node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(T source, T destination) {
        addNode(source);
        addNode(destination);

        if (!adjacencyList.get(source).contains(destination)) {
            adjacencyList.get(source).add(destination);
        }
        if (!adjacencyList.get(destination).contains(source)) {
            adjacencyList.get(destination).add(source);
        }
    }

    public List<T> getNeighbors(T node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public Map<T, List<T>> getAdjacencyList() {
        return adjacencyList;
    }

    public void clear() {
        adjacencyList.clear();
    }
}