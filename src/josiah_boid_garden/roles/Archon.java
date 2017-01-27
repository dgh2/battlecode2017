package josiah_boid_garden.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import josiah_boid_garden.util.RobotBase;
import josiah_boid_garden.util.Util;

public class Archon extends RobotBase {
    
	private final int MAXTRIES = 32;
	
	public Archon(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
 
        try {
	        
	        if (this.robotController.getTeamBullets() > RobotType.GARDENER.bulletCost){
	        	
	        	for (int i = 0 ; i < MAXTRIES ; i++){
	        		Direction dir = Util.randomDirection();	
	        		if(robotController.canHireGardener(dir)) {
	        			robotController.hireGardener(dir);
	        		}
	        	}
	        	
	        }
        	
        } catch (Exception e){
        	System.out.println(e.getMessage());
        }

        RobotInfo[] enemyRobots = robotController.senseNearbyRobots( -1, robotController.getTeam().opponent() );
        
        if (enemyRobots.length > 0) {
            // If there is an enemy robot, move away from it
            if (rightHanded) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).opposite().rotateRightDegrees(30));
            }
            if (!robotController.hasMoved()) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).opposite().rotateLeftDegrees(30));
            }
        } 
        
        if (!robotController.hasMoved()) {
            // Move randomly
            tryMove(robotController, Util.randomDirection());
        }

    }
}
