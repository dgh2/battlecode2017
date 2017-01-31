package josiah_boid_garden.boid;

import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public abstract class RobotResponse{
	
	Boid base;
	
	RobotResponse (Boid base){
		this.base = base;
	}
	
	public void run( RobotInfo[] robots ){
		
		for(int i = 0 ; i<robots.length ; i++){
			
			respond ( base , robots[i]) ;
			
		}
		
	}
	
	protected abstract void respond ( Boid controller , RobotInfo robot);

}
