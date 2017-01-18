package rolesplayer.roles;

import static rolesplayer.util.Util.randomDirection;

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
	
    Formation formation;
    Maintainer maintainer;
    Random rand = new Random();
    
    int buildOrder = 0;
    
	public Gardener(RobotController robotController) {
        super(robotController);
        formation = new Formation(this.robotController,getRandCardinalDir(),Formation.Form.C);
        maintainer = new Maintainer(this.robotController);
        troop = new TestTroop(this.robotController);
    }
    
    

    @Override
    public void run() throws GameActionException {
    	System.out.println("In build number : "+buildOrder);
    	switch(buildOrder){
    	case 0:
    		beAntiSocial();
    		buildOrder++;
    		break;
    	case 1:
    		beAntiSocial();
    		buildOrder++;
    		break;
    	case 2:
    		beAntiSocial();
    		buildOrder++;
    		break;
    	case 3:
    		beAntiSocial();
    		buildOrder++;
    		break;
    	case 4:
    		beAntiSocial();
    		buildOrder++;
    		break;
    	case 5:
    		beAntiSocial();
    		buildOrder++;
    		break;
    	case 6:
    		beAntiSocial();
    		formation =new Formation(this.robotController,lastDirection,Formation.Form.C);
    		buildOrder++;
    		break;
    	case 7:
    		if(formation.plant()) buildOrder++;

    		
    	default:
    		maintainer.maintain();
    		deterministicBuild();
    			
    	}
    	
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
    
    Direction lastDirection;
    
    /**
     * run away from enemy robots
     * @return
     * true - success
     * false - failure
     */
    boolean beAntiSocial(){
    	try {
	        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, robotController.getTeam());
	    	
	        if (enemyRobots.length > 0) {
	            // If there is an enemy robot, move away from it
	            if (rightHanded) {
	            	lastDirection = robotController.getLocation().directionTo(enemyRobots[0].getLocation()).opposite().rotateRightDegrees(30);
	                tryMove(robotController, lastDirection);
	            }
	            if (!robotController.hasMoved()) {
	            	lastDirection= robotController.getLocation().directionTo(enemyRobots[0].getLocation()).opposite().rotateLeftDegrees(30);
	                tryMove(robotController,lastDirection);
	            }
	        } 
	        return true;
        } catch (GameActionException e) {
				//something happened. You can't get away.
				e.printStackTrace();
				return false;
		}
    }
    
    int buildType = 0;
    
    RobotType[] robotBuildOrder = new RobotType[]
    		{RobotType.SCOUT,
    				RobotType.SOLDIER,
    				RobotType.LUMBERJACK,
    				RobotType.LUMBERJACK,
    				RobotType.SOLDIER,
    				RobotType.SOLDIER,
    				RobotType.TANK,
    				RobotType.SOLDIER,
    				RobotType.TANK
    		}; 
    
    RobotType getDeterministicType(){
    	return robotBuildOrder[buildType++%robotBuildOrder.length];
    }
    
    void deterministicBuild() {
        Direction dir = randomDirection();
        
        Direction[] complimentaryAngles = formation.getComplimentaryAngles();
        
        if(complimentaryAngles.length>0){
        	dir = complimentaryAngles[rand.nextInt(complimentaryAngles.length)];
        }
        
         try {
        	 
        	 RobotType buildType= getDeterministicType();
        	 
        	 if(this.robotController.getTeamBullets() > (buildType.bulletCost + 100 + 50) && this.robotController.isBuildReady()){
        		 System.out.println("I have enough money to build a tank");
        		 
        		 if(this.robotController.canBuildRobot(buildType, dir)){
        			 System.out.println("I am trying to build a tank");
        			 this.robotController.buildRobot(buildType, dir);
        		 } else {
        			 System.out.println(" for some reason I still can't build a tank.");
        			 System.out.println("direction:" + dir);
        			 formation.print();
        			 
        		 }
        		 
        	 }

        } catch (GameActionException e) {
				e.printStackTrace();
		}
    }
}
