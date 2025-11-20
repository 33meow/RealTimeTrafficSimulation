package trafficsimulation;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Simulation;


public class Main {

	public static void main(String[] args) throws Exception{
		
		SimulationManager manager = new SimulationManager();
		
		/**
		 * 
		 * Calling the Class MainFrame to open a window
		 */
		MainFrame window = new MainFrame(manager);
		
		
		
		
		
		
		
		
		
		
		// WITHOUT the Class MainFrame
		/*
        //opening Sumo with class "SimulationManager"
		SimulationManager manager = new SimulationManager();
		manager.startSimulation();
		
		//doing some steps with a method of class SimulationManager
		for(int i=0; i<50; i++) {
			manager.nextStep();
			System.out.println("Step: " +i );
		}
		
		
		manager.stopSimulation(); //stoping sumo
		
			*/
		
	} //end of main

} //end of Class MAIN
