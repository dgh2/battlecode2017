package josiah_boid_garden.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import josiah_boid_garden.boid.Boid;
import josiah_boid_garden.gardening.Formation;
import josiah_boid_garden.gardening.Maintainer;
import josiah_boid_garden.util.RobotBase;

public class Gardener extends RobotBase {
	

	
	Maintainer maintainer;
	MapLocation start;
	Formation formation;
	
	private final int attackRound = 150; // round we start building attack units
	
	boolean searching = true;
	
    public Gardener(RobotController robotController) {
        super(robotController);
        start = robotController.getLocation();
    }

    @Override
    public void run() throws GameActionException {

	    	if(searching){
	        
	    		Boid controller = new Boid(this.robotController);

	    		Direction dir = new Direction( start , this.robotController.getLocation());
	  
	          // Randomly attempt to build a Soldier or lumberjack in this direction
	          if (robotController.canBuildRobot(RobotType.SCOUT, dir) && (robotController.getRoundNum() <= 50 && Math.random() < .5) && robotController.isBuildReady()) {
	              robotController.buildRobot(RobotType.SCOUT, dir);
	          } 
	          
	          if(controller.getMagnitude() < 0.05*RobotType.GARDENER.strideRadius){
	        	  searching = false;
	        	  formation = new Formation(robotController,new Direction( start , this.robotController.getLocation()), Formation.Form.C);
	        	  maintainer = new Maintainer(this.robotController);
	          }
	          controller.apply();
	          
	    } else {
	    	
	    	formation.plant();
	    	maintainer.maintain();
	          // Randomly attempt to build a Soldier or lumberjack in this direction
	          if (robotController.canBuildRobot(RobotType.SCOUT,formation.getBuildDirection()) && Math.random() < .5 && robotController.isBuildReady() && this.robotController.getRoundNum() > attackRound) {
	              robotController.buildRobot(RobotType.SCOUT, formation.getBuildDirection());
	          } else if (robotController.canBuildRobot(RobotType.LUMBERJACK, formation.getBuildDirection()) && robotController.isBuildReady()) {
	              robotController.buildRobot(RobotType.LUMBERJACK, formation.getBuildDirection());
	          } 
	    	
	    }
    	
    	
    }
}
