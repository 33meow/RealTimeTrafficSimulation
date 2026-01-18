package trafficsimulation;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 * The Main Window (View) of the application.
 * Holds the MapPanel and the Control Buttons.
 */
public class MainFrame extends JFrame {
    
    // --- Main Components ---
    private MapPanel mapPanel;

    // --- Filter Panel ---
    private FilterPanel filterPanel;

    // --- Control Panel Components ---
    private JButton startButton;
    private JButton stepButton;
    private JButton stopButton;
    private JButton addCarButton;
    private JButton exportCsvButton;


    //for TrafficLight
    private JComboBox<String> lightSelector;
    private JButton switchLightButton;

    private JButton stressTestButton;  //for stress test


    //for Trafficlight control
    private JTextField durationInput;
    private JButton    setDurationButton;


    // Dropdown menu for vehicle selection
    private JComboBox<String> carSelector;

    private StatisticsPanel statisticsPanel;

    public MainFrame(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
        
        // 1. Window Setup
        setTitle("Traffic Simulation - Pro Version");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 2. Add Map to Center
        add(mapPanel, BorderLayout.CENTER);

        // --- Statistics Panel (Top Right) ---
        statisticsPanel = new StatisticsPanel();

// Use absolute positioning to ensure visibility on top of the map
        mapPanel.setLayout(null);
        statisticsPanel.setBounds(720, 10, 260, 180);
        mapPanel.add(statisticsPanel);

        // 3. Setup Control Panel (Bottom)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout()); // Aligns buttons in a row
        
        // Initialize Buttons
        startButton = new JButton("Start");
        stepButton  = new JButton("Step");
        stopButton  = new JButton("Stop");
        addCarButton = new JButton("Add Car");

        exportCsvButton = new JButton("Export CSV"); //for the Export CSV button


        stressTestButton = new JButton("Stress Test"); // for Stress test
         switchLightButton = new JButton("Switch"); //for TrafficLight

        //For TrafficLight Control
         durationInput = new JTextField("30", 3); // 30s, width 3
         setDurationButton = new JButton("Set Time");

        // Initialize Car Selector
        String[] carTypes = {"Red", "Yellow", "Blue", "White"};
        carSelector = new JComboBox<>(carTypes);

        lightSelector = new JComboBox<>(); // for TrafficLight

        
        // Add components to Bottom Panel
        bottomPanel.add(startButton);
        bottomPanel.add(stepButton);
        bottomPanel.add(new JLabel("Vehicle Type:")); // Visual separator
        bottomPanel.add(carSelector);
        bottomPanel.add(addCarButton);
        bottomPanel.add(stopButton);
        bottomPanel.add(stressTestButton);  //for stress test
        bottomPanel.add(new JLabel(" | Signal: ")); //for TrafficLight (choosing which one to change)
        bottomPanel.add(lightSelector);				//for TrafficLight
        bottomPanel.add(switchLightButton);         //for TrafficLight

        bottomPanel.add(exportCsvButton);


        exportCsvButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("simulation.csv"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    Files.copy(
                        Paths.get("simulation.csv"),
                        chooser.getSelectedFile().toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    );
                    JOptionPane.showMessageDialog(this, "CSV wurde gespeichert!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Fehler beim Export!");
                }
            }
        });









        bottomPanel.add(new JLabel("Duration (s):"));
        bottomPanel.add(durationInput);
        bottomPanel.add(setDurationButton);


        // Add Bottom Panel to Frame
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Show Window
        setVisible(true);
    }
    public void setFilterPanel(FilterPanel filterPanel) {
        this.filterPanel=filterPanel;
        add(filterPanel, BorderLayout.EAST);
        revalidate();
        repaint();
    }

    // --- Getters for Controller Access ---

    public JButton getStartButton() { return startButton; }
    public JButton getStepButton() { return stepButton; }
    public JButton getStopButton() { return stopButton; }
    public JButton getAddCarButton() { return addCarButton; }
    public JComboBox<String> getCarSelector() { return carSelector; }
    public MapPanel getMapPanel() { return mapPanel; }
    public JButton getStressTestButton() { return stressTestButton; }
    public JComboBox<String> getLightSelector() { return lightSelector; } //for TrafficLight
    public JButton getSwitchLightButton() { return switchLightButton; }	//for TrafficLight

    //public JButton getExportCsvButton() { return exportCsvButton; }

    public JTextField getDurationInput() { return durationInput;}
    public JButton getSetDurationButton() {return setDurationButton; }


    // --- NEW: Statistics access ---
    public StatisticsPanel getStatisticsPanel() {return statisticsPanel;}

}





