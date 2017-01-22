package boidroles.util;

import battlecode.common.BodyInfo;
import battlecode.common.BulletInfo;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import boidroles.roles.Archon;
import boidroles.roles.Gardener;
import boidroles.roles.Lumberjack;
import boidroles.roles.Scout;
import boidroles.roles.Soldier;
import boidroles.roles.Tank;

public abstract class RobotBase {
    protected RobotController robotController;
    protected RobotInfo[] sensedRobots;
    protected TreeInfo[] sensedTrees;
    protected BulletInfo[] sensedBullets;

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

    protected boolean tryMove(Direction dir, float distance) throws GameActionException {
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
        //System.out.println("I am!");
    }

    public void beforeRun() throws GameActionException {
        sensedRobots = robotController.senseNearbyRobots();
        sensedTrees = robotController.senseNearbyTrees();
        sensedBullets = robotController.senseNearbyBullets();
        //System.out.println("I'm a bot!");
        detectArchons();

//                Vector requiredmovement = new Vector();
//                Vector movement = maintainSeparation(nearby);
//                movement.add(maintainAlignment(nearby));
//                movement.add(maintainCohesion(nearby));
        //tryMove(movement.direction, movement.distance);
    }

    public abstract void run() throws GameActionException;

    protected abstract Vector calculateInfluence() throws GameActionException;

    protected Vector dodgeBullets(BulletInfo[] bullets) throws GameActionException {
        Vector movement = new Vector();
        for (BulletInfo bullet : bullets) {
            float theta = bullet.getDir().radiansBetween(bullet.getLocation().directionTo(robotController.getLocation()));
            // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
            if (Math.abs(theta) > Math.PI / 2) {
                break;
            }
            robotController.setIndicatorDot(bullet.getLocation(), 255, 0, 0);
            MapLocation intersection = robotController.getLocation()
                    .add(bullet.getDir(), (float) Math.abs(robotController.getLocation().distanceTo(bullet.getLocation()) * Math.cos(theta)));
            movement.add(new Vector(robotController.getLocation().directionTo(intersection).opposite(),
                    robotController.getType().strideRadius * 2f)
                    .scale(getInverseScaling(intersection)));
            robotController.setIndicatorLine(bullet.getLocation(), intersection, 255, 0, 0);
            robotController.setIndicatorLine(robotController.getLocation(), intersection, 255, 255, 0);
//            MapLocation nearBullet = bullet.getLocation().add(intersection.directionTo(robotController.getLocation()),
//                    1.1f * robotController.getType().bodyRadius);
//            movement.add(new Vector(robotController.getLocation().directionTo(nearBullet),
//                    robotController.getType().strideRadius * 2f)
//                    .scale(getScaling(intersection)));
        }
        return movement;
    }

    protected Vector getInfluenceFromInitialEnemyArchonLocations(boolean attract, float scale) {
        Vector movement = new Vector();
        for (MapLocation archonLocation : robotController.getInitialArchonLocations(robotController.getTeam().opponent())) {
            Direction direction = robotController.getLocation().directionTo(archonLocation);
            movement.add(new Vector(attract ? direction : direction.opposite(),
                    robotController.getType().strideRadius).scale(scale));
        }
        return movement;
    }

