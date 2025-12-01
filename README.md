# Real-Time Traffic Simulation with Java 🚦

To ensure the project runs smoothly on any machine without requiring path modification, we have structured the project using relative path and self-contained configuration folder.

## Project Structure: 

The project follows a standard Eclipse Java project structure with a dedicated folder for SUMO configuration files: 

-	Src/trafficsimulation: Contains all Java source code, separated into logical classes (Main, SimulationManager, SumoVehicle, MainFrame).

-	SumoConfic: a folder containing the simulation map and scenario files (osm.sumocfg, osm.net.xml.gz, osm.passenger.trips.xml).

-	Referenced Libraries: Contains the TraaS.jar library required for the TraCi interface.


## How to Run the Project

1.	Import: Import the project folder into the Eclipse DIE as an existing Java Project.

2.	Verify Build Path: Ensure that TraaS.jar is correctly referenced in Java Build Path (Classpath).

3.	Run: Navigate to src/trafficsimulation/Main.java.

4.	Execute: Right-click on Main.java and select Run As > Java Application.

5.	Control: The application window will appear. Click the „Start“button to launch the SUMO GUI and begin the simulation.

## 👥 Team Roles & Task Distribution (Milestone 1)

| Team Member | Project Overview | Architecture & GUI Mockups | TraaS Wrapper Design | Java Implementation (OOP) | SUMO Config & Map | Tech Stack & Testing | Documentation |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Mabchour Salaheddine** | |  |  | ✅ | ✅ | |✅ |
| **Ma Yiyuan** | | ✅ | |  | | ✅ |✅ |
| **Gradwohl Laura** | ✅ | | | | |✅|✅ |
| **Mauricio de Souza Hilpert** | | |✅ | ✅ |  | |✅ |
| **Elias Heß**| | | ✅ | |  | ✅| ✅|

## 📅 Time Plan: Features → Time


| Milestone | Deadline | Planned Features & Deliverables | Status |
| :--- | :--- | :--- | :---: |
| **Milestone 1** | **27.11.2025** | • System Architecture Design<br>• SUMO Connection Setup (TraaS)<br>• Basic Simulation Loop | ✅ Done |
| **Milestone 2** | **14.12.2025** | • **Interactive Map:** Rendering edges & vehicles in Java Swing<br>• **Vehicle Injection:** "Add Car" GUI functionality<br>• **Basic Control:** Start/Stop/Step buttons | ⏳ In Progress |
| **Final Submission** | **18.01.2026** | • **Traffic Light Control:** Phase switching via GUI<br>• **Statistics:** Charts & Data Analysis (Speed, CO2)<br>• **Exports:** Save data to CSV/PDF<br>• **Optimization:** Stress testing & Clean Code | 🔮 Planned |
