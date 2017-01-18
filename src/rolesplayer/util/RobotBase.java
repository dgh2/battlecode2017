package rolesplayer.util;

import battlecode.common.BulletInfo;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import rolesplayer.roles.Archon;
import rolesplayer.roles.Gardener;
import rolesplayer.roles.Lumberjack;
import rolesplayer.roles.Scout;
import rolesplayer.roles.Soldier;
import rolesplayer.roles.Tank;

public abstract class RobotBase {
    protected RobotController robotController;
    protected boolean rightHanded = Math.random() > .33;

    protected RobotBase(RobotController robotController) {
        if (robotController == null || robotController.getType() == null) {
            throw new IllegalArgumentException("Unable to build robot from invalid RobotController!");
        }
        this.robotController = robotController;
    }

    public static RobotBase createForController(RobotController robotController) {
        if (robotController == null || robotController.getType() == null) {
            throw new IllegalArgumentException("Unable to build robot from invalid RobotController!");
        }
        switch (robotController.getType()) {
            case ARCHON:
                return new Archon(robotController);
            case GARDENER:
                return new Gardener(robotController);
            case SOLDIER:
                return new Soldier(robotController);
            case TANK:
                return new Tank(robotController);
            case SCOUT:
                return new Scout(robotController);
            case LUMBERJACK:
                return new Lumberjack(robotController);
            default:
                throw new IllegalArgumentException("Unable to build robot of an unrecognized type: " + robotController.getType().name());
        }
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException thrown when an illegal action is attempted
     */
    protected static boolean tryMove(RobotController robotController, Direction dir) throws GameActionException {
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
    protected static boolean tryMove(RobotController robotController, Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

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

    public final RobotType getBaseType() {
        return robotController.getType();
    }

    public void runOnce() throws GameActionException {
        System.out.println("I am!");
    }

    public void beforeRun() throws GameActionException {
        System.out.println("I'm a bot!");
        detectArchons();
    }

    public abstract void run() throws GameActionException;

    public void afterRun() throws GameActionException {
        detectArchons();
        markIncoming();
        System.out.println("We're done here!");
    }

    public void dying() throws GameActionException {
        detectArchons();
        System.out.println("Oh, what a world!");
    }

    public boolean getWillToLive() {
        return true;
    }

    private boolean markIncoming() throws GameActionException {
        BulletInfo[] bullets = robotController.senseNearbyBullets(-1);
        for (BulletInfo bullet : bullets) {
//            robotController.setIndicatorLine(bullet.getLocation().add(bullet.getDir(), 6 * bullet.getSpeed()), bullet.getLocation().add(bullet.getDir(), 8 * bullet.getSpeed()), 255, 255, 0);
//            robotController.setIndicatorLine(bullet.getLocation().add(bullet.getDir(), 4 * bullet.getSpeed()), bullet.getLocation().add(bullet.getDir(), 6 * bullet.getSpeed()), 255, 115, 0);
//            robotController.setIndicatorLine(bullet.getLocation().add(bullet.getDir(), 2 * bullet.getSpeed()), bullet.getLocation().add(bullet.getDir(), 4 * bullet.getSpeed()), 255, 70, 0);
//            robotController.setIndicatorLine(bullet.getLocation(), bullet.getLocation().add(bullet.getDir(), 2 * bullet.getSpeed()), 255, 0, 0);
            robotController.setIndicatorDot(bullet.getLocation(), 0, 0, 0);

            if (willCollideWithMe(bullet)) {
//                MapLocation collision = bullet.getLocation().add(bullet.getDir(), );
                robotController.setIndicatorDot(robotController.getLocation(), 255, 0, 0);
            }
        }
        return bullets.length > 0;
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    private boolean willCollideWithMe(BulletInfo bullet) {
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

    private void detectArchons() throws GameActionException {
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
