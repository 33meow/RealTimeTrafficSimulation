package trafficsimulation;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Simulation;

public class SumoTrafficLight {
	
	private String lightId;
	private SumoTraciConnection conn;
	
	
	/**
	 * Creates a new SumoTrafiicLight wrapper.
	 * @param lightId is the ID of the traffic light in Sumo.
	 * @param conn is the active connection.
	 */
	public SumoTrafficLight(String lightId, SumoTraciConnection conn) 
	{
		this.lightId = lightId;
		this.conn    = conn;
	}

}
