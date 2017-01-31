package josiah_boid_garden.boid;

import java.util.ArrayDeque;
import java.util.Queue;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class MoveAwayFromLastLocation {
	RobotController controller;
	Queue<MapLocation> lastFewLocations = new ArrayDeque<MapLocation>();
	int maxHistory = 5;
	 float repulseForce;
	
	public MoveAwayFromLastLocation(RobotController controller , float repulseForce){
		this.controller = controller;
		this.repulseForce = repulseForce;
	}
	
	
	public void run( Boid boid ){
		lastFewLocations.add(controller.getLocation());
		
		if(lastFewLocations.size() > maxHistory){
			this.lastFewLocations.remove();
		}
		
		for(MapLocation last : this.lastFewLocations){
			boid.addSquaredRepulsion(last,repulseForce);
		}
		
		
		
	}
}
