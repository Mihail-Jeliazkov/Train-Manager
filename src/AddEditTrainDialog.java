import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddEditTrainDialog extends JDialog {
    private JTextField trainIdField;
    private JTextField startStopField;
    private JTextField endStopField;
    private JTextField intermediateStopsField;
    private Train resultTrain = null;
    private boolean confirmed = false;

    public AddEditTrainDialog(Frame owner, String title, Train trainToEdit) {
        super(owner, title, true);
        initComponents();
        if (trainToEdit != null) {
            populateFields(trainToEdit);
        }
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new GridLayout(5, 2, 5, 5));

        add(new JLabel("Train ID:"));
        trainIdField = new JTextField(20);
        add(trainIdField);

        add(new JLabel("Start Stop:"));
        startStopField = new JTextField(20);
        add(startStopField);

        add(new JLabel("End Stop:"));
        endStopField = new JTextField(20);
        add(endStopField);

        add(new JLabel("Intermediate Stops (comma-separated):"));
        intermediateStopsField = new JTextField(30);
        add(intermediateStopsField);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> onOK());
        add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> onCancel());
        add(cancelButton);
    }

    private void populateFields(Train train) {
        trainIdField.setText(train.getTrainId());
        startStopField.setText(train.getStartStop().getName());
        endStopField.setText(train.getEndStop().getName());
        String intermediate = train.getIntermediateStops().stream().map(Stop::getName).collect(Collectors.joining(","));
        intermediateStopsField.setText(intermediate);
    }

    private void onOK() {
        try {
            String id = trainIdField.getText();
            Stop start = new Stop(startStopField.getText());
            Stop end = new Stop(endStopField.getText());
            List<Stop> intermediates = Arrays.stream(intermediateStopsField.getText().split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Stop::new).collect(Collectors.toList());
            resultTrain = new Train(id, start, end, intermediates);
            confirmed = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public Train getTrain() {
        return confirmed ? resultTrain : null;
    }
}