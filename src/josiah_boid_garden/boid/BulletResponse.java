package josiah_boid_garden.boid;

import battlecode.common.BulletInfo;
import battlecode.common.Clock;
import battlecode.common.Direction;
import josiah_boid_garden.UnitVector;

public class BulletResponse {

	int antiBullet = 1000;
	Boid base;
	
	int calculationTime;
	
	public BulletResponse (Boid base){
		this(base , 2000);
	}
	
	public BulletResponse (Boid base , int calculationTime){
		this.base = base;
		this.calculationTime = calculationTime;
		
	}
	
	public void run(BulletInfo[] robots ){
		
		int timeout = Clock.getBytecodeNum() + calculationTime;
		for(int i = 0 ; i<robots.length; i++){
			respond ( base , robots[i]) ;
			//limit calculation time
			if(Clock.getBytecodeNum() > timeout)
				break;
		}
		
	}
	
	protected void respond ( Boid controller , BulletInfo bullet){
		
		float distance = bullet.location.distanceTo(controller.rc.getLocation());
		Direction directionToCraft = new Direction (bullet.location , controller.rc.getLocation());
		float degreesBetween =  bullet.getDir().degreesBetween( directionToCraft );

		double maxAngle = Math.tan( controller.rc.getType().bodyRadius /  distance);
	
		
		
		if(degreesBetween < maxAngle*2 && degreesBetween> -maxAngle*2){
			
			UnitVector dodgeDirection = new UnitVector(directionToCraft.rotateLeftDegrees(90));
			int dodgePower = (int) (antiBullet / distance);
			controller.addForce(dodgeDirection , dodgePower);
			controller.rc.setIndicatorLine(controller.rc.getLocation(),
					controller.rc.getLocation().add(dodgeDirection.getDirection(), dodgePower/10)
					,  255, 0, 0);
			
		}
		
		
	}
	
}
