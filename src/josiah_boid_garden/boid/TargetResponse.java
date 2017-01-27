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

	}
	
	private void respond ( Boid controller , MapLocation target){

		UnitVector dir = new UnitVector(controller.rc.getLocation(),target);
				
		controller.addForce(dir, 1);
		
	}
	

}
