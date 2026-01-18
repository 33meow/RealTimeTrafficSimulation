package trafficsimulation;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

interface SimulationListener {
    void onStepCompleted();
}

public class SimulationManager {

    // --- Configuration Constants ---
    // Path to the SUMO configuration file
    private static final String CONFIG_FILE = "SumoConfig/osm.sumocfg";
    private static final String SUMO_BIN = "sumo-gui";

    private static final Logger logger = LogManager.getLogger(SimulationManager.class);

    // --- Core Connection Fields ---
    private SumoTraciConnection conn;
    private int stepCounter = 0;
    
    // --- Data Repositories ---
    private VehicleRepository vehicleRepo;
    private TrafficLightRepository lightRepo;

    private List<SimulationListener> listeners = new ArrayList<>();

    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    /**
     * Initializes the connection to SUMO and sets up data repositories.
     */
    public void startSimulation() {
        try {
            //System.out.println("Starting SUMO Simulation...");
            logger.info("Starting SUMO Simulation...");
            
            // 1. Establish connection to SUMO
            conn = new SumoTraciConnection(SUMO_BIN, CONFIG_FILE);
            conn.addOption("start", "true"); // Auto-start the simulation
            conn.runServer();

            // 2. Initialize Repositories (Must be done after connection is active)
            vehicleRepo = new VehicleRepository(conn);
            lightRepo = new TrafficLightRepository(conn);
            
            //System.out.println("SUMO is running. Repositories initialized.");
            logger.info("SUMO started successfully. Repositories initialized.");
            
        } catch (Exception e) {
            //System.err.println("Error starting SUMO:");
            //e.printStackTrace();
            logger.error("Critical Error starting SUMO: ", e);
        }
    }
    
    //initialisiert LogExport durch einen (parameterlosen) Konstruktor
    private LogExport exLog = new LogExport();


    /**
     * Advances the simulation by one step and updates all entities.
     */
    public void nextStep() {
        try {
            // 1. Advance SUMO internal physics
            conn.do_timestep();
            
            // 2. Update Traffic Lights state
            if (lightRepo != null) {
                lightRepo.updateLights();
            }
            
            // 3. Update Vehicles state
            if (vehicleRepo != null) {
                vehicleRepo.updateVehicles();
            }
            
            // 4. Update Counters & Logs
            stepCounter++; 
            double time = (double) conn.do_job_get(Simulation.getTime());
            //System.out.println("Step: " + stepCounter + " | Sim Time: " + time);
            logger.debug("Step: {} | Sim Time: {}", stepCounter, time);
            
            
            int vehicleCounter = vehicleRepo.getvehicleCounter();
            
            // CSV Log-Eintrag
            //exLog.logStep(stepCounter, time); //exLog.logStep(stepCounter, time, vehicleCount); wenn vehicleCount implementiert ist
            // 5. Update Listeners
            notifyListeners();
            exLog.logStep(stepCounter, time, vehicleCounter);

        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("Error during simulation step:", e);
        }
    }

    /**
     * Safely closes the SUMO connection.
     */
    public void stopSimulation() {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            //System.out.println("Simulation stopped.");
            logger.warn("Simulation stopped by user.");
            
         // CSV Export
            CsvExporter.export("simulation.csv", exLog.getRows());
            logger.info("Simulation data exported to simulation.csv");
        }
    }

    private void notifyListeners() {
        for (SimulationListener listener : listeners) {
            listener.onStepCompleted();
        }
    }

    // --- Getters for MVC Architecture ---

    public TrafficLightRepository getLightRepository() {
        return lightRepo;
    }
    
    public VehicleRepository getRepository() {
        return vehicleRepo;
    }
    
    public SumoTraciConnection getConnection() {
        return this.conn;
    }



    /**
     * Returns the number of currently active vehicles in the simulation.
     */
    public int getActiveVehicleCount() {
        try {
            SumoStringList ids = (SumoStringList) conn.do_job_get(Vehicle.getIDList());
            return ids.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Returns total CO2 emission of the current simulation step.
     */
    public double getCurrentCo2Emission() {
        if (vehicleRepo == null) return 0.0;
        return vehicleRepo.getTotalCo2Emission();
    }

    public int getStepCounter() {
        return stepCounter;
    }

}

