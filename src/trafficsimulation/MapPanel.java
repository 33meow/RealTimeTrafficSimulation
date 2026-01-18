package trafficsimulation;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// SUMO Imports
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D; 

public class MapPanel extends JPanel {

    // --- Core Dependencies ---
    private SimulationManager manager;
    private Map<String, Image> imageMap = new HashMap<>();
    private List<List<SumoPosition2D>> roadShapes = new ArrayList<>();
    private List<VehicleWrap> vehiclesToShow;

    // --- State Flags ---
    private boolean mapLoaded = false;
    private boolean isCentered = false; // Checks if camera auto-centered

    // --- Navigation & Zoom Variables ---
    private double scaleFactor = 1.0; 
    private double offsetX = 0;
    private double offsetY = 0;
    private Point lastMousePt;

    public MapPanel(SimulationManager manager) {
        this.manager = manager;
        this.setBackground(new Color(50, 150, 50)); // Background: Green Grass
        this.vehiclesToShow = new ArrayList<>();
        // Load Vehicle Images
        loadImage("Red",    "photos/red.png");
        loadImage("Yellow", "photos/yellow.png");
        loadImage("Blue",   "photos/blue.png");
        loadImage("White",  "photos/white.png");


        // --- Mouse Listener for Panning (Drag) ---
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { lastMousePt = e.getPoint(); }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMousePt != null) {
                    offsetX += e.getX() - lastMousePt.x;
                    offsetY += e.getY() - lastMousePt.y;
                    lastMousePt = e.getPoint();
                    repaint();
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        
        // --- Mouse Listener for Zooming (Wheel) ---
        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) scaleFactor *= 1.1; // Zoom In
            else scaleFactor /= 1.1; // Zoom Out
            repaint();
        });
    }

    /**
     * Helper method to load images safely into the map.
     */
    private void loadImage(String name, String path) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() > 0) imageMap.put(name, icon.getImage());
        else System.out.println("⚠️ Image not found: " + path);
    }

    /**
     * Loads road geometry from SUMO (executed once per map).
     */
    public void loadMap() {
        if (manager.getConnection() == null || mapLoaded) return;
        try {
            System.out.println("⏳ Loading Map Roads...");
            List<String> lanes = (List<String>) manager.getConnection().do_job_get(Lane.getIDList());
            
            roadShapes.clear();
            for (String laneId : lanes) {
                SumoGeometry geometry = (SumoGeometry) manager.getConnection().do_job_get(Lane.getShape(laneId));
                roadShapes.add((List<SumoPosition2D>) geometry.coords);
            }
            
            mapLoaded = true;
            centerMap(); // Auto-center camera after loading
            System.out.println("✅ Map Loaded & Centered!");
            repaint();
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Calculates the optimal Zoom and Offset to fit the map on screen.
     */
    public void centerMap() {
        if (roadShapes.isEmpty() || getWidth() == 0 || getHeight() == 0) return;

        // 1. Calculate Map Bounds (Min/Max X, Y)
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (List<SumoPosition2D> road : roadShapes) {
            for (SumoPosition2D p : road) {
                if (p.x < minX) minX = p.x; if (p.x > maxX) maxX = p.x;
                if (p.y < minY) minY = p.y; if (p.y > maxY) maxY = p.y;
            }
        }

        // 2. Calculate Scale to fit screen (with margin)
        double mapWidth = maxX - minX;
        double mapHeight = maxY - minY;
        double scaleX = (getWidth() * 0.9) / mapWidth;
        double scaleY = (getHeight() * 0.9) / mapHeight;
        scaleFactor = Math.min(scaleX, scaleY);

        // 3. Calculate Offset to center map
        double midMapX = (minX + maxX) / 2.0;
        double midMapY = (minY + maxY) / 2.0;
        offsetX = (getWidth() / 2.0) - (midMapX * scaleFactor);
        offsetY = (midMapY * scaleFactor) - (getHeight() / 2.0);

        isCentered = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Lazy loading of map data
        if (manager.getConnection() != null && !mapLoaded) loadMap();
        if (mapLoaded && !isCentered && getWidth() > 0) centerMap();
        if (manager.getConnection() == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ==========================================
        // 1. Draw Roads (Asphalt Style)
        // ==========================================
        g2d.setColor(new Color(30, 30, 30)); 
        float pixelWidth = (float) (3.3f * scaleFactor);
        if (pixelWidth < 2) pixelWidth = 2;
        g2d.setStroke(new BasicStroke(pixelWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (List<SumoPosition2D> road : roadShapes) {
            if (road.isEmpty()) continue;
            Path2D.Float path = new Path2D.Float();
            SumoPosition2D start = road.get(0);
            
            // Map coordinates logic: Y is inverted (Height - Y)
            path.moveTo((start.x * scaleFactor) + offsetX, getHeight() - (start.y * scaleFactor) + offsetY);

            for (int i = 1; i < road.size(); i++) {
                SumoPosition2D p = road.get(i);
                path.lineTo((p.x * scaleFactor) + offsetX, getHeight() - (p.y * scaleFactor) + offsetY);
            }
            g2d.draw(path);
        }

        // ==========================================
        // 2. Draw Traffic Lights
        // ==========================================
        if (manager.getLightRepository() != null) {
            for (TrafficLightWrap tl : manager.getLightRepository().getList()) {
                String stateString = tl.getCurrentState();
                
                for (TrafficLightWrap.SignalPoint sp : tl.getSignalPoints()) {
                    int x = (int) ((sp.pos.x * scaleFactor) + offsetX);
                    int y = (int) (getHeight() - (sp.pos.y * scaleFactor) + offsetY);

                    // Determine color based on state string index
                    Color lightColor = Color.RED;
                    if (stateString != null && sp.index < stateString.length()) {
                        char c = stateString.charAt(sp.index);
                        if (c == 'G' || c == 'g') lightColor = Color.GREEN;
                        else if (c == 'Y' || c == 'y') lightColor = Color.YELLOW;
                    }
                    
                    g2d.setColor(lightColor);
                    int size = (int)(2.0 * scaleFactor); // Dynamic sizing
                    if (size < 8) size = 8; if (size > 20) size = 20;
                    
                    g2d.fillOval(x - size/2, y - size/2, size, size);
                }
            }
        }

        // ==========================================
        // 3. Draw Vehicles
        // ==========================================
        try {
            // Bestimme welche Autos gezeichnet werden sollen
            List<VehicleWrap> carsToShow;

            if (vehiclesToShow != null && !vehiclesToShow.isEmpty()) {
                // FILTER AKTIV: Zeige nur gefilterte Autos
                carsToShow = vehiclesToShow;
            } else {
                // KEINE FILTER: Zeige alle Autos aus Repository
                if (manager.getRepository() != null) {
                    carsToShow = manager.getRepository().getList();
                } else {
                    carsToShow = new ArrayList<>();
                }
            }

            // Zeichne alle Autos aus der Liste
            for (VehicleWrap car : carsToShow) {
                Point2D.Double pos = car.getPosition();
                double angle = car.getAngle();

                // Calculate screen position
                int x = (int) ((pos.x * scaleFactor) + offsetX);
                int y = (int) (getHeight() - (pos.y * scaleFactor) + offsetY);

                // Vehicle Size (Dynamic)
                double realCarLen = 7.0;
                int pixelSize = (int) (realCarLen * scaleFactor);
                if (pixelSize < 5) pixelSize = 5;

                // Rotate and Draw
                var oldTransform = g2d.getTransform();
                g2d.translate(x, y);
                g2d.rotate(Math.toRadians(angle));

                Image imgToDraw = imageMap.get(car.getImageName());
                if (imgToDraw == null) imgToDraw = imageMap.get("Red");

                if (imgToDraw != null) {
                    g2d.drawImage(imgToDraw, -pixelSize/2, -pixelSize/2, pixelSize, pixelSize, this);
                } else {
                    g2d.setColor(Color.RED);
                    g2d.fillOval(-pixelSize/2, -pixelSize/2, pixelSize, pixelSize);
                }
                g2d.setTransform(oldTransform);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void updateVehicles(List<VehicleWrap> vehicles) {
        this.vehiclesToShow = vehicles;
    }

    }


