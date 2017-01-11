package rolesplayer.util;

import battlecode.common.BulletInfo;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Util {
    /**
     * Returns a random Direction
     *
     * @return a random Direction
     */
    public static Direction randomDirection() {
        return new Direction((float) Math.random() * 2 * (float) Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException thrown when an illegal action is attempted
     */
    public static boolean tryMove(RobotController robotController, Direction dir) throws GameActionException {
//        if (tryMove(robotController, dir, 20, 3)) {
//            return true;
//        }
        return tryMove(robotController, dir, 20, 5);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir           The intended direction of movement
     * @param degreeOffset  Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException thrown when an illegal action is attempted
     */
    public static boolean tryMove(RobotController robotController, Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (robotController.canMove(dir)) {
            robotController.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while (currentCheck <= checksPerSide) {
            // Try the offset of the left side
            if (robotController.canMove(dir.rotateLeftDegrees(degreeOffset * currentCheck))) {
                robotController.move(dir.rotateLeftDegrees(degreeOffset * currentCheck));
                return true;
            }
            // Try the offset on the right side
            if (robotController.canMove(dir.rotateRightDegrees(degreeOffset * currentCheck))) {
                robotController.move(dir.rotateRightDegrees(degreeOffset * currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    public static boolean willCollideWithMe(RobotController robotController, BulletInfo bullet) {
        MapLocation myLocation = robotController.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI / 2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float) Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= robotController.getType().bodyRadius);
    }

    static void detectArchons(RobotController robotController) throws GameActionException {
        if (robotController.readBroadcast(2) != 0 && robotController.readBroadcast(2) + 15 < robotController.getRoundNum()) {
            robotController.broadcast(0, 0);
            robotController.broadcast(1, 0);
            robotController.broadcast(2, 0);
        }
        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, robotController.getTeam().opponent());
        for (RobotInfo enemyRobot : enemyRobots) {
            if (RobotType.ARCHON.equals(enemyRobot.getType())) {
                MapLocation enemyArchonLocation = enemyRobot.getLocation();
                robotController.broadcast(0, (int) enemyArchonLocation.x);
                robotController.broadcast(1, (int) enemyArchonLocation.y);
                robotController.broadcast(2, robotController.getRoundNum());
            }
        }
    }
}
