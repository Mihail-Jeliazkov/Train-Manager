import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

public class Graph {
    private Map<String, Set<String>> adjacencyList;
    private Map<String, Set<String>> trainConnections;

    public Graph() {
        adjacencyList = new HashMap<>();
        trainConnections = new HashMap<>();
    }

    public void addTrain(Train train) {
        List<String> stations = train.getStations();
        for (int i = 0; i < stations.size() - 1; i++) {
            String from = stations.get(i);
            String to = stations.get(i + 1);
            
            adjacencyList.computeIfAbsent(from, k -> new HashSet<>()).add(to);
            adjacencyList.computeIfAbsent(to, k -> new HashSet<>()).add(from);
            
            trainConnections.computeIfAbsent(from + "-" + to, k -> new HashSet<>()).add(train.getName());
            trainConnections.computeIfAbsent(to + "-" + from, k -> new HashSet<>()).add(train.getName());
        }
    }

    public void removeTrain(Train train) {
        List<String> stations = train.getStations();
        for (int i = 0; i < stations.size() - 1; i++) {
            String from = stations.get(i);
            String to = stations.get(i + 1);
            
            Set<String> trains1 = trainConnections.get(from + "-" + to);
            Set<String> trains2 = trainConnections.get(to + "-" + from);
            
            if (trains1 != null) trains1.remove(train.getName());
            if (trains2 != null) trains2.remove(train.getName());
        }
    }

    public List<String> findRoute(String start, String end) {
        if (start.equals(end)) return Arrays.asList(start);
        
        if (adjacencyList.containsKey(start) && adjacencyList.get(start).contains(end)) {
            return Arrays.asList(start, end);
        }
        
        if (adjacencyList.containsKey(start)) {
            for (String intermediate : adjacencyList.get(start)) {
                if (adjacencyList.containsKey(intermediate) && 
                    adjacencyList.get(intermediate).contains(end)) {
                    return Arrays.asList(start, intermediate, end);
                }
            }
        }
        
        return new ArrayList<>();
    }
}