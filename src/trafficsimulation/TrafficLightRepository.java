package trafficsimulation;

import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.objects.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the collection of all Traffic Lights in the simulation.
 * Acts as a central "Database" to store and update light states.
 */
public class TrafficLightRepository {
    
    // --- Fields ---
    private List<TrafficLightWrap> lights;
    private SumoTraciConnection conn;

    // --- Constructor ---
    public TrafficLightRepository(SumoTraciConnection conn) {
        this.conn = conn;
        this.lights = new ArrayList<>();
        loadLights(); // Load lights immediately upon initialization
    }

    // --- Core Logic Methods ---

    /**
     * Fetches all Traffic Light IDs from SUMO and creates wrapper objects.
     */
    public void loadLights() {
        try {
            // Fetch ID list from SUMO API
            SumoStringList ids = (SumoStringList) conn.do_job_get(Trafficlight.getIDList());
            
            // Create wrappers for each light
            for (String id : ids) {
                lights.add(new TrafficLightWrap(id, conn));
            }
            System.out.println("🚦 Loaded " + lights.size() + " Traffic Lights.");
            
        } catch (Exception e) {
            System.err.println("Error loading traffic lights:");
            e.printStackTrace();
        }
    }

    /**
     * Updates the state (Red/Green) of every traffic light in the list.
     * Should be called at every simulation step.
     */
    public void updateLights() {
        for (TrafficLightWrap tl : lights) {
            tl.updateState();
        }
    }

    // --- Getters ---

    public List<TrafficLightWrap> getList() { 
        return lights; 
    }
}