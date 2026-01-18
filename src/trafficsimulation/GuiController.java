package trafficsimulation;

import java.io.File;

import javax.swing.JFileChooser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiController {
    
    // Logger for tracking events
    private static final Logger logger = LogManager.getLogger(GuiController.class);
    
    private MainFrame view;
    private SimulationManager manager;

    public GuiController(MainFrame view, SimulationManager manager) {
        this.view = view;
        this.manager = manager;
        initController();
    }

    private void initController() {
        
        // --- 1. START BUTTON ---
        view.getStartButton().addActionListener(e -> {
            logger.info("START clicked.");
            manager.startSimulation();

            // Populate list immediately after start finishes
            if (manager.getLightRepository() != null) {
                view.getLightSelector().removeAllItems();
                
                for (TrafficLightWrap tl : manager.getLightRepository().getList()) {
                    view.getLightSelector().addItem(tl.getID());
                }
                logger.info("Traffic Lights loaded into menu.");
            } else {
                logger.warn("No Traffic Light Repository found yet.");
            }
        });

        // --- 2. SWITCH BUTTON ---
        view.getSwitchLightButton().addActionListener(e -> {
            String selectedId = (String) view.getLightSelector().getSelectedItem();
            
            if (selectedId != null && manager.getLightRepository() != null) {
                for (TrafficLightWrap tl : manager.getLightRepository().getList()) {
                    if (tl.getID().equals(selectedId)) {
                        
                        logger.info("Switching Light: {}", selectedId);
                        
                        // 1. Switch the light logic
                        tl.switchToNextPhase(); 
                       
                        
                        //Repaint to try and show color update
                        view.getMapPanel().repaint();
                        break;
                    }
                }
            }
        });

        // --- 3. STEP BUTTON ---
        view.getStepButton().addActionListener(e -> {
            manager.nextStep();
            view.getMapPanel().repaint();
        });

        // --- 4. STOP BUTTON ---
        view.getStopButton().addActionListener(e -> manager.stopSimulation());

        // --- 5. ADD CAR BUTTON ---
        view.getAddCarButton().addActionListener(e -> {
            if (manager.getRepository() != null) {
                String selectedImage = (String) view.getCarSelector().getSelectedItem();
                logger.info("Adding car: {}", selectedImage);
                manager.getRepository().addVehicle(1, "DEFAULT_VEHTYPE", selectedImage);
                manager.nextStep(); 
                view.getMapPanel().repaint();
            }
        });

        // --- 6. STRESS TEST ---
        view.getStressTestButton().addActionListener(e -> {
            if (manager.getRepository() != null) {
                logger.warn("STARTING STRESS TEST");
                
                // Add cars
                manager.getRepository().addVehicle(100, "DEFAULT_VEHTYPE", "Red");
                
                // Run fast steps in a background thread
                new Thread(() -> {
                    try {
                        for (int i = 0; i < 100; i++) {
                            manager.nextStep();
                            view.getMapPanel().repaint();
                            Thread.sleep(50); // Small delay to see animation
                        }
                        logger.info("Stress Test Finished.");
                    } catch (InterruptedException ex) {
                        logger.error("Stress test interrupted", ex);
                    }
                }).start();

            } else {
                logger.error("Simulation not running.");
            }
        });
    
       //view.getExportCsvButton().addActionListener(e -> {
           // JFileChooser chooser = new JFileChooser();
            //chooser.setSelectedFile(new File("simulation_log.csv"));
       //});
       
       
       
    }
}

