package trafficsimulation;

import de.tudresden.sumo.cmd.Simulation;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimulationManager {

    // --- Configuration Constants ---
    // Path to the SUMO configuration file
    private static final String CONFIG_FILE = "SumoConfig/DefaultMap/osm.sumocfg";
    private static final String SUMO_BIN = "sumo-gui";

    // --- Core Connection Fields ---
    private SumoTraciConnection conn;
    private int stepCounter = 0;
    
    // --- Data Repositories ---
    private VehicleRepository vehicleRepo;
    private TrafficLightRepository lightRepo;

    /**
     * Initializes the connection to SUMO and sets up data repositories.
     */
    public void startSimulation() {
        try {
            System.out.println("🚀 Starting SUMO Simulation...");
            
            // 1. Establish connection to SUMO
            conn = new SumoTraciConnection(SUMO_BIN, CONFIG_FILE);
            conn.addOption("start", "true"); // Auto-start the simulation
            conn.runServer();

            // 2. Initialize Repositories (Must be done after connection is active)
            vehicleRepo = new VehicleRepository(conn);
            lightRepo = new TrafficLightRepository(conn);
            
            System.out.println("✅ SUMO is running. Repositories initialized.");

        } catch (Exception e) {
            System.err.println("❌ Error starting SUMO:");
            e.printStackTrace();
        }
    }

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
            System.out.println("Step: " + stepCounter + " | Sim Time: " + time);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Safely closes the SUMO connection.
     */
    public void stopSimulation() {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("🛑 Simulation stopped.");
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
}