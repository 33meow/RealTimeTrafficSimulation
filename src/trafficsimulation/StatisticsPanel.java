package trafficsimulation;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

    /**
     * Panel shown in the top-right corner of the GUI.
     * Displays live simulation statistics.
     */
    public class StatisticsPanel extends JPanel {
        private JLabel vehicleCountLabel;
        private JTextArea co2HistoryArea;

        // Stores the last 5 CO2 values
        private LinkedList<Double> co2History = new LinkedList<>();

        public StatisticsPanel() {
            System.out.println("StatisticsPanel CREATED");
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createTitledBorder("Statistics"));
            setPreferredSize(new Dimension(260, 180));

            vehicleCountLabel = new JLabel("Vehicles: 0");
            vehicleCountLabel.setFont(new Font("Arial", Font.BOLD, 14));

            co2HistoryArea = new JTextArea();
            co2HistoryArea.setEditable(false);
            co2HistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            add(vehicleCountLabel, BorderLayout.NORTH);
            add(new JScrollPane(co2HistoryArea), BorderLayout.CENTER);
        }

        /**
         * Updates the vehicle count label.
         */
        public void setVehicleCount(int count) {
            vehicleCountLabel.setText("Vehicles: " + count);
        }

        /**
         * Adds a new CO2 value and keeps only the last five.
         */
        public void addCo2Value(double value) {
            if (co2History.size() == 5) {
                co2History.removeFirst();
            }
            co2History.add(value);
            updateCo2Display();
        }

        /**
         * Refreshes the CO2 history display.
         */
        private void updateCo2Display() {
            StringBuilder sb = new StringBuilder("Last 5 CO₂ steps:\n");

            for (int i = 0; i < co2History.size(); i++) {
                sb.append(String.format("Step %d: %.2f%n", i + 1, co2History.get(i)));
            }

            co2HistoryArea.setText(sb.toString());
        }
    }


