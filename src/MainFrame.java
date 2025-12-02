package trafficsimulation;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;


public class MainFrame extends JFrame{
	
	private SimulationManager manager;
	
	
	public MainFrame(SimulationManager manager) 
	{
		setTitle("Traffic Simulation Project");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		setLayout(new FlowLayout());
		
		// creating THE BUTTONS
		JButton startButton = new JButton("Start");
		JButton stepButton  = new JButton("Step");
		JButton stopButton  = new JButton("Stop");
		
		
		//what the Program will do when the user clicks on the Button
		startButton.addActionListener(e->manager.startSimulation());
		stepButton.addActionListener(e->manager.nextStep());
		stopButton.addActionListener(e->manager.stopSimulation());
		
		
		//adding the Buttons to the Windows 
		add(startButton);
		add(stepButton);
		add(stopButton);
		
		
		
		
		
		
		setVisible(true);
		
	}
	
	

} // end of Class
