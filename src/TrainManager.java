import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrainManager {

    private List<Train> trains;
    private final CustomGraph<String> stationGraph;
    private static final String SAVE_FILE = "trains.txt";

    public TrainManager() {
        this.trains = new ArrayList<>();
        this.stationGraph = new CustomGraph<>();
        loadFromFile();
    }

    public List<Train> getTrains() {
        return trains;
    }

    public boolean addTrain(Train train) {
        if (trains.contains(train)) {
            return false;
        }
        trains.add(train);
        rebuildGraph();
        return true;
    }

    public void removeTrain(String trainName) {
        trains.removeIf(train -> train.getName().equalsIgnoreCase(trainName));
        rebuildGraph();
    }

    public void updateTrain(String oldTrainName, Train newTrain) {
        removeTrain(oldTrainName);
        addTrain(newTrain);
    }

    private void rebuildGraph() {
        stationGraph.clear();
        for (Train train : trains) {
            List<String> stations = train.getStations();
            if (stations.size() < 2)
                continue;

            for (int i = 0; i < stations.size() - 1; i++) {
                stationGraph.addEdge(stations.get(i), stations.get(i + 1));
            }
        }
    }

    public List<Train> findTrainsByStation(String stationName) {
        return trains.stream()
                .filter(train -> train.getStations().stream()
                        .anyMatch(s -> s.equalsIgnoreCase(stationName)))
                .collect(Collectors.toList());
    }

    public Train findDirectRoute(String start, String end) {
        for (Train train : trains) {
            List<String> stations = train.getStations();
            int startIndex = -1;
            int endIndex = -1;
            for (int i = 0; i < stations.size(); i++) {
                if (stations.get(i).equalsIgnoreCase(start))
                    startIndex = i;
                if (stations.get(i).equalsIgnoreCase(end))
                    endIndex = i;
            }

            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                return train;
            }
        }
        return null;
    }

    public List<Train> findRouteWithOneTransfer(String start, String end) {
        List<Train> trainsFromStart = findTrainsByStation(start);

        for (Train train1 : trainsFromStart) {
            List<String> stations1 = train1.getStations();
            int startIndex = stations1.indexOf(start);

            for (int i = startIndex + 1; i < stations1.size(); i++) {
                String transferStation = stations1.get(i);

                Train train2 = findDirectRoute(transferStation, end);

                if (train2 != null && !train1.getName().equals(train2.getName())) {
                    List<Train> result = new ArrayList<>();
                    result.add(train1);
                    result.add(train2);
                    return result;
                }
            }
        }
        return new ArrayList<>();
    }

    public void sortTrainsByName() {
        for (int i = 1; i < trains.size(); i++) {
            Train key = trains.get(i);
            int j = i - 1;
            while (j >= 0 && trains.get(j).getStartStation().compareToIgnoreCase(key.getStartStation()) > 0) {
                trains.set(j + 1, trains.get(j));
                j = j - 1;
            }
            trains.set(j + 1, key);
        }
    }

    public void sortByNumberOfStops() {
        for (int i = 1; i < trains.size(); i++) {
            Train key = trains.get(i);
            int j = i - 1;
            while (j >= 0 && trains.get(j).getNumberOfStops() > key.getNumberOfStops()) {
                trains.set(j + 1, trains.get(j));
                j = j - 1;
            }
            trains.set(j + 1, key);
        }
    }

    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            for (Train train : trains) {
                String line = train.getName() + ":" + String.join(",", train.getStations());
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error saving trains to file: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            createDefaultData();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            trains.clear();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String name = parts[0];
                    List<String> stations = new ArrayList<>(Arrays.asList(parts[1].split(",")));
                    trains.add(new Train(name, stations));
                }
            }
            rebuildGraph();
        } catch (IOException e) {
            System.err.println("Error loading trains from file: " + e.getMessage());
            createDefaultData();
        }
    }

    private void createDefaultData() {
        this.trains = new ArrayList<>();
        addTrain(new Train("Express 101", Arrays.asList("New York", "Philadelphia", "Baltimore", "Washington D.C.")));
        addTrain(new Train("West Coast Line", Arrays.asList("Los Angeles", "San Jose", "San Francisco", "Sacramento")));
        addTrain(new Train("Cross Country 45", Arrays.asList("Chicago", "Omaha", "Denver", "Salt Lake City")));
        addTrain(new Train("Texas Eagle", Arrays.asList("Chicago", "St. Louis", "Dallas", "San Antonio")));
        addTrain(new Train("Florida Flyer", Arrays.asList("Miami", "Orlando", "Jacksonville")));
        rebuildGraph();
    }
}