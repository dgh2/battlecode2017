package boids;

import battlecode.common.BodyInfo;
import battlecode.common.BulletInfo;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

import java.util.Arrays;

@SuppressWarnings("unused")
public strictfp class RobotPlayer {
    private static RobotController rc;

    private static BodyInfo[] concatArrays(BodyInfo[]... arrays) {
        int length = 0;
        for (BodyInfo[] array : arrays) {
            length += array.length;
        }
        BodyInfo[] result = new BodyInfo[length];
        int pos = 0;
        for (BodyInfo[] array : arrays) {
            for (BodyInfo element : array) {
                result[pos] = element;
                pos++;
            }
        }
        return result;
    }

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;

        //noinspection InfiniteLoopStatement
        do {
            try {
                for (RobotInfo infoTest : rc.senseNearbyRobots()) {
                    if (infoTest == null) {
                        System.out.println("I'm sensing null things on round " + rc.getRoundNum());
                    }
                }
                BodyInfo[] nearby = concatArrays(rc.senseNearbyRobots(), rc.senseNearbyBullets(), rc.senseNearbyTrees());

                Vector movement = new Vector();
                for (BodyInfo thing : nearby) {
                    movement.add(calculateInfluence(thing));
                }

//                Vector movement = maintainSeparation(nearby);
//                movement.add(maintainAlignment(nearby));
//                movement.add(maintainCohesion(nearby));

                tryMove(movement.getDirection(), movement.getDistance());

                switch (rc.getType()) {
                    case ARCHON: {
                        runArchon();
                        break;
                    }
                    case GARDENER: {
                        runGardener();
                        break;
                    }
                    case SCOUT: {
                        runScout();
                        break;
                    }
                    case SOLDIER: {
                        runSoldier();
                        break;
                    }
                    case LUMBERJACK: {
                        runLumberjack();
                        break;
                    }
                    case TANK: {
                        runTank();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
            Clock.yield();
        } while (true);
    }

    // this returns a direction and distance that the unit desires to travel based on the objects it can sense
    // todo: also use things it can remember and information from broadcast
    private static Vector calculateInfluence(BodyInfo bodyInfo) throws GameActionException {
        //todo: have separate influence calculation methods for each class
        Vector v = new Vector();
        if (bodyInfo.isRobot()) {
            RobotInfo robot = (RobotInfo) bodyInfo;
            if (rc.getTeam().equals(robot.getTeam())) {
                v.add(new Vector(rc.getLocation().directionTo(robot.getLocation()), rc.getType().strideRadius).scale(1f));
                v.add(new Vector(rc.getLocation().directionTo(robot.getLocation()).opposite(), rc.getType().strideRadius).scale(1f));
            } else {
                if (RobotType.SCOUT.equals(rc.getType()) || RobotType.SOLDIER.equals(rc.getType()) || RobotType.TANK.equals(rc.getType())) {
                    v.add(new Vector(rc.getLocation().directionTo(robot.getLocation()), rc.getType().strideRadius).scale(1f));
                }
                v.add(new Vector(rc.getLocation().directionTo(robot.getLocation()).opposite(), rc.getType().strideRadius).scale(1f));
            }
        } else if (bodyInfo.isTree()) {
            TreeInfo tree = (TreeInfo) bodyInfo;
            if (RobotType.LUMBERJACK.equals(rc.getType())) {
                if (rc.getTeam().equals(tree.getTeam())) {
                    v.add(new Vector(rc.getLocation().directionTo(tree.getLocation()).opposite(), GameConstants.LUMBERJACK_STRIKE_RADIUS).scale(1f));
                } else if (!Team.NEUTRAL.equals(tree.getTeam())) {
                    v.add(new Vector(rc.getLocation().directionTo(tree.getLocation()), GameConstants.LUMBERJACK_STRIKE_RADIUS).scale(1f));
                }
            } else if (RobotType.GARDENER.equals(rc.getType()) && rc.getTeam().equals(tree.getTeam())) {
                v.add(new Vector(rc.getLocation().directionTo(tree.getLocation()), rc.getType().strideRadius).scale(1f));
            } else {
                v.add(new Vector(rc.getLocation().directionTo(tree.getLocation()).opposite(), rc.getType().strideRadius).scale(1f));
            }
        } else if (bodyInfo.isBullet()) {
            BulletInfo bullet = (BulletInfo) bodyInfo;
            float distance = distanceToIntersection(bullet.getLocation(), projectBulletLocation(bullet), rc.senseRobot(rc.getID()));
            if (distance <= 0) {
                v.add(new Vector(rc.getLocation().directionTo(projectBulletLocation(bullet)).opposite(), rc.getType().strideRadius).scale(1f));
            }
        }
        return v;
    }

    /**
     * Returns a random Direction
     *
     * @return a random Direction
     */
    private static Direction randomDirection() {
        return new Direction((float) Math.random() * 2 * (float) Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float distance) throws GameActionException {
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
    static boolean tryMove(Direction dir, float distance, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir, distance)) {
            rc.move(dir, distance);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        for (int i = 1; i <= checksPerSide; i++) {
            // Try the offset of the left side
            if (rc.canMove(dir.rotateLeftDegrees(degreeOffset * i), distance)) {
                rc.move(dir.rotateLeftDegrees(degreeOffset * i), distance);
                return true;
            }
            // Try the offset on the right side
            if (rc.canMove(dir.rotateRightDegrees(degreeOffset * i), distance)) {
                rc.move(dir.rotateRightDegrees(degreeOffset * i), distance);
                return true;
            }
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
    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

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

        return (perpendicularDist <= rc.getType().bodyRadius);
    }

    private static MapLocation projectBulletLocation(BulletInfo bulletInfo) {
        return projectBulletLocation(bulletInfo, 1);
    }

    private static MapLocation projectBulletLocation(BulletInfo bulletInfo, int rounds) {
        return bulletInfo.getLocation().add(bulletInfo.getDir(), bulletInfo.getSpeed());
    }

    private static float distanceToIntersection(MapLocation a, MapLocation b, BodyInfo target) {
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

    private static boolean hasLineOfSight(BodyInfo target) {
        return hasLineOfSight(target, false);
    }

    private static boolean hasLineOfSight(BodyInfo target, boolean returnValueIfTargetNotInRange) {
        boolean targetDetected = false;
        BodyInfo[][] arrays = {rc.senseNearbyTrees(), rc.senseNearbyRobots()};
        for (BodyInfo[] array : arrays) {
            for (BodyInfo thing : array) {
                if (thing.getID() == target.getID()) {
                    targetDetected = true;
                    continue;
                } else if (rc.getID() == target.getID()) {
                    continue;
                }
                if (distanceToIntersection(rc.getLocation(), target.getLocation(), thing) >= 0) {
                    return false;
                }
            }
        }
        return targetDetected || returnValueIfTargetNotInRange;
    }

    //private static boolean obstructsLineOfSight(BodyInfo target, BodyInfo thing) {
    //    return distanceToIntersection(rc.getLocation(), target.getLocation(), thing) <= 0;
    //}
    //
    //private static boolean isLineOfSightObstructedBy(BodyInfo target, BodyInfo[] things) {
    //    for (BodyInfo thing : things) {
    //        if (thing.getID() != target.getID() && rc.getID() != target.getID() && obstructsLineOfSight(target, thing)) {
    //            return true;
    //        }
    //    }
    //    return false;
    //}
    //
    //private static boolean hasLineOfSight(BodyInfo target) {
    //    return hasLineOfSight(target, false);
    //}
    //
    //private static boolean hasLineOfSight(BodyInfo target, boolean ignoreRobots) {
    //    return !isLineOfSightObstructedBy(target, rc.senseNearbyTrees()) &&
    //            (ignoreRobots || !isLineOfSightObstructedBy(target, rc.senseNearbyRobots()));
    //}

    private static void attackClosestEnemy() throws GameActionException {
        Team enemyTeam = rc.getTeam().opponent();

        RobotInfo[] enemies = rc.senseNearbyRobots(-1, enemyTeam);
        Arrays.sort(enemies, (o1, o2) -> Float.compare(o1.getLocation().distanceTo(rc.getLocation()), o2.getLocation().distanceTo(rc.getLocation())));

        if (enemies.length > 0) {
            for (RobotInfo enemy : enemies) {
                if (rc.canFireSingleShot() && hasLineOfSight(enemy)) {
                    rc.fireSingleShot(rc.getLocation().directionTo(enemy.location));
                    break;
                }
            }
        }
    }

    private static void runArchon() throws GameActionException {
        // Generate a random direction
        Direction dir = randomDirection();

        // Randomly attempt to build a Gardener in this direction
        if (rc.canHireGardener(dir) && Math.random() < .3) {
            rc.hireGardener(dir);
        }
    }

    private static void runGardener() throws GameActionException {
        // Generate a random direction
        Direction dir = randomDirection();

        // Randomly attempt to build a Soldier or lumberjack in this direction
        if (rc.canBuildRobot(RobotType.SCOUT, dir) && (rc.getRoundNum() <= 4 || Math.random() < .5) && rc.isBuildReady()) {
            rc.buildRobot(RobotType.SCOUT, dir);
        } else if (rc.canPlantTree(dir) && Math.random() < .3) {
            rc.plantTree(dir);
        } else if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .25) {
            rc.buildRobot(RobotType.SOLDIER, dir);
        } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .2 && rc.isBuildReady()) {
            rc.buildRobot(RobotType.LUMBERJACK, dir);
        } else if (rc.canBuildRobot(RobotType.TANK, dir) && Math.random() < .1 && rc.isBuildReady()) {
            rc.buildRobot(RobotType.TANK, dir);
        }
    }

    private static void runLumberjack() throws GameActionException {
        float strikingRange = RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;
        BodyInfo[] enemyRobots = rc.senseNearbyRobots(strikingRange, rc.getTeam().opponent());
        BodyInfo[] friendlyRobots = rc.senseNearbyRobots(strikingRange, rc.getTeam());
        TreeInfo[] enemyTrees = rc.senseNearbyTrees(strikingRange, rc.getTeam().opponent());
        TreeInfo[] friendlyTrees = rc.senseNearbyTrees(strikingRange, rc.getTeam());
        TreeInfo[] neutralTrees = rc.senseNearbyTrees(strikingRange, Team.NEUTRAL);
        if (rc.canStrike() && friendlyRobots.length * 2 < enemyRobots.length + enemyTrees.length && enemyRobots.length > 0) {
            rc.strike();
        } else if (enemyTrees.length > 0 || neutralTrees.length > 0) {
            tryChop(enemyTrees);
        } else if (neutralTrees.length > 0) {
            tryChop(neutralTrees);
        }
    }

    private static boolean tryChop(TreeInfo[] trees) throws GameActionException {
        for (TreeInfo tree : trees) {
            if (tryChop(tree)) {
                return true;
            }
        }
        return false;
    }

    private static boolean tryChop(TreeInfo tree) throws GameActionException {
        if (rc.canChop(tree.getLocation())) {
            if (tree.getHealth() <= GameConstants.LUMBERJACK_CHOP_DAMAGE) {
                System.out.println("Timber!");
            } else {
                System.out.println("Chop!");
            }
            rc.chop(tree.getLocation());
            return true;
        }
        return false;
    }

    private static void runScout() throws GameActionException {
        attackClosestEnemy();
    }

    private static void runSoldier() throws GameActionException {
        attackClosestEnemy();
    }

    private static void runTank() throws GameActionException {
        attackClosestEnemy();
    }
}
