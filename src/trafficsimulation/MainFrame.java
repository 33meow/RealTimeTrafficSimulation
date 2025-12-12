package trafficsimulation;

import javax.swing.*;
import java.awt.*;

/**
 * The Main Window (View) of the application.
 * Holds the MapPanel and the Control Buttons.
 */
public class MainFrame extends JFrame {
    
    // --- Main Components ---
    private MapPanel mapPanel;
    
    // --- Control Panel Components ---
    private JButton startButton;
    private JButton stepButton;
    private JButton stopButton;
    private JButton addCarButton;
    
    //for TrafficLight
    private JComboBox<String> lightSelector;
    private JButton switchLightButton;

    private JButton stressTestButton;  //for stress test

    
    // Dropdown menu for vehicle selection
    private JComboBox<String> carSelector;

    public MainFrame(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
        
        // 1. Window Setup
        setTitle("Traffic Simulation - Pro Version");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 2. Add Map to Center
        add(mapPanel, BorderLayout.CENTER);
        
        // 3. Setup Control Panel (Bottom)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout()); // Aligns buttons in a row
        
        // Initialize Buttons
        startButton = new JButton("Start");
        stepButton  = new JButton("Step");
        stopButton  = new JButton("Stop");
        addCarButton = new JButton("Add Car");

        stressTestButton = new JButton("Stress Test"); // for Stress test
         switchLightButton = new JButton("Switch"); //for TrafficLight
        
        // Initialize Car Selector
        String[] carTypes = {"standard", "ferrari", "Formula1", "Bugatti", "Red Car"};
        carSelector = new JComboBox<>(carTypes);

        lightSelector = new JComboBox<>(); // for TrafficLight

        
        // Add components to Bottom Panel
        bottomPanel.add(startButton);
        bottomPanel.add(stepButton);
        bottomPanel.add(new JLabel("| Type:")); // Visual separator
        bottomPanel.add(carSelector);
        bottomPanel.add(addCarButton);
        bottomPanel.add(stopButton);
        bottomPanel.add(stressTestButton);  //for stress test
        bottomPanel.add(new JLabel(" | Signal: ")); //for TrafficLight (choosing which one to change)
        bottomPanel.add(lightSelector);				//for TrafficLight
        bottomPanel.add(switchLightButton);         //for TrafficLight
        
        // Add Bottom Panel to Frame
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Show Window
        setVisible(true);
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

}


