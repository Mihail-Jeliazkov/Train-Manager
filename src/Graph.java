import java.util.*;

public class Graph {
    private Map<String, Set<String>> adjacencyList;
    private Map<String, Set<String>> trainConnections;

    public Graph() {
        adjacencyList = new HashMap<>();
        trainConnections = new HashMap<>();
    }

    public void addTrain(Train train) {
        List<String> stations = train.getStations();
        
        for (String station : stations) {
            adjacencyList.computeIfAbsent(station.toLowerCase().trim(), k -> new HashSet<>());
        }
        
        for (int i = 0; i < stations.size() - 1; i++) {
            String from = stations.get(i).toLowerCase().trim();
            String to = stations.get(i + 1).toLowerCase().trim();
            
            adjacencyList.computeIfAbsent(from, k -> new HashSet<>()).add(to);
            adjacencyList.computeIfAbsent(to, k -> new HashSet<>()).add(from);
            
            String connection1 = from + "-" + to;
            String connection2 = to + "-" + from;
            trainConnections.computeIfAbsent(connection1, k -> new HashSet<>()).add(train.getName());
            trainConnections.computeIfAbsent(connection2, k -> new HashSet<>()).add(train.getName());
        }
    }

    public void removeTrain(Train train) {
        List<String> stations = train.getStations();
        for (int i = 0; i < stations.size() - 1; i++) {
            String from = stations.get(i).toLowerCase().trim();
            String to = stations.get(i + 1).toLowerCase().trim();
            
            String connection1 = from + "-" + to;
            String connection2 = to + "-" + from;
            
            Set<String> trains1 = trainConnections.get(connection1);
            Set<String> trains2 = trainConnections.get(connection2);
            
            if (trains1 != null) {
                trains1.remove(train.getName());
                if (trains1.isEmpty()) {
                    trainConnections.remove(connection1);
                    if (adjacencyList.containsKey(from)) {
                        adjacencyList.get(from).remove(to);
                    }
                }
            }
            
            if (trains2 != null) {
                trains2.remove(train.getName());
                if (trains2.isEmpty()) {
                    trainConnections.remove(connection2);
                    if (adjacencyList.containsKey(to)) {
                        adjacencyList.get(to).remove(from);
                    }
                }
            }
        }
    }

    public List<String> findRoute(String start, String end) {
        String normalizedStart = start.toLowerCase().trim();
        String normalizedEnd = end.toLowerCase().trim();
        
        if (normalizedStart.equals(normalizedEnd)) {
            return Arrays.asList(start);
        }
        
        if (!adjacencyList.containsKey(normalizedStart) || !adjacencyList.containsKey(normalizedEnd)) {
            return new ArrayList<>();
        }
        
        if (adjacencyList.get(normalizedStart).contains(normalizedEnd)) {
            return Arrays.asList(start, end);
        }
        
        Set<String> startConnections = adjacencyList.get(normalizedStart);
        if (startConnections != null) {
            for (String intermediate : startConnections) {
                Set<String> intermediateConnections = adjacencyList.get(intermediate);
                if (intermediateConnections != null && intermediateConnections.contains(normalizedEnd)) {
                    String originalIntermediate = findOriginalStationName(intermediate);
                    return Arrays.asList(start, originalIntermediate, end);
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    private String findOriginalStationName(String normalizedStation) {
        return normalizedStation.substring(0, 1).toUpperCase() + normalizedStation.substring(1);
    }
    
    public void printGraph() {
        System.out.println("Graph adjacency list:");
        for (Map.Entry<String, Set<String>> entry : adjacencyList.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
        System.out.println("Train connections:");
        for (Map.Entry<String, Set<String>> entry : trainConnections.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}