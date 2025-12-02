package trafficsimulation;


import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Simulation;

public class SumoVehicle {
	
	private String vehicleId;
	private SumoTraciConnection conn;
	
	public SumoVehicle(String vehicleId, SumoTraciConnection conn) {
		this.vehicleId = vehicleId;
		this.conn = conn;
	}

}