    protected Vector getInfluenceFromTreesWithBullets(TreeInfo[] trees) {
        Vector movement = new Vector();
        for (TreeInfo tree : trees) {
            if (tree.getContainedBullets() > 0) {
                movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()),
                        robotController.getType().strideRadius * 1.5f)
                        .scale(getScaling(tree.getLocation())));
            }
        }
        return movement;
    }

    protected Vector getInfluenceFromTrees(TreeInfo[] trees) {
        Vector movement = new Vector();
        for (TreeInfo tree : trees) {
            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(),
                    robotController.getType().strideRadius)
                    .scale(getInverseScaling(tree.getLocation())));
        }
        return movement;
    }

    public void afterRun() throws GameActionException {
        shakeTrees();
        detectArchons();
        //markIncoming();
        if (robotController.getTeamVictoryPoints() + (robotController.getTeamBullets() / 10) >= 1000) {
            //if current victory points plus all our bullets turned into victory points is at least 1k, sell all bullets
            robotController.donate(robotController.getTeamBullets());
        }
        //System.out.println("We're done here!");
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

    //private void markIncoming() throws GameActionException {
//        BulletInfo[] bullets = robotController.senseNearbyBullets(-1);
//        for (BulletInfo bullet : bullets) {
//            float theta = bullet.getDir().radiansBetween(bullet.getLocation().directionTo(robotController.getLocation()));
//            // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
//            if (Math.abs(theta) > Math.PI / 2) {
//                continue;
//            }
////            robotController.setIndicatorLine(bullet.getLocation().add(bullet.getDir(), 6 * bullet.getSpeed()), bullet.getLocation().add(bullet.getDir(), 8 * bullet.getSpeed()), 255, 255, 0);
////            robotController.setIndicatorLine(bullet.getLocation().add(bullet.getDir(), 4 * bullet.getSpeed()), bullet.getLocation().add(bullet.getDir(), 6 * bullet.getSpeed()), 255, 115, 0);
////            robotController.setIndicatorLine(bullet.getLocation().add(bullet.getDir(), 2 * bullet.getSpeed()), bullet.getLocation().add(bullet.getDir(), 4 * bullet.getSpeed()), 255, 70, 0);
////            robotController.setIndicatorLine(bullet.getLocation(), bullet.getLocation().add(bullet.getDir(), 2 * bullet.getSpeed()), 255, 0, 0);
////            robotController.setIndicatorDot(bullet.getLocation(), 0, 0, 0);
//
//            if (willCollideWithMe(bullet)) {
////                MapLocation collision = bullet.getLocation().add(bullet.getDir(), );
////                robotController.setIndicatorDot(bullet.getLocation(), 0, 0, 0);
//                robotController.setIndicatorDot(robotController.getLocation(), 255, 0, 0);
//                //robotController.setIndicatorLine(bullet.getLocation(), bullet.getLocation().add(bullet.getDir(), 2 * bullet.getSpeed()), 255, 255, 0);
////
//            }
//        }
//        return bullets.length > 0;
//    }

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

    protected void detectArchons() throws GameActionException {
        if (robotController.readBroadcast(2) != 0 && robotController.readBroadcast(2) + 15 < robotController.getRoundNum()) {
            robotController.broadcast(0, 0);
            robotController.broadcast(1, 0);
            robotController.broadcast(2, 0);
        }
        for (RobotInfo robot : sensedRobots) {
            if (robot.getTeam().opponent().equals(robot.getTeam()) && RobotType.ARCHON.equals(robot.getType())) {
                MapLocation enemyArchonLocation = robot.getLocation();
                robotController.broadcast(0, (int) enemyArchonLocation.x);
                robotController.broadcast(1, (int) enemyArchonLocation.y);
                robotController.broadcast(2, robotController.getRoundNum());
            }
        }
    }

    private void shakeTrees() throws GameActionException {
        if (robotController.canShake()) {
            for (TreeInfo tree : sensedTrees) {
                if (tree.getContainedBullets() > 0 && robotController.canInteractWithTree(tree.getID())) {
                    robotController.shake(tree.getID());
                    break;
                }
            }
        }
    }

    protected MapLocation projectBulletLocation(BulletInfo bulletInfo) {
        return projectBulletLocation(bulletInfo, 1);
    }

    protected MapLocation projectBulletLocation(BulletInfo bulletInfo, int rounds) {
        return bulletInfo.getLocation().add(bulletInfo.getDir(), rounds * bulletInfo.getSpeed());
    }

    protected float distanceToIntersection(MapLocation a, MapLocation b, BodyInfo target) {
        float triangleArea = Math.abs((b.x - a.x) * (target.getLocation().y - a.y) - (target.getLocation().x - a.x) * (b.y - a.y));
        float triangleHeight = triangleArea / a.distanceTo(b);
        if (triangleHeight < target.getRadius()
                && a.distanceTo(target.getLocation()) < a.distanceTo(b)
                && b.distanceTo(target.getLocation()) < b.distanceTo(a)) {
            return triangleHeight;
        } else {
            return -1;
        }
    }

    protected Vector vectorToIntersection(BodyInfo target, Direction direction) {
        float targetDistance = robotController.getLocation().distanceTo(target.getLocation());
        float theta = direction.radiansBetween(target.getLocation().directionTo(robotController.getLocation()));
        MapLocation intersection = target.getLocation().add(direction, (float) Math.abs(targetDistance * Math.cos(theta)));
        return new Vector(robotController.getLocation().directionTo(intersection), robotController.getLocation().distanceTo(intersection));
    }

    private boolean hasLineOfSight(BodyInfo target) {
        return hasLineOfSight(target, false);
    }

    private boolean hasLineOfSight(BodyInfo target, boolean returnValueIfTargetNotInRange) {
        boolean targetDetected = false;
        BodyInfo[][] arrays = {sensedTrees, sensedRobots};
        for (BodyInfo[] array : arrays) {
            for (BodyInfo thing : array) {
                if (thing.getID() == target.getID()) {
                    targetDetected = true;
                    continue;
                } else if (robotController.getID() == target.getID()) {
                    continue;
                }
                if (distanceToIntersection(robotController.getLocation(), target.getLocation(), thing) >= 0) {
                    return false;
                }
            }
        }
        return targetDetected || returnValueIfTargetNotInRange;
    }

    //private boolean obstructsLineOfSight(BodyInfo target, BodyInfo thing) {
    //    return distanceToIntersection(robotController.getLocation(), target.getLocation(), thing) <=
    //            target.getRadius() + thing.getRadius();
    //}
    //
    //private boolean isLineOfSightObstructedBy(BodyInfo target, BodyInfo[] things) {
    //    for (BodyInfo thing : things) {
    //        if (thing.getID() != target.getID() && robotController.getID() != target.getID() && obstructsLineOfSight(target, thing)) {
    //            return true;
    //        }
    //    }
    //    return false;
    //}
    //
    //private boolean hasLineOfSight(BodyInfo target) {
    //    return hasLineOfSight(target, false);
    //}
    //
    //private boolean hasLineOfSight(BodyInfo target, boolean ignoreRobots) {
    //    return !isLineOfSightObstructedBy(target, robotController.senseNearbyTrees()) &&
    //            (ignoreRobots || !isLineOfSightObstructedBy(target, robotController.senseNearbyRobots()));
    //}

    protected boolean attackClosestEnemy() throws GameActionException {
        for (RobotInfo robot : sensedRobots) {
            if (robotController.getTeam().opponent().equals(robot.getTeam())
                    && robotController.canFireSingleShot()
                    && hasLineOfSight(robot)) {
                robotController.fireSingleShot(robotController.getLocation().directionTo(robot.location));
                return true;
            }
        }
        return false;
    }

//    protected boolean attackArchons() throws GameActionException {
//        // Listen for enemy Archon's location
//        int xPos = robotController.readBroadcast(0);
//        int yPos = robotController.readBroadcast(1);
//        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);
//
//        if (robotController.canFireSingleShot() && hasLineOfSight(enemyArchonLoc, true)) {
//            robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc));
//        }
//        return false;
//    }

    // this returns a direction and distance that the unit desires to travel based on the objects it can sense
    // todo: also use things it can remember and information from broadcast
//    private Vector calculateInfluenceExample(BodyInfo bodyInfo) throws GameActionException {
//        //todo: have separate influence calculation methods for each class
//        Vector v = new Vector();
//        if (bodyInfo.isRobot()) {
//            RobotInfo robot = (RobotInfo) bodyInfo;
//            if (robotController.getTeam().equals(robot.getTeam())) {
//                v.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()), robotController.getType().strideRadius).scale(1f));
//                v.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(), robotController.getType().strideRadius).scale(1f));
//            } else {
//                if (RobotType.SCOUT.equals(robotController.getType()) || RobotType.SOLDIER.equals(robotController.getType()) || RobotType.TANK.equals(robotController.getType())) {
//                    v.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()), robotController.getType().strideRadius).scale(1f));
//                }
//                v.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(), robotController.getType().strideRadius).scale(1f));
//            }
//        } else if (bodyInfo.isTree()) {
//            TreeInfo tree = (TreeInfo) bodyInfo;
//            if (RobotType.LUMBERJACK.equals(robotController.getType())) {
//                if (robotController.getTeam().equals(tree.getTeam())) {
//                    v.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(), GameConstants.LUMBERJACK_STRIKE_RADIUS).scale(1f));
//                } else if (!Team.NEUTRAL.equals(tree.getTeam())) {
//                    v.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()), GameConstants.LUMBERJACK_STRIKE_RADIUS).scale(1f));
//                }
//            } else if (RobotType.GARDENER.equals(robotController.getType()) && robotController.getTeam().equals(tree.getTeam())) {
//                v.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()), robotController.getType().strideRadius).scale(1f));
//            } else {
//                v.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(), robotController.getType().strideRadius).scale(1f));
//            }
//        } else if (bodyInfo.isBullet()) {
//            BulletInfo bullet = (BulletInfo) bodyInfo;
//            float distance = distanceToIntersection(bullet.getLocation(), projectBulletLocation(bullet), robotController.senseRobot(robotController.getID()));
//            if (distance <= 0) {
//                v.add(new Vector(robotController.getLocation().directionTo(projectBulletLocation(bullet)).opposite(), robotController.getType().strideRadius).scale(1f));
//            }
//        }
//        return v;
//    }

    protected float getScaling(MapLocation location) {
        return robotController.getLocation().distanceTo(location) / robotController.getType().sensorRadius;
    }

    protected float getInverseScaling(MapLocation location) {
        return 1 - getScaling(location);
    }

    protected void outputInfluenceDebugging(String title, Vector movement) {
        outputInfluenceDebugging(title, null, movement);
    }

    protected void outputInfluenceDebugging(String title, BodyInfo target, Vector movement) {
        String from = target == null ? "" : " from (" + target.getLocation().x + "," + target.getLocation().y + ") ";
        System.out.println(title + from + " on (" + robotController.getLocation().x + "," + robotController.getLocation().y + "): ("
                + movement.dx + "," + movement.dy + ")");
    }

    public void debugBytecodeUsed(String title) {
        System.out.println(title + ": "
                + (100f * Clock.getBytecodesLeft() / Clock.getBytecodeNum()) + "% ("
                + (Clock.getBytecodeNum() - Clock.getBytecodesLeft()) + ") of Bytecode used.");
    }
}
