package rolesplayer.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

public class Gardener extends RobotBase {
	
	enum gState {PLANT , WATER}
	
	private gState state = gState.PLANT;
	//planting fields
	float currentPlantingDirection = 0.0f; //the direction the gardener is trying to plant
	float angleBetweenTrees = (float) (28f * Math.PI / 180f); // The number of degrees between trees
	float turnRate = (float)(7 * Math.PI / 180); // the number of degrees the gardener turns to try to plant the next plant(radians).
	int maxPlants = 7; // the maximum number of plants around the gardener.
	//watering fields
	int numberOfMaintainedTrees = 0; // This is the number of ally trees around the gardener that is being maintained by this gardener
    int currentTreeWatered = 0;// within the index of trees being maintained, this is the one that our gardener is currently maintaining.
    TreeInfo[] surroundingTrees; //the ally trees surrounding the gardener
	
	public Gardener(RobotController robotController) {
        super(robotController);
    }
    
    
    

    @Override
    public void run() throws GameActionException {
    	switch(state){
    	case PLANT:
    		if(numberOfMaintainedTrees<maxPlants){
    			System.out.println("I'm a gardener, and I'm planting trees!");
    			plantInCircle();
    		} else {
    			this.state = gState.WATER;
    		}
    		
    	case WATER:

    		System.out.println("I'm a gardener, and I'm watering trees!");	
    			waterInRange();
    		
    		break;
    	}
    	
    	createRandomRobot();
    }
    
    /**
     * rotate around the gardener and try to plant trees.
     */
    void plantInCircle(){
    	float maxTryArc = 45;
    	for(float angle = currentPlantingDirection; angle < currentPlantingDirection+maxTryArc ; angle += turnRate){
    		
    		Direction plantDirection = new Direction(angle);
    		
    		if(this.robotController.canPlantTree(plantDirection)){
    		
    			try {
    				//plant a tree. Stop trying to plant trees
					this.robotController.plantTree(plantDirection);
					this.numberOfMaintainedTrees++;
					this.currentPlantingDirection = (float) ((angle + angleBetweenTrees)%(2*Math.PI));
					
					break;
    			} catch (GameActionException e) {
    				this.numberOfMaintainedTrees = this.robotController.senseNearbyTrees(1).length;
					
				}
    			
    			break;
    		}
    	}
    	
    }
    
    void waterInRange(){
    	//If their are surrounding trees that need to be maintained AND there's a discrepancy between the number of trees being maintained 
    	if(this.surroundingTrees == null || (this.numberOfMaintainedTrees>0 && this.numberOfMaintainedTrees != this.surroundingTrees.length)){
    		//get the actual number of ally trees to maintain and by updating the array of them
    		this.surroundingTrees = this.robotController.senseNearbyTrees(2f, this.robotController.getTeam());
    		this.numberOfMaintainedTrees = this.surroundingTrees.length;
    	}
    	
    	//check if there are any surrounding trees
    	if(this.surroundingTrees.length>0){
	    	//check for valid indexes
	    	if(! (this.currentTreeWatered<this.surroundingTrees.length)){
	    		this.currentTreeWatered = 0;
	    	}
	    	//check if you can water your target tree
	    	if(this.robotController.canWater( this.surroundingTrees[this.currentTreeWatered].getID() )){
	    		
	    		//try to water it
	    		try {
					this.robotController.water( this.surroundingTrees[this.currentTreeWatered].getID() );
				} catch (GameActionException e) {
					//if an exception is thrown, clearly something died since the last time you did a survey. Survey again.
		    		//get the actual number of ally trees to maintain and by updating the array of them
		    		this.surroundingTrees = this.robotController.senseNearbyTrees(1f, this.robotController.getTeam());
		    		this.numberOfMaintainedTrees = this.surroundingTrees.length;
				}
	    		
	    		//move on to the next tree
	    		this.currentTreeWatered++;
	    	} else {
	    		//try another tree
	    		this.currentTreeWatered++;
	    	}
    	
    	} else {
    		System.out.println("There don't seem to be any trees in range!");
    	}
    	
    	//if there aren't enough trees, go into planting mode
    	if(this.numberOfMaintainedTrees<this.maxPlants){
    		this.state = gState.PLANT;
    	}
    	

    }
    /**
     * creates a random robot in a random direction.
     * @return
     * true - success
     * false - failure
     */
    boolean createRandomRobot(){
        Direction dir = randomDirection();
         try {
	        // Randomly attempt to build a Soldier or lumberjack in this direction
	        if (robotController.canBuildRobot(RobotType.SCOUT, dir) && (robotController.getRoundNum() <= 4 || Math.random() < .005) && robotController.isBuildReady()) {
					robotController.buildRobot(RobotType.SCOUT, dir);
	        } else if (robotController.canPlantTree(dir) && Math.random() < .05) {
	            robotController.plantTree(dir);
	        } else if (robotController.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
	            robotController.buildRobot(RobotType.SOLDIER, dir);
	        } else if (robotController.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && robotController.isBuildReady()) {
	            robotController.buildRobot(RobotType.LUMBERJACK, dir);
	        } else if (robotController.canBuildRobot(RobotType.TANK, dir) && Math.random() < .01 && robotController.isBuildReady()) {
	            robotController.buildRobot(RobotType.TANK, dir);
	        }
	        return true;
        } catch (GameActionException e) {
				//something happened, actually, we can't build the robot.
				e.printStackTrace();
				return false;
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
    
    /**
     * waters nearby trees.
     * @return
     */
    boolean simpleWaterTrees(){
    	try {
	        TreeInfo[] ourTrees = robotController.senseNearbyTrees(-1, robotController.getTeam());
	        //if there's a tree on our team
	        if (ourTrees.length > 0) {
	        	//go through each tree
	            for (TreeInfo ourTree : ourTrees) {
	            	//move toward the first tree with 80% hp and water it, but only if we can water it in the first place.
	                if (ourTree.getHealth() < .8 * ourTree.getMaxHealth() && robotController.canWater(ourTree.getLocation())) {
	                    tryMove(robotController, robotController.getLocation().directionTo(ourTree.getLocation()));
	                    robotController.water(ourTree.getLocation());
	                    break;
	                }
	            }
	        }
	        
	        return true;
        } catch (GameActionException e) {
				//something happened, actually, we can't build the robot.
				e.printStackTrace();
				return false;
		}
    }
    
}
