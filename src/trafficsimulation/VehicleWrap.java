package trafficsimulation;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A Wrapper class for a SUMO Vehicle.
 * Stores the vehicle's ID, its connection, and its current physical state (Position, Speed, Angle).
 * Also holds the 'imageName' to determine which car photo to draw (e.g., Ferrari, Bugatti).
 */
public class VehicleWrap {

    // --- Static Properties (Do not change) ---
    private final String id;
    private final SumoTraciConnection conn;
    private final String imageName; // The visual type (e.g., "ferrari")

    // --- Dynamic Properties (Updated every step) ---
    private double speed;
    private Point2D.Double position;
    private double angle;

    private String color;
    private String edge;
    private double x;
    private double y;

    // --- Constructor ---
    public VehicleWrap(String id) {
        this.id = id;
        this.conn = null;
        this.imageName = "";
    }

    public VehicleWrap(String id, SumoTraciConnection conn, String imageName) {
        this.id = id;
        this.conn = conn;
        this.imageName = imageName;
        updateVehicle(); // Fetch initial state immediately
    }

    // --- Logic Methods ---

    /**
     * Communicates with SUMO to get the latest speed, position, and angle.
     */
    public void updateVehicle() {
        try {
            // 1. Get Speed
            this.speed = (double) conn.do_job_get(Vehicle.getSpeed(id));

            // 2. Get Position (X, Y)
            SumoPosition2D pos2D = (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(id));
            this.position = new Point2D.Double(pos2D.x, pos2D.y);

            // 3. Get Rotation Angle
            this.angle = (double) conn.do_job_get(Vehicle.getAngle(id));

        } catch (Exception e) {
            // If vehicle leaves the simulation, this might fail (Expected behavior)
            // e.printStackTrace(); 
        }
    }

    // --- Getters ---

    public Point2D.Double getPosition() {
        return position;
    }
    public String getID() {
        return id;
    }
    public double getSpeed() {
        return speed;
    }
    public double getAngle() {
        return angle;
    }
    public String getImageName() {
        return imageName;
    }
    public String getId() {
        return id;
    }
    public String getColor() {
        return color;
    }
    public String getEdge() {
        return edge;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    // --- Setters ---
    public void setEdge(String edge) {
        this.edge = edge;
    }
}