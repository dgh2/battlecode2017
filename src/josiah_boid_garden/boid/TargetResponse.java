package josiah_boid_garden.boid;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import josiah_boid_garden.UnitVector;

public class TargetResponse {
	
	Boid boid;
	
	public TargetResponse(Boid boid){
		this.boid = boid;
	}
	
	public void run(MapLocation target){
		
		respond(boid , target );
		/*
		for(int i = 0 ; i<targets.length ; i++){
			respond(boid , targets[i]);
		}*/
	}
	
	private void respond ( Boid controller , MapLocation target){

		UnitVector dir = new UnitVector(controller.rc.getLocation(),target);
		
		System.out.println( "I'm moving toward my target with a magnitude of " + dir.getMagnitude() );
		
		controller.addForce(dir, 1);
		
	}
	

}
