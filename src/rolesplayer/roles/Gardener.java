package rolesplayer.roles;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import comm.TestTroop;
import gardener.Formation;
import gardener.Maintainer;
import rolesplayer.util.RobotBase;

public class Gardener extends RobotBase {
	
	TestTroop troop;
	
	//planting fields
	float currentPlantingDirection = 0.0f; //the direction the gardener is trying to plant
	float angleBetweenTrees = (float) (28f * Math.PI / 180f); // The number of degrees between trees
	float turnRate = (float)(7 * Math.PI / 180); // the number of degrees the gardener turns to try to plant the next plant(radians).
	int maxPlants = 7; // the maximum number of plants around the gardener.
	//watering fields
	int numberOfMaintainedTrees = 0; // This is the number of ally trees around the gardener that is being maintained by this gardener
    int currentTreeWatered = 0;// within the index of trees being maintained, this is the one that our gardener is currently maintaining.
    TreeInfo[] surroundingTrees; //the ally trees surrounding the gardener
	
    Formation formation;
    Maintainer maintainer;
    Random rand = new Random();
    
	public Gardener(RobotController robotController) {
        super(robotController);
        formation = new Formation(this.robotController,getRandCardinalDir(),Formation.Form.C);
        maintainer = new Maintainer(this.robotController);
        troop = new TestTroop(this.robotController);
    }
    
    

    @Override
    public void run() throws GameActionException {
    	formation.plant();
    	maintainer.maintain();
    	createRandomRobot();
    	troop.run();
    }
    
   
    /**
     * creates a random robot in a random direction.
     * @return
     * true - success
     * false - failure
     */
    boolean createRandomRobot(){
        Direction dir = randomDirection();
        
        Direction[] complimentaryAngles = formation.getComplimentaryAngles();
        
        if(complimentaryAngles.length>0){
        	dir = complimentaryAngles[rand.nextInt(complimentaryAngles.length)];
        }
        
         try {
        	 
        	 if(this.robotController.getTeamBullets() > (RobotType.TANK.bulletCost + 100 + 50) && this.robotController.isBuildReady()){
        		 System.out.println("I have enough money to build a tank");
        		 
        		 if(this.robotController.canBuildRobot(RobotType.TANK, dir)){
        			 System.out.println("I am trying to build a tank");
        			 this.robotController.buildRobot(RobotType.TANK, dir);
        			 return true;
        		 } else {
        			 System.out.println(" for some reason I still can't build a tank.");
        			 System.out.println("direction:" + dir);
        			 formation.print();
        			 
        		 }
        		 
        	 }
        	 
//	        // Randomly attempt to build a Soldier or lumberjack in this direction
//	        if (robotController.canBuildRobot(RobotType.SCOUT, dir) && (robotController.getRoundNum() <= 4 || Math.random() < .015) && robotController.isBuildReady()) {
//					robotController.buildRobot(RobotType.SCOUT, dir);
//	        } else if (robotController.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
//	            robotController.buildRobot(RobotType.SOLDIER, dir);
//	        } else if (robotController.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && robotController.isBuildReady()) {
//	            robotController.buildRobot(RobotType.LUMBERJACK, dir);
//	        } else if (robotController.canBuildRobot(RobotType.TANK, dir) && Math.random() < .01 && robotController.isBuildReady()) {
//	            robotController.buildRobot(RobotType.TANK, dir);
//	        }
//	        return true;
        } catch (GameActionException e) {
				//something happened, actually, we can't build the robot.
				e.printStackTrace();
				return false;
		}
         return false;
    }
    
    Direction getRandCardinalDir(){
    	int dir = rand.nextInt(4);
    	switch(dir){
    	case 0:
    		return Direction.getNorth();
    	case 1:
    		return Direction.getEast();
    	case 2:
    		return Direction.getWest();
    	case 3:
    		return Direction.getSouth();
		default:
			return Direction.getNorth();
    	}
    }

    /**
     * run away from enemy robots
     * @return
     * true - success
     * false - failure
     */
    boolean runAway(){
    	try {
	        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, robotController.getTeam().opponent());
	    	
	        if (enemyRobots.length > 0) {
	            // If there is an enemy robot, move away from it
	            if (rightHanded) {
	                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).opposite().rotateRightDegrees(30));
	            }
	            if (!robotController.hasMoved()) {
	                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).opposite().rotateLeftDegrees(30));
	            }
	        } 
	        return true;
        } catch (GameActionException e) {
				//something happened. You can't get away.
				e.printStackTrace();
				return false;
		}
    }
    
    
    
}
