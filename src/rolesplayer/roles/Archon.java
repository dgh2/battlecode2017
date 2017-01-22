package rolesplayer.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
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
        if (robotController.canHireGardener(dir) && Math.random() < .5) {
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
