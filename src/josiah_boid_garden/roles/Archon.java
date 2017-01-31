package josiah_boid_garden.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import josiah_boid_garden.boid.Boid;
import josiah_boid_garden.boid.RobotResponse;
import josiah_boid_garden.gardening.ForestationChecker;
import josiah_boid_garden.util.GlobalMap;
import josiah_boid_garden.util.RobotBase;
import josiah_boid_garden.util.Util;


public class Archon extends RobotBase {
    
	private final int MAXTRIES = 32;
	
	int nextGardenerTurn = 0;
	int gardenerPeriodicity = 25;
	
	GlobalMap map;
	
	float forestation = 0;
	
	RobotInfo[] robots;
	TreeInfo[] trees;
	
	public Archon(RobotController robotController) {
        super(robotController);
        map = new GlobalMap(this.robotController);
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
	        
	        Boid controller = new Boid (this.robotController);
	        
	        RobotResponse RR = new RobotResponse(controller , 45);
	        
	        
	        //run away from bad guys
	        RobotInfo[] enemyRobots = robotController.senseNearbyRobots( -1, robotController.getTeam().opponent() );
	        RR.runAway(robots);
	        
	        RobotInfo[] allyRobots = robotController.senseNearbyRobots( -1 , robotController.getTeam()); 
        	RR.runAway(allyRobots);
        	
	        if(map.check()){
	        	controller.applyConstantRotationalForce(
	        			new MapLocation(map.getXOffset() + map.getWidth()/2 , map.getYOffset() + map.getWidth()/2),
	        			true,
	        			3);
	        	
	        }
        	
        	controller.apply();
        } catch (Exception e){
        	System.out.println(e.getMessage());
        }

        
        
  

    }
    
    boolean shouldIBuildAGardener(){
    	if(this.robotController.getRoundNum() > nextGardenerTurn){
    		
    		if(forestation < 0.2){
    			nextGardenerTurn += gardenerPeriodicity;
    			System.out.println(" Clear land. Less than 20% forestation");
    		} else if (forestation < 0.5){
    			nextGardenerTurn += gardenerPeriodicity * 2;
    			System.out.println(" Moderatly forested. Less than 50%-70% forestation");
    		} else if (forestation < 0.7 ){
    			System.out.println(" Heavily forested. 70%-90% forestation");
    			nextGardenerTurn += gardenerPeriodicity*3;
    		} else {
    			System.out.println("Severly forested land. 90%+ forest");
    			nextGardenerTurn += gardenerPeriodicity * 4;
    		}
    		gardenerPeriodicity /= 2;
    		
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
