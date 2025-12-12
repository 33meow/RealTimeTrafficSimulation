package trafficsimulation;

import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D;
import de.tudresden.sumo.objects.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents a Traffic Light logic (Junction) in the simulation.
 * Handles the positions of individual signal heads and their current states.
 */
public class TrafficLightWrap {
    
    // --- Fields ---
	private static final Logger logger =
	        LoggerFactory.getLogger(TrafficLightWrap.class);
    private String id;
    private SumoTraciConnection conn;
    private String currentState = ""; 
    
    // List of signal heads (one per controlled lane)
    private List<SignalPoint> signalPoints = new ArrayList<>();

    // --- Constructor ---
    public TrafficLightWrap(String id, SumoTraciConnection conn) {
        this.id = id;
        this.conn = conn;
        calculateSignalPositions(); // Calculate positions on initialization
        updateState();              // Get initial state
    }

    // --- Core Logic ---

    /**
     * Determines the physical position (x, y) for each signal head
     * by finding the end-point of every lane controlled by this light.
     */
    private void calculateSignalPositions() {
        try {
            // 1. Get all lanes controlled by this Traffic Light
            SumoStringList lanes = (SumoStringList) conn.do_job_get(Trafficlight.getControlledLanes(id));
            
            // SUMO returns lanes sorted by their link index
            for (int i = 0; i < lanes.size(); i++) {
                String laneId = lanes.get(i);
                
                // 2. Get the geometry (shape) of the lane
                SumoGeometry shape = (SumoGeometry) conn.do_job_get(Lane.getShape(laneId));
                
                // 3. The Signal is located at the LAST point of the lane (Stop Line)
                if (!shape.coords.isEmpty()) {
                    SumoPosition2D lastPoint = shape.coords.get(shape.coords.size() - 1);
                    
                    // Store the position and its index
                    signalPoints.add(new SignalPoint(lastPoint.x, lastPoint.y, i));
                }
            }
        } catch (Exception ex) {
            // Silently fail if geometry is missing (prevents crash on startup)
            // ex.printStackTrace(); 
        }
    }

    /**
     * Updates the current Red/Yellow/Green state string from SUMO.
     */
    public void updateState() {
        try {
            // Returns a string like "GrGr" (Green, Red, Green, Red)
            this.currentState = (String) conn.do_job_get(Trafficlight.getRedYellowGreenState(id));
        } catch (Exception e) {
           // Ignore update errors
        }
    }
    
    /**
     * Switch to the next traffic light phase in SUMO (via TraCI / TraaS).
     * Reads the current phase index, increments it, applies it, then refreshes the state string.
     */
    public void switchToNextPhase() {
        try {
            // 1) read current phase index (0,1,2,...)
            int currentPhase = (Integer) conn.do_job_get(Trafficlight.getPhase(id));

            // 2) next phase (simple increment)
            int nextPhase = (currentPhase + 1) % 4;

            // 3) apply next phase
            conn.do_job_set(Trafficlight.setPhase(id, nextPhase));

            // 4) refresh cached state so GUI can display the new colors
            updateState();
         
            // 5) log success
            logger.info("Switched Light '{}' to phase {}", id, nextPhase);
            
        } catch (Exception e) {
        	logger.error("Error switching traffic light: " + id, e);
        }
    }


    // --- Getters ---

    public List<SignalPoint> getSignalPoints() { return signalPoints; }
    public String getCurrentState() { return currentState; }
    public String getID() { return id; }

    // --- Inner Class ---
    
    /**
     * Represents a single signal head (Bulb) on the map.
     */
    public static class SignalPoint {
        public Point2D.Double pos;
        public int index; // Index corresponds to the character in the state string
        
        public SignalPoint(double x, double y, int index) {
            this.pos = new Point2D.Double(x, y);
            this.index = index;
        }
    }
}