package trafficsimulation;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Simulation;

public class SimulationManager {

	
	private SumoTraciConnection conn;
	private int stepCounter = 0;
	
	 /**
	  * Please make sure the 'SumoConfig' folder is inside the project root.
	  * Do NOT use absolute paths (like C:/Users/...).
	  * Structure: TrafficSimulation/SumoConfig/osm.sumocfg
	  */	 	
	private static final String CONFIG_FILE = "SumoConfig/osm.sumocfg";
    private static final String SUMO_GUI = "sumo-gui";
    
     /**
     *  Starting Sumo and connecting to Sumo server.
     *  Getting an error if anything goes wrong ( File is missing)
     */
    public void startSimulation()  
    {
    	// "try" "catch" in case something goes wrong
    	try {
    		System.out.println("Starting Sumo...");
    		
    		conn = new SumoTraciConnection(SUMO_GUI, CONFIG_FILE);
    		
    		conn.addOption("start", "true"); //this function is to start the simulation automatically  after  opening Sumo
    										//otherwise u have to click start(green arrow) after Sumo opens
    		conn.runServer();
    	} catch (Exception e) 
    	   {
    		  System.out.println("Oops, Something is wrong");
    		  e.printStackTrace();
    	   }
    } //end of stratSimulationmethod
    
    
  /**
   * Advances the SUMO simulation by one time step
   * and handling any connection error 
   */ 
    public void nextStep()   
     {
     	try 
     	{
     		conn.do_timestep();	
     		
     		
     		double time = (double) conn.do_job_get(Simulation.getTime());
     		int carCount = (int) conn.do_job_get(Vehicle.getIDCount());
     		
     		System.out.println("========== STEP " + stepCounter + " ==========");
     		System.out.println("Time: " + time + "| How many cars:"+ carCount);
     		
     		
     		List<String> carList = (List<String>) conn.do_job_get(Vehicle.getIDList());
     		
     		for (String carId : carList) {
     			double speed = (double) conn.do_job_get(Vehicle.getSpeed(carId));
     			System.out.println("-> ID: "+ carId +" | speed: " + speed +"m/s");
     		}
     		
       
     		
     	} catch (Exception e) {e.printStackTrace();}
     	
    	
     } // end of nextStep methpd
    
    
    /**
     * Stopping the Simulation and closing the connection.
     * But to avoid any problem the method check if the simulation is
     * already running  ( conn!= null)
     */
    public void stopSimulation() 
    {
    	if(conn != null) 
    	{
    		conn.close();
    		System.out.println("closing Sumo");
    	}
    } // end od stopSimulation method
    
    
    
 /**
  *  Retrieves the current simulation time from SUMO.  
  *  @return The time in seconds (returns 0.0 if the connection fails).
  */
    public double getTime()    
    {
        try {
            return (double) conn.do_job_get(Simulation.getTime());
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    
    /**
     * Provides access to the acive SUMO connection.
     * So the other classes can send commands using the same connection
     * @return the active connection
     */
    public SumoTraciConnection getConnection()  
    {
    	return  this.conn;
    } //end of getConnection method
    
} //end of class 

