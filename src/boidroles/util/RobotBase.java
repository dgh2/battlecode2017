package boidroles.util;

import battlecode.common.BodyInfo;
import battlecode.common.BulletInfo;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;
import boidroles.roles.Archon;
import boidroles.roles.Gardener;
import boidroles.roles.Lumberjack;
import boidroles.roles.Scout;
import boidroles.roles.Soldier;
import boidroles.roles.Tank;

import java.util.Arrays;

import static boidroles.util.Util.concatArrays;

public abstract class RobotBase {
    protected RobotController robotController;

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
     * @throws GameActionException
     */
    boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir, robotController.getType().strideRadius);
    }

    boolean tryMove(Direction dir, float distance) throws GameActionException {
        return tryMove(dir, distance, 20, 5);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir           The intended direction of movement
     * @param degreeOffset  Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {
        return tryMove(dir, robotController.getType().strideRadius, degreeOffset, checksPerSide);
    }

    boolean tryMove(Direction dir, float distance, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (robotController.canMove(dir, distance)) {
            robotController.move(dir, distance);
            return true;
        }

        // Now try a bunch of similar angles
        for (int i = 1; i <= checksPerSide; i++) {
            // Try the offset of the left side
            if (robotController.canMove(dir.rotateLeftDegrees(degreeOffset * i), distance)) {
                robotController.move(dir.rotateLeftDegrees(degreeOffset * i), distance);
                return true;
            }
            // Try the offset on the right side
            if (robotController.canMove(dir.rotateRightDegrees(degreeOffset * i), distance)) {
                robotController.move(dir.rotateRightDegrees(degreeOffset * i), distance);
                return true;
            }
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

        //todo: remove from beforeRun and have each role implement it's own influence and movement methods
        for (RobotInfo infoTest : robotController.senseNearbyRobots()) {
            if (infoTest == null) {
                System.out.println("I'm sensing null things on round " + robotController.getRoundNum());
            }
        }
        BodyInfo[] nearby = concatArrays(robotController.senseNearbyRobots(), robotController.senseNearbyBullets(), robotController.senseNearbyTrees());

        Vector movement = new Vector();
//                Vector requiredmovement = new Vector();
        for (BodyInfo thing : nearby) {
            movement.add(calculateInfluence(thing));
        }
//                Vector movement = maintainSeparation(nearby);
//                movement.add(maintainAlignment(nearby));
//                movement.add(maintainCohesion(nearby));
        tryMove(movement.getDirection(), movement.getDistance());
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

    public final void logRobotException(String method, Exception e) {
        System.out.print("An exception was caught from " + robotController.getType().name() + "." + method + "(): " + e.getMessage());
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

    private MapLocation projectBulletLocation(BulletInfo bulletInfo) {
        return projectBulletLocation(bulletInfo, 1);
    }

    private MapLocation projectBulletLocation(BulletInfo bulletInfo, int rounds) {
        return bulletInfo.getLocation().add(bulletInfo.getDir(), bulletInfo.getSpeed());
    }

    private float distanceToIntersection(MapLocation a, MapLocation b, BodyInfo target) {
        float triangleArea = Math.abs((b.x - a.x) * (target.getLocation().y - a.y) - (target.getLocation().x - a.x) * (b.y - a.y));
        float triangleHeight = triangleArea / a.distanceTo(b);
        if (triangleHeight < target.getRadius()
                && a.distanceTo(target.getLocation()) < a.distanceTo(b)
                && b.distanceTo(target.getLocation()) < b.distanceTo(a)) {
            return triangleHeight - target.getRadius();
        } else {
            return -1;
        }
    }

    private boolean hasLineOfSight(MapLocation target) {
        return hasLineOfSight(target, false);
    }

    private boolean hasLineOfSight(MapLocation target, boolean returnValueIfTargetNotInRange) {
        boolean targetDetected = false;
        BodyInfo[] things = concatArrays(robotController.senseNearbyTrees(), robotController.senseNearbyRobots());
        for (BodyInfo thing : things) {
            if (thing.getLocation().equals(target)) {
                targetDetected = true;
                continue;
            } else if (robotController.getLocation().equals(target)) {
                continue;
            }
            if (distanceToIntersection(robotController.getLocation(), target, thing) >= 0) {
                return false;
            }
        }
        return targetDetected || returnValueIfTargetNotInRange;
    }

    protected boolean attackClosestEnemy() throws GameActionException {
        Team enemyTeam = robotController.getTeam().opponent();

        RobotInfo[] enemies = robotController.senseNearbyRobots(-1, enemyTeam);
        Arrays.sort(enemies, (o1, o2) -> Float.compare(o1.getLocation().distanceTo(robotController.getLocation()), o2.getLocation().distanceTo(robotController.getLocation())));

        if (enemies.length > 0) {
            for (RobotInfo enemy : enemies) {
                if (robotController.canFireSingleShot() && hasLineOfSight(enemy.location)) {
                    robotController.fireSingleShot(robotController.getLocation().directionTo(enemy.location));
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean attackArchons() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = robotController.readBroadcast(0);
        int yPos = robotController.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        if (robotController.canFireSingleShot() && hasLineOfSight(enemyArchonLoc, true)) {
            robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc));
        }
        return false;
    }

    // this returns a direction and distance that the unit desires to travel based on the objects it can sense
    // todo: also use things it can remember and information from broadcast
    private Vector calculateInfluence(BodyInfo bodyInfo) throws GameActionException {
        //todo: have separate influence calculation methods for each class
        Vector v = new Vector();
        if (bodyInfo.isRobot()) {
            RobotInfo robot = (RobotInfo) bodyInfo;
            if (robotController.getTeam().equals(robot.getTeam())) {
                v.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()), robotController.getType().strideRadius).scale(1f));
                v.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(), robotController.getType().strideRadius).scale(1f));
            } else {
                if (RobotType.SCOUT.equals(robotController.getType()) || RobotType.SOLDIER.equals(robotController.getType()) || RobotType.TANK.equals(robotController.getType())) {
                    v.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()), robotController.getType().strideRadius).scale(1f));
                }
                v.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(), robotController.getType().strideRadius).scale(1f));
            }
        } else if (bodyInfo.isTree()) {
            TreeInfo tree = (TreeInfo) bodyInfo;
            if (RobotType.LUMBERJACK.equals(robotController.getType())) {
                if (robotController.getTeam().equals(tree.getTeam())) {
                    v.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(), GameConstants.LUMBERJACK_STRIKE_RADIUS).scale(1f));
                } else if (!Team.NEUTRAL.equals(tree.getTeam())) {
                    v.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()), GameConstants.LUMBERJACK_STRIKE_RADIUS).scale(1f));
                }
            } else if (RobotType.GARDENER.equals(robotController.getType()) && robotController.getTeam().equals(tree.getTeam())) {
                v.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()), robotController.getType().strideRadius).scale(1f));
            } else {
                v.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(), robotController.getType().strideRadius).scale(1f));
            }
        } else if (bodyInfo.isBullet()) {
            BulletInfo bullet = (BulletInfo) bodyInfo;
            float distance = distanceToIntersection(bullet.getLocation(), projectBulletLocation(bullet), robotController.senseRobot(robotController.getID()));
            if (distance <= 0) {
                v.add(new Vector(robotController.getLocation().directionTo(projectBulletLocation(bullet)).opposite(), robotController.getType().strideRadius).scale(1f));
            }
        }
        return v;
    }
}
