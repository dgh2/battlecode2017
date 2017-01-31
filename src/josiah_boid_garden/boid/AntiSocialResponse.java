package josiah_boid_garden.boid;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class AntiSocialResponse{
	
	int antiSocialPower = 30;
	
	int maxByteCodes = 800;
	
	Boid base;
	
	public AntiSocialResponse (Boid base){
		this.base = base;
	}
	
	public AntiSocialResponse (Boid base , int antiSocialPower){
		this.base = base;
		this.antiSocialPower = antiSocialPower;
	}
	
	public void run( RobotInfo[] robots ){
		
		int startBytes = Clock.getBytecodeNum();
		
		for(int i = 0 ; i<robots.length ; i++){
			
			if(robots[i].getID() != base.rc.getID()){
				base.addSquaredRepulsion(robots[i].location, antiSocialPower);
			}
				
			if(Clock.getBytecodeNum() - startBytes > maxByteCodes){
				break;
			}
		}
		
	}

}
