import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrainManager {
    private final List<Train> trains;
    private final CustomGraph<Stop> stopGraph;
    private static final String DATA_FILE = "trains_data.txt";

    public TrainManager() {
        this.trains = new ArrayList<>();
        this.stopGraph = new CustomGraph<>();
        loadTrainsFromFile();
    }

    private void rebuildGraph() {
        stopGraph.clear();
        for (Train train : trains) {
            List<Stop> route = train.getFullRoute();
            if (route.isEmpty()) continue;

            stopGraph.addNode(route.get(0));
            for (int i = 0; i < route.size() - 1; i++) {
                Stop current = route.get(i);
                Stop next = route.get(i + 1);
                stopGraph.addNode(current);
                stopGraph.addNode(next);
                stopGraph.addEdge(current, next);
            }
        }
    }

    public void addTrain(Train newTrain) {
        if (trains.stream().anyMatch(t -> t.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))) {
            return;
        }
        trains.add(newTrain);
        rebuildGraph();
        saveTrainsToFile();
    }

    public void removeTrain(String trainId) {
        boolean removed = trains.removeIf(t -> t.getTrainId().equalsIgnoreCase(trainId));
        if (removed) {
            rebuildGraph();
            saveTrainsToFile();
        }
    }

    public void editTrain(String oldTrainId, Train updatedTrain) {
        Optional<Train> existingTrainOpt = trains.stream().filter(t -> t.getTrainId().equalsIgnoreCase(oldTrainId)).findFirst();

        if (existingTrainOpt.isEmpty()) {
            return;
        }

        if (!oldTrainId.equalsIgnoreCase(updatedTrain.getTrainId())) {
            if (trains.stream().anyMatch(t -> t.getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))) {
                return;
            }
        }

        trains.remove(existingTrainOpt.get());
        trains.add(updatedTrain);
        rebuildGraph();
        saveTrainsToFile();
    }

    public Optional<Train> getTrainById(String trainId) {
        return trains.stream().filter(t -> t.getTrainId().equalsIgnoreCase(trainId)).findFirst();
    }

    public List<Train> getAllTrains() {
        return new ArrayList<>(trains);
    }

    public List<Train> findTrainsPassingThroughStop(Stop stop) {
        return trains.stream().filter(train -> train.hasStop(stop)).collect(Collectors.toList());
    }

    public List<List<RouteSegment>> findRoutes(Stop start, Stop end) {
        List<List<RouteSegment>> allFoundRoutes = new ArrayList<>();

        for (Train train : trains) {
            List<Stop> fullRoute = train.getFullRoute();
            int startIndex = fullRoute.indexOf(start);
            int endIndex = fullRoute.indexOf(end);

            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                List<RouteSegment> directRoute = new ArrayList<>();
                directRoute.add(new RouteSegment(train, start, end));
                allFoundRoutes.add(directRoute);
            }
        }

        for (Train train1 : trains) {
            if (!train1.hasStop(start)) continue;
            int t1StartIndex = train1.getStopIndex(start);
            if (t1StartIndex == -1) continue;

            List<Stop> route1 = train1.getFullRoute();
            for (int i = t1StartIndex + 1; i < route1.size(); i++) {
                Stop transferStop = route1.get(i);

                for (Train train2 : trains) {
                    if (train1.equals(train2)) continue;

                    if (!train2.hasStop(transferStop) || !train2.hasStop(end)) continue;

                    int t2TransferIndex = train2.getStopIndex(transferStop);
                    int t2EndIndex = train2.getStopIndex(end);

                    if (t2TransferIndex != -1 && t2EndIndex != -1 && t2TransferIndex < t2EndIndex) {
                        List<RouteSegment> transferRoute = new ArrayList<>();
                        transferRoute.add(new RouteSegment(train1, start, transferStop));
                        transferRoute.add(new RouteSegment(train2, transferStop, end));
                        allFoundRoutes.add(transferRoute);
                    }
                }
            }
        }
        return allFoundRoutes;
    }


    public void sortTrainsByStopCount() {
        InsertionSort.sort(trains, Comparator.comparingInt(Train::getNumberOfStops));
    }

    public void sortTrainsByStartStopName() {
        InsertionSort.sort(trains, Comparator.comparing(train -> train.getStartStop().getName()));
    }

    public void saveTrainsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Train train : trains) {
                writer.write(train.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving trains to file: " + e.getMessage());
        }
    }

    public void loadTrainsFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        trains.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    trains.add(Train.fromFileString(line));
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping invalid line in data file: " + line + " - Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading trains from file: " + e.getMessage());
        }
        rebuildGraph();
    }
}