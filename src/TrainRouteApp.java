import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrainRouteApp extends JFrame {
    private final TrainManager trainManager;
    private JList<Train> trainListDisplay;
    private DefaultListModel<Train> trainListModel;
    private JTextArea searchResultsArea;

    private JTextField searchByStopField;
    private JTextField searchRouteFromField;
    private JTextField searchRouteToField;

    public TrainRouteApp() {
        trainManager = new TrainManager();
        initComponents();
        refreshTrainList();
        setTitle("Train Route Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel trainPanel = new JPanel(new BorderLayout(5, 5));
        trainPanel.setBorder(new TitledBorder("Available Trains"));
        trainListModel = new DefaultListModel<>();
        trainListDisplay = new JList<>(trainListModel);
        trainListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane trainScrollPane = new JScrollPane(trainListDisplay);
        trainPanel.add(trainScrollPane, BorderLayout.CENTER);

        JPanel trainButtonsPanel = getJPanel();

        trainPanel.add(trainButtonsPanel, BorderLayout.SOUTH);
        add(trainPanel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(new TitledBorder("Search"));

        JPanel singleStopSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        singleStopSearchPanel.add(new JLabel("Find trains via stop:"));
        searchByStopField = new JTextField(15);
        singleStopSearchPanel.add(searchByStopField);
        JButton searchByStopButton = new JButton("Search");
        searchByStopButton.addActionListener(e -> searchByStop());
        singleStopSearchPanel.add(searchByStopButton);
        searchPanel.add(singleStopSearchPanel);

        JPanel routeSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        routeSearchPanel.add(new JLabel("Find route from:"));
        searchRouteFromField = new JTextField(10);
        routeSearchPanel.add(searchRouteFromField);
        routeSearchPanel.add(new JLabel("to:"));
        searchRouteToField = new JTextField(10);
        routeSearchPanel.add(searchRouteToField);
        JButton searchRouteButton = new JButton("Search Route");
        searchRouteButton.addActionListener(e -> searchRoute());
        routeSearchPanel.add(searchRouteButton);
        searchPanel.add(routeSearchPanel);

        searchResultsArea = new JTextArea(15, 40);
        searchResultsArea.setEditable(false);
        JScrollPane resultsScrollPane = new JScrollPane(searchResultsArea);
        resultsScrollPane.setBorder(new TitledBorder("Search Results"));
        searchPanel.add(resultsScrollPane);

        add(searchPanel, BorderLayout.CENTER);
    }

    private JPanel getJPanel() {
        JPanel trainButtonsPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Train");
        addButton.addActionListener(e -> addTrain());
        trainButtonsPanel.add(addButton);

        JButton editButton = new JButton("Edit Train");
        editButton.addActionListener(e -> editTrain());
        trainButtonsPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Train");
        deleteButton.addActionListener(e -> deleteTrain());
        trainButtonsPanel.add(deleteButton);

        JButton sortStopsButton = new JButton("Sort by Stops");
        sortStopsButton.addActionListener(e -> {
            trainManager.sortTrainsByStopCount();
            refreshTrainList();
        });
        trainButtonsPanel.add(sortStopsButton);

        JButton sortNameButton = new JButton("Sort by Start Stop");
        sortNameButton.addActionListener(e -> {
            trainManager.sortTrainsByStartStopName();
            refreshTrainList();
        });
        trainButtonsPanel.add(sortNameButton);
        return trainButtonsPanel;
    }

    private void refreshTrainList() {
        trainListModel.clear();
        trainManager.getAllTrains().forEach(trainListModel::addElement);
    }

    private void addTrain() {
        AddEditTrainDialog dialog = new AddEditTrainDialog(this, "Add New Train", null);
        dialog.setVisible(true);
        Train newTrain = dialog.getTrain();
        if (newTrain != null) {
            if (trainManager.getTrainById(newTrain.getTrainId()).isPresent()) {
                JOptionPane.showMessageDialog(this, "Train with ID '" + newTrain.getTrainId() + "' already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                if (newTrain.getStartStop().getName().isEmpty() || newTrain.getEndStop().getName().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Start and End stops cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                trainManager.addTrain(newTrain);
                refreshTrainList();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error adding train: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editTrain() {
        Train selectedTrain = trainListDisplay.getSelectedValue();
        if (selectedTrain == null) {
            JOptionPane.showMessageDialog(this, "Please select a train to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AddEditTrainDialog dialog = new AddEditTrainDialog(this, "Edit Train", selectedTrain);
        dialog.setVisible(true);
        Train updatedTrain = dialog.getTrain();
        if (updatedTrain != null) {
            Optional<Train> conflictingTrain = trainManager.getTrainById(updatedTrain.getTrainId());
            if (conflictingTrain.isPresent() && !conflictingTrain.get().getTrainId().equalsIgnoreCase(selectedTrain.getTrainId())) {
                JOptionPane.showMessageDialog(this, "Another train with ID '" + updatedTrain.getTrainId() + "' already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                if (updatedTrain.getStartStop().getName().isEmpty() || updatedTrain.getEndStop().getName().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Start and End stops cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                trainManager.editTrain(selectedTrain.getTrainId(), updatedTrain);
                refreshTrainList();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error editing train: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteTrain() {
        Train selectedTrain = trainListDisplay.getSelectedValue();
        if (selectedTrain == null) {
            JOptionPane.showMessageDialog(this, "Please select a train to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete train '" + selectedTrain.getTrainId() + "'?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            trainManager.removeTrain(selectedTrain.getTrainId());
            refreshTrainList();
        }
    }

    private void searchByStop() {
        String stopName = searchByStopField.getText().trim();
        if (stopName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a stop name to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Stop searchStop = new Stop(stopName);
            List<Train> foundTrains = trainManager.findTrainsPassingThroughStop(searchStop);
            StringBuilder sb = new StringBuilder("Trains passing through " + stopName + ":\n");
            if (foundTrains.isEmpty()) {
                sb.append("None found.");
            } else {
                foundTrains.forEach(t -> sb.append("- ").append(t.getTrainId()).append(" (").append(t.getFullRoute().stream().map(Stop::getName).collect(Collectors.joining(" -> "))).append(")\n"));
            }
            searchResultsArea.setText(sb.toString());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid stop name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchRoute() {
        String fromStopName = searchRouteFromField.getText().trim();
        String toStopName = searchRouteToField.getText().trim();

        if (fromStopName.isEmpty() || toStopName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both From and To stops.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (fromStopName.equalsIgnoreCase(toStopName)) {
            JOptionPane.showMessageDialog(this, "From and To stops cannot be the same for this search.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Stop fromStop = new Stop(fromStopName);
            Stop toStop = new Stop(toStopName);

            List<List<RouteSegment>> routes = trainManager.findRoutes(fromStop, toStop);
            StringBuilder sb = new StringBuilder("Routes from " + fromStopName + " to " + toStopName + ":\n\n");
            if (routes.isEmpty()) {
                sb.append("No routes found (direct or with one transfer).");
            } else {
                for (int i = 0; i < routes.size(); i++) {
                    sb.append("Option ").append(i + 1).append(":\n");
                    List<RouteSegment> routePath = routes.get(i);
                    if (routePath.size() == 1) {
                        sb.append("  Direct: ").append(routePath.get(0).toString()).append("\n");
                    } else {
                        sb.append("  With 1 Transfer:\n");
                        sb.append("    1. ").append(routePath.get(0).toString()).append("\n");
                        sb.append("    Transfer at: ").append(routePath.get(0).getToStop().getName()).append("\n");
                        sb.append("    2. ").append(routePath.get(1).toString()).append("\n");
                    }
                    sb.append("\n");
                }
            }
            searchResultsArea.setText(sb.toString());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid stop name(s): " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}