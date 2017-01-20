package rolesplayer.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import comm.TestCommander;
import rolesplayer.util.RobotBase;

public class Archon extends RobotBase {
   
	TestCommander commander;
	
	int buildOrder = 0;
	
	public Archon(RobotController robotController) {
        super(robotController);
        commander = new TestCommander(this.robotController);
    }

    @Override
    public void run() throws GameActionException {
    	System.out.println("In build number : "+buildOrder);
    	
    	//build some gardeners, send them kinda to the middle of the map ,and build more when it's uneconomical to buy anything else
    	switch(buildOrder){
    	case 0:
    		if (buildGardener()) buildOrder++;
    		break;
    	case 1:
    		if (buildGardener()) buildOrder++;
    		break;
    	case 2:
    		if (buildGardener()) buildOrder++;
    		break;
    	case 3:
    		if(robotController.getTeamBullets() > RobotType.TANK.bulletCost) buildOrder++;
    		break;
    		
    	default:
    		leanGardener();
    		break;
    	
    	
    	}
    	
    	
    	if(this.robotController.getTeamBullets() > RobotType.GARDENER.bulletCost ){
    		if(this.robotController.canHireGardener(Direction.getNorth())){
    			this.robotController.hireGardener(Direction.getNorth());
    		}
    	}
    	
    }
    
    
    boolean buildGardener(){
    	if(this.robotController.getTeamBullets() > RobotType.GARDENER.bulletCost ){
    		Direction buildDirection =getGardenerDirection();
    		if(this.robotController.canHireGardener(buildDirection)){
    			try {
					this.robotController.hireGardener(buildDirection);
					return true;
				} catch (GameActionException e) {
					return false;
				}
    		} else {
    			return false;
    		}
    	} else {
    		return false;
    	}
    }
    
    boolean leanGardener(){
    	if(this.robotController.getTeamBullets()>300) {
    		try {
				this.robotController.donate(50);
			} catch (GameActionException e) {
				//not much you can do, eh?
			}
    		return buildGardener();
    	}
    	return false;
    }
    

    Direction getGardenerDirection(){

    	int tries = 30;
    	Direction buildDir = new Direction((float) (Math.random()*360)); 
    	
    	while( ! (this.robotController.canHireGardener(buildDir)) && tries>0){
    		buildDir = new Direction((float) (Math.random()*360)); 
    		tries--;
    	}
    	
    	return buildDir;
    }

}
