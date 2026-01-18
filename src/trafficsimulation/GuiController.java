package trafficsimulation;

import java.io.File;

import javax.swing.JFileChooser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.SwingUtilities;

import java.util.List;

public class GuiController {
    
    // Logger for tracking events
    private static final Logger logger = LogManager.getLogger(GuiController.class);
    
    private MainFrame view;
    private SimulationManager manager;
    private VehicleFilter filter;
    private FilterPanel filterPanel;

    public GuiController(MainFrame view, SimulationManager manager) {
        this.view = view;
        this.manager = manager;
        initController();
    }

    /**
     * Updates the statistics panel with the latest simulation data.
     * This method must never be called directly from the Swing EDT.
     */
    private void updateStatistics() {

        // Safety check: statistics panel might not exist yet
        if (view.getStatisticsPanel() == null) {
            return;
        }

        // Get data
        int vehicleCount = manager.getActiveVehicleCount();
        double co2 = manager.getCurrentCo2Emission();

        // Update Swing components on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            view.getStatisticsPanel().setVehicleCount(vehicleCount);
            view.getStatisticsPanel().addCo2Value(co2);
        });
    }


    private void initController() {
        manager.addListener(() -> {
            if (filterPanel != null) {
                filterPanel.update();
            }
            view.getMapPanel().repaint();
        });

        // --- 1. START BUTTON ---
        view.getStartButton().addActionListener(e -> {
            logger.info("START clicked.");
            manager.startSimulation();

            // Setup filter panel once repository is available
            if (filter == null) {
                VehicleRepository repo = manager.getRepository();
                setupFilter(repo);
                FilterPanel fp = getFilterPanel();
                view.setFilterPanel(fp);
                if (filterPanel != null) {
                    filterPanel.update();
                }
            }

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
            updateStatistics();
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
                // --- STATISTICS ---
                updateStatistics();
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

                            //Update filter after every 10 steps
                            if (filterPanel != null && i % 10 == 0) {
                                filterPanel.update();
                            }
                            view.getMapPanel().repaint();
                            Thread.sleep(50); // Small delay to see animation
                            // --- STATISTICS ---
                            updateStatistics();
                        }
                        //final update after stresstest
                        if (filterPanel != null) {
                            filterPanel.update();
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





        // For traffic light control (set time=
                view.getSetDurationButton().addActionListener(e -> {
               
        	try { 
        		String selectedId = (String) view.getLightSelector().getSelectedItem();  //Get Selected Id
        		String text = view.getDurationInput().getText(); // get the number the user wrote
        		double newDuration = Double.parseDouble(text); //converting  text to number
        		
        		if (selectedId != null && manager.getLightRepository() != null) { 
        			
        			TrafficLightWrap light = manager.getLightRepository().findlight(selectedId);
        			if (light != null) { light.setPhaseDuration(newDuration); //calling the new function
        			logger.info("Updated {} duration to {} s", selectedId, newDuration);
        			}
        		}
        		
        	} catch (NumberFormatException ex) {logger.error("Please enter a valid number!");
        	javax.swing.JOptionPane.showMessageDialog(view, "Please enter a valid number (for example 30 or 50)");
        	}
        		
        });
    }
    // Sets up the vehicle filter and associated panel
    public void setupFilter(VehicleRepository repo) {
        this.filter=new VehicleFilter(repo);
        this.filterPanel=new FilterPanel(filter,this);
    }
    public FilterPanel getFilterPanel() {
        return filterPanel;
    }
    public void refreshMap() {
        MapPanel mapPanel =  view.getMapPanel();
        List<VehicleWrap> filtered = filter.getFiltered();

        mapPanel.updateVehicles(filter.getFiltered());
        mapPanel.repaint();

    }
}


