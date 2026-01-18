package trafficsimulation;

import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Route;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import de.tudresden.sumo.cmd.Simulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the lifecycle of vehicles in the simulation.
 * FIXED: Handles Unique IDs safely and calculates statistics.
 */
public class VehicleRepository {
    
    // --- Fields ---
    private ArrayList<VehicleWrap> vehicles;
    private SumoTraciConnection conn;
    private int vehicleCounter = 0; // Changed name to indicate it's a counter
    private Random rand; 

    private static final Logger logger = LogManager.getLogger(VehicleRepository.class);

    // --- Constructor ---
    public VehicleRepository(SumoTraciConnection conn) {
        this.conn = conn;
        this.vehicles = new ArrayList<>();
        this.vehicleCounter = 0;
        this.rand = new Random();
    }



    /**
     * Adds 'n' new vehicles safely.
     * Uses 'synchronized' to prevent errors during Stress Tests.
     */
    public synchronized void addVehicle(int n, String type, String imageName) {
        try {
            // Fetch all available edges (roads)
            SumoStringList allEdges = (SumoStringList) conn.do_job_get(Edge.getIDList());

            if (allEdges.isEmpty()) return;

            for (int i = 0; i < n; i++) {

                // Generate Unique ID
                String carId;
                do {
                    vehicleCounter++;
                    carId = "car_" + vehicleCounter;
                } while (idExists(carId)); // Keep trying until we find a free ID

                // --- 2. Select Random Start/End ---
                String startEdge = getRandomEdge(allEdges);
                while (startEdge.startsWith(":")) { // Avoid junctions
                    startEdge = getRandomEdge(allEdges);
                }

                String endEdge = getRandomEdge(allEdges);
                while (endEdge.startsWith(":") || endEdge.equals(startEdge)) {
                    endEdge = getRandomEdge(allEdges);
                }

                // --- 3. Create Route ---
                String newRouteID = "route_" + carId;
                SumoStringList edgesForRoute = new SumoStringList();
                edgesForRoute.add(startEdge);
                conn.do_job_set(Route.add(newRouteID, edgesForRoute));

                // --- 4. Add to SUMO ---
                conn.do_job_set(Vehicle.addFull(
                        carId, newRouteID, type, "now", "0", "0", "0", "current", "max", "current", "", "", "", 0, 0)
                );

                conn.do_job_set(Vehicle.changeTarget(carId, endEdge));

                // --- 5. Add to Java List ---
                VehicleWrap newCar = new VehicleWrap(carId, conn, imageName);
                vehicles.add(newCar);

                logger.info("Added unique vehicle: {}", carId);
            }
        } catch (Exception e) {
            logger.error("Failed to add vehicle:", e);
        }
    }

    /**
     * Checks if ID exists in Java List OR in SUMO.
     */
    private boolean idExists(String id) {
        // Check local list
        for (VehicleWrap v : vehicles) {
            if (v.getID().equals(id)) return true;
        }
        // Check SUMO (Safety check)
        try {
            SumoStringList sumoList = (SumoStringList) conn.do_job_get(Vehicle.getIDList());
            if (sumoList.contains(id)) return true;
        } catch (Exception e) {
            return true; // If error, assume ID is taken to be safe
        }
        return false;
    }

    /**
     * Syncs Java objects with SUMO.
     * Also removes cars that have finished their route.
     */
        public void updateVehicles() {
        try {
            // Get list of currently active vehicles in SUMO
            SumoStringList activeIds = (SumoStringList) conn.do_job_get(Vehicle.getIDList());
            
            // Get list of vehicles that have arrived at their destination
            SumoStringList arrivedIds = (SumoStringList) conn.do_job_get(de.tudresden.sumo.cmd.Simulation.getArrivedIDList());

            // Iterate safely (works with CopyOnWriteArrayList)
            for (VehicleWrap car : vehicles) {

                // 1. If car has arrived/left the simulation -> Remove it
                if (arrivedIds.contains(car.getID())) {
                    vehicles.remove(car);
                    continue;
                }

                // 2. If car is actively running -> Update it
                if (activeIds.contains(car.getID())) {
                    car.updateVehicle();

                    // Debugging: TypeID
                    try {
                        String typeID = (String) conn.do_job_get(Vehicle.getTypeID(car.getID()));
                        System.out.println("Auto " + car.getID() + " - Type: " + typeID + " - ImageName: " + car.getImageName());
                    } catch (Exception e) {
                        System.out.println("Fehler beim Type holen: " + e.getMessage());
                    }

                    // Update Edge for Filter
                    try {
                        String edge = (String) conn.do_job_get(Vehicle.getRoadID(car.getID()));
                        car.setEdge(edge);
                        System.out.println("Auto " + car.getID() + " - Edge: " + edge);
                    } catch (Exception e) {
                        System.out.println("Fehler beim Edge holen: " + e.getMessage());
                    }
                }
                
                // 3. If car is neither active nor arrived, it is in the Queue -> Keep it!
            }
        } catch (Exception e) { 
            logger.error("Error updating vehicles", e);
        }
    }

    public int getvehicleCounter() {
    	return vehicleCounter;
    	}

    // --- Statistics (Required for Project) ---

    public double getAverageSpeed() {
        if (vehicles.isEmpty()) return 0.0;
        double totalSpeed = 0;
        for (VehicleWrap v : vehicles) {
            totalSpeed += v.getSpeed();
        }
        return totalSpeed / vehicles.size();
    }

    // --- Helper Methods ---

    private String getRandomEdge(SumoStringList edges) {
        return edges.get(rand.nextInt(edges.size()));
    }

    // --- Getters ---
    public List<VehicleWrap> getAllVehicles() {
        return new ArrayList<>(vehicles);
    }
    public ArrayList<VehicleWrap> getList() { 
        return vehicles; 
    }
    public double getTotalCo2Emission() {
        double total = 0.0;

        try {
            for (VehicleWrap v : vehicles) {
                total += v.getCo2Emission();
            }
        } catch (Exception e) {
            logger.error("Failed to calculate CO2 emission", e);
        }

        return total;
    }
}

