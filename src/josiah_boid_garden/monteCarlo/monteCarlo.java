package josiah_boid_garden.monteCarlo;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class monteCarlo {
	
	McPoint[] testPoints = new McPoint[32]; //array of test points
	int tpCount = 0; // how many test points there are.
	RobotController rc;
	monteCarlo(RobotController rc){
		this.rc = rc;
		addCardinalDirections();
		addCardinalHalfDirections();
	}
	
	public boolean addPoint(MapLocation point){
		if(tpCount >= 32 || !rc.canMove(point)){
			return false;
		}
		testPoints[tpCount] = new McPoint(point);
		tpCount++;
		
		return true;
	}
	
	private void addCardinalDirections(){
		addPoint ( rc.getLocation().add(Direction.NORTH , rc.getType().strideRadius) );
		addPoint ( rc.getLocation().add(Direction.SOUTH , rc.getType().strideRadius) );
		addPoint ( rc.getLocation().add(Direction.EAST , rc.getType().strideRadius) );
		addPoint ( rc.getLocation().add(Direction.WEST , rc.getType().strideRadius) );
	}
	
	private void addCardinalHalfDirections(){
		
	}
	
	
	

}
