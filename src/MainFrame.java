import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private final TrainManager trainManager;
    private final DefaultListModel<Train> trainListModel;
    private JList<Train> trainList;

    private final JTextField nameField = new JTextField(20);
    private final JTextField stationsField = new JTextField(40);

    private final JTextField fromField = new JTextField(15);
    private final JTextField toField = new JTextField(15);
    private final JTextArea resultArea = new JTextArea(5, 50);

    public MainFrame() {
        this.trainList = trainList;
        this.trainManager = new TrainManager();
        this.trainListModel = new DefaultListModel<>();

        setTitle("Train Route Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        setPreferredSize(new Dimension(1000, 725));

        JPanel listPanel = createListPanel();
        JPanel managementPanel = createManagementPanel();
        JPanel searchPanel = createSearchPanel();

        add(listPanel, BorderLayout.WEST);
        add(managementPanel, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                trainManager.saveToFile();
            }
        });

        updateTrainList();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Available Trains"));

        trainList = new JList<>(trainListModel);
        trainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trainList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Train selected = trainList.getSelectedValue();
                if (selected != null) {
                    nameField.setText(selected.getName());
                    stationsField.setText(String.join(", ", selected.getStations()));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(trainList);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        JPanel sortButtonPanel = new JPanel(new FlowLayout());
        JButton sortByNameButton = new JButton("Sort by Start Station");
        sortByNameButton.addActionListener(e -> {
            trainManager.sortTrainsByName();
            updateTrainList();
        });

        JButton sortByStopsButton = new JButton("Sort by Stops");
        sortByStopsButton.addActionListener(e -> {
            trainManager.sortByNumberOfStops();
            updateTrainList();
        });

        sortButtonPanel.add(sortByNameButton);
        sortButtonPanel.add(sortByStopsButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(sortButtonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createManagementPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Manage Train"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Train Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Stations:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(stationsField, gbc);

        gbc.gridwidth = 1;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add New");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        panel.add(buttonPanel, gbc);

        addButton.addActionListener(e -> handleAddTrain());
        updateButton.addActionListener(e -> handleUpdateTrain());
        deleteButton.addActionListener(e -> handleDeleteTrain());
        clearButton.addActionListener(e -> clearInputFields());

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Find Route"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.add(new JLabel("From:"));
        inputPanel.add(fromField);
        inputPanel.add(new JLabel("To:"));
        inputPanel.add(toField);
        JButton findButton = new JButton("Find Route");
        inputPanel.add(findButton);

        resultArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultScrollPane, BorderLayout.CENTER);

        findButton.addActionListener(e -> handleFindRoute());

        return panel;
    }

    private void updateTrainList() {
        trainListModel.clear();
        for (Train train : trainManager.getTrains()) {
            trainListModel.addElement(train);
        }
    }

    private void clearInputFields() {
        nameField.setText("");
        stationsField.setText("");
        trainList.clearSelection();
    }

    private void handleAddTrain() {
        String name = nameField.getText().trim();
        String stationsStr = stationsField.getText().trim();

        if (name.isEmpty() || stationsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Train name and stations cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> stations = Arrays.stream(stationsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (stations.size() < 2) {
            JOptionPane.showMessageDialog(this, "A train must have at least two stations.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (stations.stream().distinct().count() != stations.size()) {
            JOptionPane.showMessageDialog(this, "Duplicate stations are not allowed in a single route.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Train newTrain = new Train(name, stations);
        if (trainManager.addTrain(newTrain)) {
            updateTrainList();
            clearInputFields();
        } else {
            JOptionPane.showMessageDialog(this, "A train with this name already exists.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateTrain() {
        Train selectedTrain = trainList.getSelectedValue();
        if (selectedTrain == null) {
            JOptionPane.showMessageDialog(this, "Please select a train from the list to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String oldName = selectedTrain.getName();
        String newName = nameField.getText().trim();
        String stationsStr = stationsField.getText().trim();

        if (newName.isEmpty() || stationsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Train name and stations cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<String> stations = Arrays.stream(stationsStr.split(",")).map(String::trim).collect(Collectors.toList());
        if (stations.size() < 2) {
            JOptionPane.showMessageDialog(this, "A train must have at least two stations.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Train updatedTrain = new Train(newName, stations);

        if(!oldName.equalsIgnoreCase(newName) && trainManager.getTrains().contains(updatedTrain)) {
            JOptionPane.showMessageDialog(this, "Another train with the new name already exists.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        trainManager.updateTrain(oldName, updatedTrain);
        updateTrainList();
        clearInputFields();
    }

    private void handleDeleteTrain() {
        Train selectedTrain = trainList.getSelectedValue();
        if (selectedTrain == null) {
            JOptionPane.showMessageDialog(this, "Please select a train from the list to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the train '" + selectedTrain.getName() + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            trainManager.removeTrain(selectedTrain.getName());
            updateTrainList();
            clearInputFields();
        }
    }

    private void handleFindRoute() {
        String from = fromField.getText().trim();
        String to = toField.getText().trim();

        if (from.isEmpty() || to.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both a 'From' and 'To' station.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (from.equalsIgnoreCase(to)) {
            JOptionPane.showMessageDialog(this, "'From' and 'To' stations cannot be the same.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder result = new StringBuilder();

        Train directRoute = trainManager.findDirectRoute(from, to);
        if (directRoute != null) {
            result.append("--- Direct Route Found ---\n");
            result.append("Take train '").append(directRoute.getName()).append("'\n");
            result.append("Route: ").append(String.join(" -> ", directRoute.getStations()));
        } else {
            List<Train> transferRoute = trainManager.findRouteWithOneTransfer(from, to);
            if (!transferRoute.isEmpty()) {
                Train train1 = transferRoute.get(0);
                Train train2 = transferRoute.get(1);

                String transferStation = "";
                for(String station : train1.getStations()){
                    if(train2.getStations().contains(station)){
                        transferStation = station;
                        break;
                    }
                }

                result.append("--- Route with 1 Transfer Found ---\n");
                result.append("1. Take train '").append(train1.getName()).append("' from ").append(from).append(" to ").append(transferStation).append(".\n");
                result.append("2. Transfer at ").append(transferStation).append(".\n");
                result.append("3. Take train '").append(train2.getName()).append("' from ").append(transferStation).append(" to ").append(to).append(".");
            } else {
                result.append("No direct route or route with one transfer found between '")
                        .append(from).append("' and '").append(to).append("'.");
            }
        }

        resultArea.setText(result.toString());
    }
}