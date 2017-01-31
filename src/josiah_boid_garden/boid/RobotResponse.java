package josiah_boid_garden.boid;

import battlecode.common.RobotInfo;

public class RobotResponse{
	
	Boid base;
	int power;
	public RobotResponse (Boid base , int power){
		this.base = base;
		this.power = power;
	}
	
	public void runAway( RobotInfo[] robots ){
		
		for(int i = 0 ; i<robots.length ; i++){
			
			base.addSquaredRepulsion(robots[i].getLocation(), power);
			
		}
		
	}
	
	public void runToward( RobotInfo[] robots ){
		
		for(int i = 0 ; i<robots.length ; i++){
			
			base.addLinearAttraction(robots[i].getLocation(), power);
			
		}
		
	}
	

}
