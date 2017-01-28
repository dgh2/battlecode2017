package josiah_boid_garden.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import josiah_boid_garden.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;



public class Archon extends RobotBase {
    public Archon(RobotController robotController) {
        super(robotController);
    }

    float lastTurnBullets = 0; //variable we will need

    @Override
    public void run() throws GameActionException {
  
        MapLocation enemyArchonLoc = new MapLocation(0, 0);

        // Generate a random direction
        Direction dir = randomDirection();


        // if turn zero or one, buy a victory point
        if(robotController.getRoundNum() <=1){
            robotController.donate(getDonationQty(1)); //getDonationQty returns #bullets based on current round's exchange rate
        }

        // Build a gardener on the first turn possible! even if that's "zero"
        if(robotController.getRoundNum() <=15) {
            if (robotController.canHireGardener(dir)) {
                robotController.hireGardener(dir);
            }
        }

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

        // Broadcast Archon's location for other robots on the team to know
//        MapLocation myLocation = robotController.getLocation();
//        robotController.broadcast(0,(int)myLocation.x);
//        robotController.broadcast(1,(int)myLocation.y);
    }
}
