package trafficsimulation;

import javax.swing.*;
import java.awt.*;

/**
 * Panel shown in the top-left corner of the GUI.
 * Displays live simulation statistics.
 */
public class StatisticsPanel extends JPanel {

    private JLabel vehicleCountLabel;
    private JTextArea co2HistoryArea;

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Statistics"));
        setPreferredSize(new Dimension(280, 220));

        vehicleCountLabel = new JLabel("Vehicles: 0");
        vehicleCountLabel.setFont(new Font("Arial", Font.BOLD, 14));

        co2HistoryArea = new JTextArea();
        co2HistoryArea.setEditable(false);
        co2HistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(co2HistoryArea);

        add(vehicleCountLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Updates the vehicle count label.
     */
    public void setVehicleCount(int count) {
        vehicleCountLabel.setText("Vehicles: " + count);
    }

    /**
     * Adds a CO2 value for the current step.
     * Newest step is always inserted at the top.
     */
    public void addCo2Value(int step, double value) {
        String line = String.format(
                "Step %d: %.2f mg/s%n",
                step,
                value
        );

        // Insert new line at the very top
        co2HistoryArea.insert(line, 0);

        // Keep view at the top
        co2HistoryArea.setCaretPosition(0);
    }
}
