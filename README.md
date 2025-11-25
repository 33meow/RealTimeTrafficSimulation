# RealTimeTrafficSimulation
To ensure the project runs smoothly on any machine without requiring path modification, we have structured the project using relative path and self-contained configuration folder.

3.1	Project Structure: 

The project follows a standard Eclipse Java project structure with a dedicated folder for SUMO configuration files: 

-	Src/trafficsimulation: Contains all Java source code, separated into logical classes (Main, SimulationManager, SumoVehicle, MainFrame).

-	SumoConfic: a folder containing the simulation map and scenario files (osm.sumocfg, osm.net.xml.gz, osm.passenger.trips.xml).

-	Referenced Libraries: Contains the TraaS.jar library required for the TraCi interface.


3.2	How to Run the Project

1.	Import: Import the project folder into the Eclipse DIE as an existing Java Project.

2.	Verify Build Path: Ensure that TraaS.jar is correctly referenced in Java Build Path (Classpath).

3.	Run: Navigate to src/trafficsimulation/Main.java.

4.	Execute: Right-click on Main.java and select Run As > Java Application.

5.	Control: The application window will appear. Click the „Start“button to launch the SUMO GUI and begin the simulation.
