package josiah_boid_garden.roles;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import josiah_boid_garden.boid.AntiSocialBehavior;
import josiah_boid_garden.boid.Boid;
import josiah_boid_garden.boid.BulletDodger;
import josiah_boid_garden.boid.TargetResponse;
import josiah_boid_garden.util.RobotBase;

public class Scout extends RobotBase {
	
	MapLocation[] archons;
	boolean startFound = false;
	
    public Scout(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
    	
    	Boid actionController = new Boid (this.robotController);
    	
    	if(!startFound)
    		archons = this.robotController.getInitialArchonLocations(this.robotController.getTeam().opponent());
    	
    	//move to the archon if you are far away from it
    	if(this.robotController.getLocation().distanceTo(archons[0])>10){
    		TargetResponse targetLockOn = new TargetResponse(actionController);
    		targetLockOn.run( archons[0] );
    	}
    	
    	//let's be honest, you're always anti-social. keep it up.
    	AntiSocialBehavior antiSocial = new AntiSocialBehavior(actionController);
    	antiSocial.run(this.robotController.senseNearbyRobots());
    	
    	//...and bullets. We don't like bullets
    	BulletDodger dodger = new BulletDodger(actionController);
    	dodger.run(this.robotController.senseNearbyBullets());
    	
    	//apply those movements
    	actionController.apply();
    	
    
    	
        // Listen for enemy Archon's location
//        int xPos = robotController.readBroadcast(0);
//        int yPos = robotController.readBroadcast(1);
//        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);
//
//        Team enemy = robotController.getTeam().opponent();
//
//        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, enemy);
//        RobotInfo[] closeEnemyRobots = robotController.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius + 2 * GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
//        if (enemyRobots.length > 0) {
//            if (closeEnemyRobots.length > 0) {
//                if (rightHanded) {
//                    tryMove(robotController, robotController.getLocation().directionTo(closeEnemyRobots[0].getLocation()).opposite().rotateRightDegrees(45).opposite());
//                }
//                if (!robotController.hasMoved()) {
//                    tryMove(robotController, robotController.getLocation().directionTo(closeEnemyRobots[0].getLocation()).opposite().rotateLeftDegrees(45).opposite());
//                }
//            } else {
//                if (rightHanded) {
//                    tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateLeftDegrees(45));
//                }
//                if (!robotController.hasMoved()) {
//                    tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateRightDegrees(45));
//                }
//            }
//            if (!robotController.hasMoved()) {
//                // Move randomly
//                tryMove(robotController, randomDirection());
//            }
//            attackClosestEnemy();
//        } else if (enemyArchonLoc.x != 0 && enemyArchonLoc.y != 0) {
//            if (rightHanded) {
//                tryMove(robotController, robotController.getLocation().directionTo(enemyArchonLoc).rotateLeftDegrees(90));
//            }
//            if (!robotController.hasMoved()) {
//                tryMove(robotController, robotController.getLocation().directionTo(enemyArchonLoc).rotateRightDegrees(90));
//            }
//            if (!robotController.hasMoved()) {
//                // Move randomly
//                tryMove(robotController, randomDirection());
//            }
//            if (robotController.canFireSingleShot() && hasLineOfSight(enemyArchonLoc)) {
//                if (rightHanded) {
//                    robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc).rotateRightDegrees((float) (1 * Math.random())));
//                } else {
//                    robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc).rotateLeftDegrees((float) (1 * Math.random())));
//                }
//            }
//        } else if (!robotController.hasMoved()) {
//            // Move randomly
//            tryMove(robotController, randomDirection());
//        }
    }
}