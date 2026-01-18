package trafficsimulation;

public class Main {
    public static void main(String[] args) {

        // Handles SUMO connection and simulation data.
        SimulationManager manager = new SimulationManager();

        // Handles the drawing of the map and vehicles.
        MapPanel mapPanel = new MapPanel(manager);

        // Creates the main window and buttons.
        MainFrame frame = new MainFrame(mapPanel);

        // Binds the View buttons to the Model logic.
        GuiController controller = new GuiController(frame, manager);

    }
}