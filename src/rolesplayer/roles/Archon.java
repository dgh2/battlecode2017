package rolesplayer.roles;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.TreeInfo;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;

public class Archon extends RobotBase {
    public Archon(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = robotController.readBroadcast(0);
        int yPos = robotController.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        // Generate a random direction
        Direction dir = randomDirection();

        // Randomly attempt to build a Gardener in this direction
        if (robotController.canHireGardener(dir) && Math.random() < .02) {
            robotController.hireGardener(dir);
        }

        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, robotController.getTeam().opponent());
        if (enemyRobots.length > 0) {
            // If there is an enemy robot, move away from it
            if (rightHanded) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).opposite().rotateRightDegrees(30));
            }
            if (!robotController.hasMoved()) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).opposite().rotateLeftDegrees(30));
            }
        } else {
            if (enemyArchonLoc.x != 0 || enemyArchonLoc.y != 0) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyArchonLoc).opposite());
            }
        }
        if (!robotController.hasMoved()) {
            // Move randomly
            tryMove(robotController, randomDirection());
        }

        
    }
    
    
    void analyzeMap(){
    	
    	int startByteCodes = Clock.getBytecodeNum();
    	
    	MapLocation[] allyArchons = this.robotController.getInitialArchonLocations(robotController.getTeam());
    	MapLocation[] enemyArchons = this.robotController.getInitialArchonLocations(robotController.getTeam().opponent());
    	
    	float x = 0;
    	float y = 0;
    	int archonCount = 0;
    	for(MapLocation loc : allyArchons){
    		x+=loc.x;
    		y+=loc.y;
    		archonCount++;
    		
    	}
    	
    	for(MapLocation loc : enemyArchons){
    		x+=loc.x;
    		y+=loc.y;
    		archonCount++;
    		
    		
    	}
    	
    	mapInfo.center = new MapLocation(  x/archonCount , y/archonCount );
    	mapInfo.size = Math.max(x/archonCount, y/archonCount) * 2f;
    	
    	TreeInfo[] nearbyTrees = robotController.senseNearbyTrees();
    	
    	
    	
    	for(TreeInfo trees : nearbyTrees){
    		
    		
    		
    	}
    	
    	
    	int cost = Clock.getBytecodeNum() - startByteCodes;
    	System.out.println("Map Analysis costs : " + cost);
    }
    
    MapInformation mapInfo = new MapInformation();
    
    class MapInformation{
    	
    	public MapLocation center;
    	public float size;
    	
    }
}
