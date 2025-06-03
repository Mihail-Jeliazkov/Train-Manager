import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

public class Train {
    private String name;
    private String startStation;
    private String endStation;
    private List<String> stations;

    public Train(String name, String startStation, String endStation, List<String> stations) {
        this.name = name;
        this.startStation = startStation;
        this.endStation = endStation;
        this.stations = new ArrayList<>(stations);
    }

    public String getName() { return name; }
    public String getStartStation() { return startStation; }
    public String getEndStation() { return endStation; }
    public List<String> getStations() { return new ArrayList<>(stations); }

    public void setName(String name) { this.name = name; }
    public void setStartStation(String startStation) { this.startStation = startStation; }
    public void setEndStation(String endStation) { this.endStation = endStation; }
    public void setStations(List<String> stations) { this.stations = new ArrayList<>(stations); }

    @Override
    public String toString() {
        return name + " (" + startStation + " - " + endStation + ")";
    }
}