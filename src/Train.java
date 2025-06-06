import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Train {
    private final String name;
    private final List<String> stations;

    public Train(String name, List<String> stations) {
        this.name = name;
        this.stations = stations;
    }

    public String getName() {
        return name;
    }

    public List<String> getStations() {
        return stations;
    }

    public String getStartStation() {
        if (stations == null || stations.isEmpty()) {
            return "N/A";
        }
        return stations.get(0);
    }

    public String getEndStation() {
        if (stations == null || stations.isEmpty()) {
            return "N/A";
        }
        return stations.get(stations.size() - 1);
    }

    public int getNumberOfStops() {
        return stations.size();
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        for (String station : stations) {
            sj.add(station);
        }
        return String.format("%s (%d stops): %s", name, getNumberOfStops(), sj);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Train train = (Train) o;
        return name.equalsIgnoreCase(train.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}