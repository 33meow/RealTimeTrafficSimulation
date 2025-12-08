package trafficsimulation;

import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Route;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import java.util.ArrayList;
import java.util.Random;

/**
 * Manages the lifecycle of vehicles in the simulation.
 * Handles adding new cars, defining their routes, and tracking their updates.
 */
public class VehicleRepository {
    
    // --- Fields ---
    private ArrayList<VehicleWrap> vehicles;
    private SumoTraciConnection conn;
    private int totalCars; 
    private Random rand; 

    // --- Constructor ---
    public VehicleRepository(SumoTraciConnection conn) {
        this.conn = conn;
        this.vehicles = new ArrayList<>();
        this.totalCars = 0;
        this.rand = new Random();
    }

    // --- Core Logic ---

    /**
     * Adds 'n' new vehicles to the simulation with random start and end points.
     * * @param n Number of cars to add.
     * @param type The internal SUMO vehicle type.
     * @param imageName The name of the image (e.g., "ferrari") for visualization.
     */
    public void addVehicle(int n, String type, String imageName) {
        try {
            // Fetch all available edges (roads)
            SumoStringList allEdges = (SumoStringList) conn.do_job_get(Edge.getIDList());

            if (allEdges.isEmpty()) return;

            for (int i = 0; i < n; i++) {
                // 1. Select a random Start Edge
                // We loop to avoid internal edges (starting with ":") used for junctions
                String startEdge = getRandomEdge(allEdges);
                while (startEdge.startsWith(":")) {
                    startEdge = getRandomEdge(allEdges);
                }

                // 2. Select a random Destination Edge
                // Must not be a junction and must not be the same as start
                String endEdge = getRandomEdge(allEdges);
                while (endEdge.startsWith(":") || endEdge.equals(startEdge)) {
                    endEdge = getRandomEdge(allEdges);
                }

                // 3. Generate unique IDs
                String carId = "user_car_" + totalCars; 
                String newRouteID = "route_" + carId;

                // 4. Create a temporary route (Start -> Start)
                SumoStringList edgesForRoute = new SumoStringList();
                edgesForRoute.add(startEdge);
                conn.do_job_set(Route.add(newRouteID, edgesForRoute));

                // 5. Add the vehicle to SUMO
                conn.do_job_set(Vehicle.addFull(
                        carId, newRouteID, type, "now", "0", "0", "0", "current", "max", "current", "", "", "", 0, 0)
                );

                // 6. Set the real target (SUMO calculates the path automatically)
                conn.do_job_set(Vehicle.changeTarget(carId, endEdge));

                // 7. Store the vehicle wrapper with its visual image
                VehicleWrap newCar = new VehicleWrap(carId, conn, imageName);
                vehicles.add(newCar);
                
                totalCars++;
            }
        } catch (Exception e) {
            System.err.println("Error adding vehicle:");
            e.printStackTrace();
        }
    }

    /**
     * Syncs the Java objects with the current state of SUMO simulation.
     */
    public void updateVehicles() {
        try {
            // Get list of currently active vehicles
            SumoStringList activeIds = (SumoStringList) conn.do_job_get(Vehicle.getIDList());
            
            for (VehicleWrap car : vehicles) {
                // Update only if the car is still in the simulation
                if (activeIds.contains(car.getID())) {
                    car.updateVehicle();
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    // --- Helper Methods ---

    private String getRandomEdge(SumoStringList edges) {
        return edges.get(rand.nextInt(edges.size()));
    }

    // --- Getters ---

    public ArrayList<VehicleWrap> getList() { 
        return vehicles; 
    }
}