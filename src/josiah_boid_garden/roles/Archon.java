package josiah_boid_garden.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import josiah_boid_garden.gardening.ForestationChecker;
import josiah_boid_garden.util.RobotBase;
import josiah_boid_garden.util.Util;


public class Archon extends RobotBase {
    
	private final int MAXTRIES = 32;
	
	int nextGardenerTurn = 0;
	
	float forestation = 0;
	
	RobotInfo[] robots;
	TreeInfo[] trees;
	
	public Archon(RobotController robotController) {
        super(robotController);
        
    }

    @Override
    public void run() throws GameActionException {
 
        try {
	        

	        robots = this.robotController.senseNearbyRobots();
	        trees = this.robotController.senseNearbyTrees();
	        checkForestation();
	        //check to build a gardener
	        if(shouldIBuildAGardener()){
	        	this.buildAGardener();
	        }
	        
	        //run away from bad guys
	        RobotInfo[] enemyRobots = robotController.senseNearbyRobots( -1, robotController.getTeam().opponent() );
	        runAway(enemyRobots);
        	
        } catch (Exception e){
        	System.out.println(e.getMessage());
        }

        
        
  

    }
    
    boolean shouldIBuildAGardener(){
    	if(this.robotController.getRoundNum() > nextGardenerTurn){
    		
    		
    		
    		if(forestation < 0.2){
    			nextGardenerTurn += 120;
    			System.out.println(" Clear land. Less than 20% forestation");
    		} else if (forestation < 0.5){
    			nextGardenerTurn += 200;
    			System.out.println(" Moderatly forested. Less than 50%-70% forestation");
    		} else if (forestation < 0.7 ){
    			System.out.println(" Heavily forested. 70%-90% forestation");
    			nextGardenerTurn += 300;
    		} else {
    			System.out.println("Severly forested land. 90%+ forest");
    			nextGardenerTurn += 1000;
    		}
    		
    		return true;
    	} else {
    		return false;
    	}
    }
    void checkForestation(){
    	
        ForestationChecker forestCheck = new ForestationChecker();
        forestation = forestCheck.getForestationAtLocation( robotController.senseNearbyTrees() , RobotType.ARCHON.sensorRadius);
        
    }
    
    void buildAGardener() throws GameActionException{
        if (this.robotController.getTeamBullets() > RobotType.GARDENER.bulletCost){
        	
        	for (int i = 0 ; i < MAXTRIES ; i++){
        		Direction dir = Util.randomDirection();	
        		if(robotController.canHireGardener(dir)) {
        			robotController.hireGardener(dir);
        		}
        	}
        	
        }
    }
    
    void runAway(RobotInfo[] enemyRobots) throws GameActionException{
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
