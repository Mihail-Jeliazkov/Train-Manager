import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

public class Main extends JFrame {
    private TrainManager trainManager;
    private DefaultListModel<Train> trainListModel;
    private JList<Train> trainList;
    private JTextField trainNameField, startStationField, endStationField, stationsField;
    private JTextField searchStationField, routeFromField, routeToField;
    private JTextArea resultArea;

    public Main() {
        trainManager = new TrainManager();
        initializeGUI();
        loadData();
    }

    private void initializeGUI() {
        setTitle("Train Route Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Train List"));
        panel.setPreferredSize(new Dimension(300, 600));

        trainListModel = new DefaultListModel<>();
        trainList = new JList<>(trainListModel);
        trainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(trainList);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        
        JButton sortByStationsBtn = new JButton("Sort by Station Count");
        JButton sortByStartBtn = new JButton("Sort by Start Station");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton editBtn = new JButton("Edit Selected");

        sortByStationsBtn.addActionListener(e -> sortTrains(true));
        sortByStartBtn.addActionListener(e -> sortTrains(false));
        deleteBtn.addActionListener(e -> deleteTrain());
        editBtn.addActionListener(e -> editTrain());

        buttonPanel.add(sortByStationsBtn);
        buttonPanel.add(sortByStartBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(editBtn);

        panel.add(listScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel addPanel = createAddTrainPanel();
        
        JPanel searchPanel = createSearchPanel();
        
        JPanel resultPanel = createResultPanel();

        panel.add(addPanel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(resultPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAddTrainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Add/Edit Train"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        trainNameField = new JTextField(15);
        startStationField = new JTextField(15);
        endStationField = new JTextField(15);
        stationsField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Train Name:"), gbc);
        gbc.gridx = 1;
        panel.add(trainNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Start Station:"), gbc);
        gbc.gridx = 1;
        panel.add(startStationField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("End Station:"), gbc);
        gbc.gridx = 1;
        panel.add(endStationField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("All Stations (comma-separated):"), gbc);
        gbc.gridx = 1;
        panel.add(stationsField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Train");
        JButton updateBtn = new JButton("Update Train");
        JButton clearBtn = new JButton("Clear Fields");

        addBtn.addActionListener(e -> addTrain());
        updateBtn.addActionListener(e -> updateTrain());
        clearBtn.addActionListener(e -> clearFields());

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Search"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        searchStationField = new JTextField(15);
        routeFromField = new JTextField(10);
        routeToField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Search by Station:"), gbc);
        gbc.gridx = 1;
        panel.add(searchStationField, gbc);
        gbc.gridx = 2;
        JButton searchStationBtn = new JButton("Search");
        searchStationBtn.addActionListener(e -> searchByStation());
        panel.add(searchStationBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Find Route From:"), gbc);
        gbc.gridx = 1;
        panel.add(routeFromField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        panel.add(routeToField, gbc);
        gbc.gridx = 2;
        JButton findRouteBtn = new JButton("Find Route");
        findRouteBtn.addActionListener(e -> findRoute());
        panel.add(findRouteBtn, gbc);

        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Results"));
        panel.setPreferredSize(new Dimension(400, 200));

        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JPanel filePanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Save Data");
        JButton loadBtn = new JButton("Load Data");

        saveBtn.addActionListener(e -> saveData());
        loadBtn.addActionListener(e -> loadData());

        filePanel.add(saveBtn);
        filePanel.add(loadBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(filePanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addTrain() {
        try {
            String name = trainNameField.getText().trim();
            String start = startStationField.getText().trim();
            String end = endStationField.getText().trim();
            String stationsText = stationsField.getText().trim();

            if (name.isEmpty() || start.isEmpty() || end.isEmpty() || stationsText.isEmpty()) {
                throw new Exception("All fields must be filled!");
            }

            List<String> stations = Arrays.asList(stationsText.split(","));
            for (int i = 0; i < stations.size(); i++) {
                stations.set(i, stations.get(i).trim());
            }

            Train train = new Train(name, start, end, stations);
            trainManager.addTrain(train);
            
            updateTrainList();
            clearFields();
            resultArea.setText("Train added successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTrain() {
        Train selected = trainList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a train to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String name = trainNameField.getText().trim();
            String start = startStationField.getText().trim();
            String end = endStationField.getText().trim();
            String stationsText = stationsField.getText().trim();

            if (name.isEmpty() || start.isEmpty() || end.isEmpty() || stationsText.isEmpty()) {
                throw new Exception("All fields must be filled!");
            }

            List<String> stations = Arrays.asList(stationsText.split(","));
            for (int i = 0; i < stations.size(); i++) {
                stations.set(i, stations.get(i).trim());
            }

            Train newTrain = new Train(name, start, end, stations);
            trainManager.updateTrain(selected, newTrain);
            
            updateTrainList();
            clearFields();
            resultArea.setText("Train updated successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTrain() {
        Train selected = trainList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a train to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete train: " + selected.getName() + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            trainManager.removeTrain(selected);
            updateTrainList();
            clearFields();
            resultArea.setText("Train deleted successfully!");
        }
    }

    private void editTrain() {
        Train selected = trainList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a train to edit!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        trainNameField.setText(selected.getName());
        startStationField.setText(selected.getStartStation());
        endStationField.setText(selected.getEndStation());
        stationsField.setText(String.join(", ", selected.getStations()));
    }

    private void clearFields() {
        trainNameField.setText("");
        startStationField.setText("");
        endStationField.setText("");
        stationsField.setText("");
    }

    private void sortTrains(boolean byStationCount) {
        if (byStationCount) {
            trainManager.sortByStationCount();
        } else {
            trainManager.sortByStartStation();
        }
        updateTrainList();
        resultArea.setText("Trains sorted " + (byStationCount ? "by station count" : "alphabetically by start station"));
    }

    private void searchByStation() {
        String station = searchStationField.getText().trim();
        if (station.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a station name!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Train> trains = trainManager.getTrainsByStation(station);
        StringBuilder result = new StringBuilder();
        result.append("Trains passing through ").append(station).append(":\n\n");
        
        if (trains.isEmpty()) {
            result.append("No trains found.");
        } else {
            for (Train train : trains) {
                result.append(train.toString()).append("\n");
                result.append("Route: ").append(String.join(" - ", train.getStations())).append("\n\n");
            }
        }
        
        resultArea.setText(result.toString());
    }

    private void findRoute() {
        String from = routeFromField.getText().trim();
        String to = routeToField.getText().trim();
        
        if (from.isEmpty() || to.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both stations!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> route = trainManager.findRoute(from, to);
        StringBuilder result = new StringBuilder();
        result.append("Route from ").append(from).append(" to ").append(to).append(":\n\n");
        
        if (route.isEmpty()) {
            result.append("No route found (max 1 transfer allowed).");
        } else if (route.size() == 2) {
            result.append("Direct route: ").append(String.join(" - ", route));
        } else if (route.size() == 3) {
            result.append("Route with 1 transfer: ").append(String.join(" - ", route));
            result.append("\nTransfer at: ").append(route.get(1));
        }
        
        resultArea.setText(result.toString());
    }

    private void updateTrainList() {
        trainListModel.clear();
        for (Train train : trainManager.getTrains()) {
            trainListModel.addElement(train);
        }
    }

    private void saveData() {
        try {
            trainManager.saveToFile("trains.txt");
            resultArea.setText("Data saved successfully to trains.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        try {
            trainManager.loadFromFile("trains.txt");
            updateTrainList();
            resultArea.setText("Data loaded successfully from trains.txt");
        } catch (IOException e) {
            resultArea.setText("No existing data file found. Starting with empty system.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}

// Martin Pancharevski 11:45