import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

public class TrainManager {
    private List<Train> trains;
    private Graph graph;

    public TrainManager() {
        trains = new ArrayList<>();
        graph = new Graph();
    }

    public void addTrain(Train train) throws Exception {
        for (Train t : trains) {
            if (t.getName().equalsIgnoreCase(train.getName())) {
                throw new Exception("Train with this name already exists!");
            }
        }
        
        List<String> stations = train.getStations();
        if (stations.size() < 2) {
            throw new Exception("Train must have at least 2 stations!");
        }
        
        Set<String> uniqueStations = new HashSet<>(stations);
        if (uniqueStations.size() != stations.size()) {
            throw new Exception("Duplicate stations found in route!");
        }
        
        trains.add(train);
        graph.addTrain(train);
    }

    public void removeTrain(Train train) {
        trains.remove(train);
        graph.removeTrain(train);
        rebuildGraph();
    }

    public void updateTrain(Train oldTrain, Train newTrain) throws Exception {
        removeTrain(oldTrain);
        try {
            addTrain(newTrain);
        } catch (Exception e) {
            addTrain(oldTrain);
            throw e;
        }
    }

    private void rebuildGraph() {
        graph = new Graph();
        for (Train train : trains) {
            graph.addTrain(train);
        }
    }

    public List<Train> getTrains() {
        return new ArrayList<>(trains);
    }

    public List<Train> getTrainsByStation(String station) {
        List<Train> result = new ArrayList<>();
        for (Train train : trains) {
            if (train.getStations().contains(station)) {
                result.add(train);
            }
        }
        return result;
    }

    public List<String> findRoute(String start, String end) {
        return graph.findRoute(start, end);
    }

    public void sortByStationCount() {
        for (int i = 1; i < trains.size(); i++) {
            Train key = trains.get(i);
            int j = i - 1;
            while (j >= 0 && trains.get(j).getStations().size() > key.getStations().size()) {
                trains.set(j + 1, trains.get(j));
                j--;
            }
            trains.set(j + 1, key);
        }
    }

    public void sortByStartStation() {
        for (int i = 1; i < trains.size(); i++) {
            Train key = trains.get(i);
            int j = i - 1;
            while (j >= 0 && trains.get(j).getStartStation().compareToIgnoreCase(key.getStartStation()) > 0) {
                trains.set(j + 1, trains.get(j));
                j--;
            }
            trains.set(j + 1, key);
        }
    }

    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Train train : trains) {
                writer.println(train.getName() + "|" + train.getStartStation() + "|" + 
                              train.getEndStation() + "|" + String.join(",", train.getStations()));
            }
        }
    }

    public void loadFromFile(String filename) throws IOException {
        trains.clear();
        graph = new Graph();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    String name = parts[0];
                    String start = parts[1];
                    String end = parts[2];
                    List<String> stations = Arrays.asList(parts[3].split(","));
                    
                    Train train = new Train(name, start, end, stations);
                    trains.add(train);
                    graph.addTrain(train);
                }
            }
        }
    }
}