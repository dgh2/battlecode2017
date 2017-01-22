package josiah_boid_garden.boid;

import battlecode.common.RobotInfo;
import josiah_boid_garden.UnitVector;

public class AntiSocialBehavior extends RobotResponse{
	
	int antiSocialPower = 100;	
	
	public AntiSocialBehavior(Boid base) {
		super(base);
	}
	
	protected void respond ( Boid controller , RobotInfo robot){
		
		if(robot.getID() == controller.rc.getID()){
			return;
		}
		
		System.out.print("Being antisocial toward robot "+robot.getID() + " at " + robot.getLocation() );
		
		//We use a formula similar to the Gravitational Attraction between two bodies equation.
		float distance = controller.rc.getLocation().distanceSquaredTo( robot.getLocation() );
		
		int multiplier = (int) (antiSocialPower / distance);
		
		UnitVector dir = new UnitVector(robot.getLocation() , controller.rc.getLocation());
		
		controller.addForce(dir, multiplier);
		
	}

	
}
