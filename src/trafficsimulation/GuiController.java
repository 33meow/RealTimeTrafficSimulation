package trafficsimulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The Controller Class (MVC Pattern).
 * Handles user interactions (Button Clicks) and bridges the View (MainFrame) 
 * with the Model/Logic (SimulationManager).
 */
public class GuiController {

    private static final Logger logger = LogManager.getLogger(GuiController.class);

    
    // --- Core Components ---
    private MainFrame view;
    private SimulationManager manager;

    public GuiController(MainFrame view, SimulationManager manager) {
        this.view = view;
        this.manager = manager;
        initController();
    }

    /**
     * Initializes Event Listeners for all UI buttons.
     */
    private void initController() {
        
        // --- 1. Simulation Control Buttons ---
        
        // Start Simulation
        view.getStartButton().addActionListener(e -> manager.startSimulation());

        // Step Forward (Manual Control)
        view.getStepButton().addActionListener(e -> {
            manager.nextStep();
            view.getMapPanel().repaint(); // Refresh the map to show changes
        });

        // Stop Simulation
        view.getStopButton().addActionListener(e -> manager.stopSimulation());

        // --- 2. Interaction Buttons (Add Vehicle) ---
        
        view.getAddCarButton().addActionListener(e -> {
            // Ensure simulation is running and repository exists
            if (manager.getRepository() != null) {
                
                // 1. Retrieve the selected vehicle type (Image Name) from the dropdown menu
                String selectedImage = (String) view.getCarSelector().getSelectedItem();
                
                // 2. Add the vehicle to the repository logic
                manager.getRepository().addVehicle(1, "DEFAULT_VEHTYPE", selectedImage);
                
                // 3. Update simulation immediately to show the new car
                manager.nextStep(); 
                view.getMapPanel().repaint();
            }
        });
                // 4. Stress Test
        view.getStressTestButton().addActionListener(e -> {
            if (manager.getRepository() != null) {
                logger.warn("STARTING STRESS TEST SCENARIO");
                
                logger.warn("Injecting 100 vehicles...");
                manager.getRepository().addVehicle(100, "DEFAULT_VEHTYPE", "standard");
                
                
                new Thread(() -> {
                    logger.info("Running 100 fast simulation steps...");
                    
                    try {
                        for (int i = 0; i < 100; i++) {
                            manager.nextStep();
                            view.getMapPanel().repaint();
                            
                            Thread.sleep(50); 
                        }
                        logger.info("Stress Test Sequence Finished.");
                        
                    } catch (InterruptedException ex) {
                        logger.error("Stress test interrupted", ex);
                    }
                }).start();

            } else {
                logger.error("Cannot run Stress Test: Simulation not running.");
            }
        });
    }

}
