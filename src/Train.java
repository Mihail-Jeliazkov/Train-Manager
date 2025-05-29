import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Train {
    private final String trainId;
    private final Stop startStop;
    private final Stop endStop;
    private final List<Stop> intermediateStops;

    public Train(String trainId, Stop startStop, Stop endStop, List<Stop> intermediateStops) {
        if (trainId == null || trainId.trim().isEmpty()) {
            throw new IllegalArgumentException("Train ID cannot be null or empty.");
        }
        if (startStop == null) {
            throw new IllegalArgumentException("Start stop cannot be null.");
        }
        if (endStop == null) {
            throw new IllegalArgumentException("End stop cannot be null.");
        }
        this.trainId = trainId.trim();
        this.startStop = startStop;
        this.endStop = endStop;
        this.intermediateStops = intermediateStops == null ? new ArrayList<>() : new ArrayList<>(intermediateStops);

        validateStops();
    }

    private void validateStops() {
        List<Stop> allStops = getFullRoute();
        for (int i = 0; i < allStops.size(); i++) {
            for (int j = i + 1; j < allStops.size(); j++) {
                if (allStops.get(i).equals(allStops.get(j))) {
                    throw new IllegalArgumentException("Route for train " + trainId + " contains duplicate stop: " + allStops.get(i));
                }
            }
        }
    }

    public String getTrainId() {
        return trainId;
    }

    public Stop getStartStop() {
        return startStop;
    }

    public Stop getEndStop() {
        return endStop;
    }

    public List<Stop> getIntermediateStops() {
        return new ArrayList<>(intermediateStops);
    }

    public List<Stop> getFullRoute() {
        List<Stop> fullRoute = new ArrayList<>();
        fullRoute.add(startStop);
        fullRoute.addAll(intermediateStops);
        fullRoute.add(endStop);
        return fullRoute;
    }

    public boolean hasStop(Stop stop) {
        return getFullRoute().contains(stop);
    }

    public int getStopIndex(Stop stop) {
        return getFullRoute().indexOf(stop);
    }

    public int getNumberOfStops() {
        return getFullRoute().size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Train train = (Train) o;
        return Objects.equals(trainId, train.trainId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainId);
    }

    @Override
    public String toString() {
        String intermediateStr = intermediateStops.stream().map(Stop::getName).collect(Collectors.joining(", "));
        if (intermediateStr.isEmpty()) {
            intermediateStr = "None";
        }
        return String.format("Train ID: %s, Route: %s -> %s (Intermediate: %s)", trainId, startStop.getName(), endStop.getName(), intermediateStr);
    }

    public String toFileString() {
        String intermediateCsv = intermediateStops.stream().map(Stop::getName).collect(Collectors.joining(","));
        return String.join(";", trainId, startStop.getName(), endStop.getName(), intermediateCsv);
    }

    public static Train fromFileString(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 3) throw new IllegalArgumentException("Invalid train data format in file: " + line);

        String id = parts[0];
        Stop start = new Stop(parts[1]);
        Stop end = new Stop(parts[2]);
        List<Stop> intermediates = new ArrayList<>();
        if (parts.length == 4 && !parts[3].isEmpty()) {
            String[] intermediateNames = parts[3].split(",");
            for (String name : intermediateNames) {
                if (!name.trim().isEmpty()) {
                    intermediates.add(new Stop(name.trim()));
                }
            }
        }
        return new Train(id, start, end, intermediates);
    }
}