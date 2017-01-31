package boidroles.util;

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
import battlecode.common.TreeInfo;
import boidroles.roles.Archon;
import boidroles.roles.Gardener;
import boidroles.roles.Lumberjack;
import boidroles.roles.Scout;
import boidroles.roles.Soldier;
import boidroles.roles.Tank;

//import static boidroles.util.Util.invSqrt;

public abstract class RobotBase {
    protected RobotController robotController;
    //todo: do some preprocessing to split these into groups based on team and type, so we don't always loop over the whole array
    //todo: maybe split into groups of things that are in view too?
    protected RobotInfo[] sensedRobots;
    protected TreeInfo[] sensedTrees;
    protected BulletInfo[] sensedBullets;
    private final float dodgeBulletsTimeout;
    private final float treesWithBulletsInfluenceTimeout;
    private final float treeInfluenceTimeout;
    private final float shakeTreesTimeout;
    private final float bulletStockpileLimit = 500f;

    protected RobotBase(RobotController robotController) {
        if (robotController == null || robotController.getType() == null) {
            throw new IllegalArgumentException("Unable to build robot from invalid RobotController!");
        }
        this.robotController = robotController;
        this.dodgeBulletsTimeout = robotController.getType().bytecodeLimit * .5f;
        this.treesWithBulletsInfluenceTimeout = robotController.getType().bytecodeLimit * .2f;
        this.treeInfluenceTimeout = robotController.getType().bytecodeLimit * .1f;
        this.shakeTreesTimeout = 100;
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

    protected Vector repelFromMapEdges(float scale) throws GameActionException {
        Vector movement = new Vector();
        Direction[] compass = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (Direction dir : compass) {
            if (!robotController.onTheMap(robotController.getLocation().add(dir, robotController.getType().sensorRadius*.95f))) {
                movement.add(new Vector(dir).scale(robotController.getType().strideRadius * scale).opposite());
            }
        }
        return movement;
    }

    protected Vector dodgeBullets(BulletInfo[] bullets) throws GameActionException {
        Vector movement = new Vector();
        float distance;
        float initialBytecodesLeft = Clock.getBytecodesLeft();
            for (BulletInfo bullet : bullets) {
            if (initialBytecodesLeft - Clock.getBytecodesLeft() >= dodgeBulletsTimeout) {
                break;
            }
            distance = robotController.getLocation().distanceTo(bullet.getLocation());
            float theta = bullet.getDir().radiansBetween(bullet.getLocation().directionTo(robotController.getLocation()));
            // If theta > /*90*/ 100 degrees, then the bullet is traveling away from us and we can skip processing it...
            // ...unless we could step into it this turn
            if (distance > robotController.getType().strideRadius + robotController.getType().bodyRadius
                    && 1.8f * Math.abs(theta) > Math.PI) {
//                robotController.setIndicatorDot(bullet.getLocation(), 0, 255, 0);
                continue;
            }
//            if (!hasLineOfSight(bullet, false, false, true)) {
            if (!hasLineOfSight(bullet)) {
//                robotController.setIndicatorDot(bullet.getLocation(), 0, 0, 0);
                continue;
            }
            //The intersection is the point on the bullet's path closest to this robot
            MapLocation intersection = bullet.getLocation()
                    .add(bullet.getDir(), (float) Math.abs(distance * Math.cos(theta)));
//            MapLocation intersection = bullet.getLocation()
//                    .add(bullet.getDir(),
//                            distanceToIntersection(bullet.getLocation(), projectBulletLocation(bullet), robotController.getLocation()));
            if (robotController.getLocation().distanceTo(intersection) < robotController.getType().bodyRadius) {
                movement.add(new Vector(robotController.getLocation().directionTo(bullet.getLocation()).rotateLeftDegrees(90),
                        robotController.getType().strideRadius * 4f)
                        .scale(getInverseScaling(intersection)));
            } else {
                movement.add(new Vector(robotController.getLocation().directionTo(intersection).opposite(),
                        robotController.getType().strideRadius * 4f)
                        .scale(getInverseScaling(intersection)));
            }
//            robotController.setIndicatorDot(robotController.getLocation(), 255, 255, 255);
//            robotController.setIndicatorDot(bullet.getLocation(), 255, 0, 0);
//            robotController.setIndicatorDot(intersection, 255, 255, 0);
//            robotController.setIndicatorLine(bullet.getLocation(), intersection, 255, 0, 0);
//            robotController.setIndicatorLine(robotController.getLocation(), intersection, 255, 255, 0);
//            MapLocation nearBullet = bullet.getLocation().add(intersection.directionTo(robotController.getLocation()),
//                    1.1f * robotController.getType().bodyRadius);
//            movement.add(new Vector(robotController.getLocation().directionTo(nearBullet),
//                    robotController.getType().strideRadius * 2f)
//                    .scale(getScaling(intersection)));
        }
        return movement;
    }

    protected Vector getInfluenceFromInitialEnemyArchonLocations(boolean attract, float scale) throws GameActionException {
        Vector movement = new Vector();
        for (MapLocation archonLocation : robotController.getInitialArchonLocations(robotController.getTeam().opponent())) {
            Direction direction = robotController.getLocation().directionTo(archonLocation);
            movement.add(new Vector(attract ? direction : direction.opposite(),
                    robotController.getType().strideRadius).scale(scale));
        }
        return movement;
    }

    protected Vector getInfluenceFromTreesWithBullets(TreeInfo[] trees) throws GameActionException {
        Vector movement = new Vector();
        float initialBytecodesLeft = Clock.getBytecodesLeft();
        for (TreeInfo tree : trees) {
            if (initialBytecodesLeft - Clock.getBytecodesLeft() >= treesWithBulletsInfluenceTimeout) {
                break;
            }
            if (tree.getContainedBullets() > 0 && (RobotType.SCOUT.equals(robotController.getType()) || hasLineOfSight(tree))) {
                movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()),
                                robotController.getType().strideRadius/* * 1.5f*/)
//                        .scale(getScaling(tree.getLocation())));
                );
            }
        }
        return movement;
    }

    protected Vector getInfluenceFromTrees(TreeInfo[] trees) throws GameActionException {
        Vector movement = new Vector();
        float initialBytecodesLeft = Clock.getBytecodesLeft();
        for (TreeInfo tree : trees) {
            if (initialBytecodesLeft - Clock.getBytecodesLeft() >= treeInfluenceTimeout) {
                break;
            }
            if (hasLineOfSight(tree)) { //commenting this out solved issue of maxing out bytecode use
                movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(),
                        robotController.getType().strideRadius)
//                        .scale(getInverseScaling(tree.getLocation()))
                );
            }
        }
        return movement;
    }

    public void afterRun() throws GameActionException {
        shakeTrees();
        detectArchons();
        //markIncoming();
        if (robotController.getType().equals(RobotType.ARCHON)){
//            if (robotController.getTeamBullets() >
//                    getDonationQty(GameConstants.VICTORY_POINTS_TO_WIN - robotController.getTeamBullets())) {
//                //robotController.donate(robotController.getTeamBullets());
//            }
            if (robotController.getTeamBullets() > 500f) { //don't amass bullets once u have enough for a tank and some activity
                float num = buyVP((robotController.getTeamBullets() - 500f));
                robotController.donate(num);
                System.out.println("donated: " + num + " factor: " + ((robotController.getRoundNum() *12.5f ) / 3000f ));
            }
            if (robotController.getRoundNum() < 100 && robotController.getTeamBullets() > 150) { //buy some while they are cheap
                robotController.donate(getDonationQty(1));
            }
        }

        System.out.println("We're done here!");
    }
//
//    private float getAllVP() { //if u donate all bullets, how many victory points do we get?
//        float vpCost = 7.5f + (robotController.getRoundNum() * 12.5f) / 3000f;
//        vpCost = robotController.getTeamBullets() / vpCost;
//        return vpCost;
//    }

    protected float buyVP( float availBullets) throws GameActionException {
        float factor = (robotController.getRoundNum() *12.5f ) / 3000f;
        float remainder = availBullets % factor;
        return (availBullets - remainder);
    }

    protected float getDonationQty( float desiredVP )  {
        //1 victory point = 7.5 bullets + (round)*12.5 / 3000
        float factor = (robotController.getRoundNum() *12.5f ) / 3000f;
        return (factor + 7.5f) * desiredVP; //returns number of bullets needed to purchase desiredVP
    }

    public void dying() throws GameActionException {
        detectArchons();
        System.out.println("Oh, what a world!");
    }

    public boolean getWillToLive() throws GameActionException {
        return robotController.getRoundNum() < GameConstants.GAME_DEFAULT_ROUNDS - 1
                || robotController.getTeamVictoryPoints() < GameConstants.VICTORY_POINTS_TO_WIN;
    }

    public final void logRobotException(String method, Exception e) throws GameActionException {
        System.out.print("An exception was caught from " + robotController.getType().name() + "." + method + "(): " + e.getMessage());
    }

//    /**
//     * A slightly more complicated example function, this returns true if the given bullet is on a collision
//     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
//     *
//     * @param bullet The bullet in question
//     * @return True if the line of the bullet's path intersects with this robot's current position.
//     */
//    private boolean willCollideWithMe(BulletInfo bullet) {
//        MapLocation myLocation = robotController.getLocation();
//
//        // Get relevant bullet information
//        Direction propagationDirection = bullet.dir;
//        MapLocation bulletLocation = bullet.location;
//
//        // Calculate bullet relations to this robot
//        Direction directionToRobot = bulletLocation.directionTo(myLocation);
//        float distToRobot = bulletLocation.distanceTo(myLocation);
//        float theta = propagationDirection.radiansBetween(directionToRobot);
//
//        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
//        if (2 * Math.abs(theta) > Math.PI) {
//            return false;
//        }
//
//        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
//        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
//        // This corresponds to the smallest radius circle centered at our location that would intersect with the
//        // line that is the path of the bullet.
//        float perpendicularDist = (float) Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)
//
//        return (perpendicularDist <= robotController.getType().bodyRadius);
//    }

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
        float initialBytecodesLeft = Clock.getBytecodesLeft();
        if (robotController.canShake()) {
            for (TreeInfo tree : sensedTrees) {
                if (initialBytecodesLeft - Clock.getBytecodesLeft() > shakeTreesTimeout
                        || !robotController.canInteractWithTree(tree.getID())) {
                    break;
                }
                if (tree.getContainedBullets() > 0) {
                    robotController.shake(tree.getID());
                    break;
                }
            }
        }
    }

    protected boolean hasLineOfSight(BodyInfo target) throws GameActionException {
        float startingBytecodesLeft = Clock.getBytecodesLeft();
        float scanRadius = 1f;
        float scanStep = 1.75f;
        float timeout = 250;
        Direction direction = robotController.getLocation().directionTo(target.getLocation());
        MapLocation location = robotController.getLocation()
                .add(direction, robotController.getType().bodyRadius - scanStep * .5f);
        do {
            location = location.add(direction, scanStep); // 2 bytecode
            if (location.distanceTo(target.getLocation()) <= target.getRadius() + scanStep * .5f) {
//                robotController.setIndicatorDot(location, 80, 200, 80);
                return true;
            }
            if (robotController.isCircleOccupiedExceptByThisRobot(location, scanRadius)) { //20 bytecode
//                robotController.setIndicatorDot(location, 200, 80, 80);
                return false;
            }
//            robotController.setIndicatorDot(location, 80, 80, 80);
        } while (startingBytecodesLeft - Clock.getBytecodesLeft() < timeout);
        return false;
    }

    protected boolean attackClosestEnemy() throws GameActionException {
        return attackClosestEnemy(RobotType.values());
    }

    protected boolean attackClosestEnemy(RobotType[] attackTypes) throws GameActionException {
        if (robotController.hasAttacked()) {
            return false;
        }
        float initialBytecodesLeft = Clock.getBytecodesLeft();
        float bytecodeLimit = robotController.getType().bytecodeLimit - initialBytecodesLeft - 100;
        float angle, distance;
        boolean skip;
        for (RobotInfo robot : sensedRobots) {
            if (initialBytecodesLeft - Clock.getBytecodesLeft() >= bytecodeLimit) {
                break;
            }
            skip = true;
            for (RobotType attackType : attackTypes) {
                if (attackType.equals(robot.getType())) {
                    skip = false;
                    break;
                }
            }
            if (skip) {
                continue;
            }
            if (robotController.getTeam().opponent().equals(robot.getTeam()) && hasLineOfSight(robot)) {
                if (robotController.canFirePentadShot() || robotController.canFireTriadShot()) {
                    distance = robotController.getLocation().distanceTo(robot.getLocation());
                    angle = (float) Math.toDegrees(Math.atan(robot.getType().bodyRadius / distance));
                    if (angle > 2 * GameConstants.PENTAD_SPREAD_DEGREES && robotController.canFirePentadShot()) {
                        robotController.firePentadShot(robotController.getLocation().directionTo(robot.location));
                    } else if (angle > GameConstants.TRIAD_SPREAD_DEGREES && robotController.canFireTriadShot()) {
                        robotController.fireTriadShot(robotController.getLocation().directionTo(robot.location));
                    } else {
                        robotController.fireSingleShot(robotController.getLocation().directionTo(robot.location));
                    }
                } else if (robotController.canFireSingleShot()) {
                    robotController.fireSingleShot(robotController.getLocation().directionTo(robot.location));
                }
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

    protected float getScaling(MapLocation location) throws GameActionException {
        return robotController.getLocation().distanceTo(location) / robotController.getType().sensorRadius;
    }

    protected float getInverseScaling(MapLocation location) throws GameActionException {
        return 1 - getScaling(location);
    }

    protected void outputInfluenceDebugging(String title, Vector movement) throws GameActionException {
        outputInfluenceDebugging(title, null, movement);
    }

    protected void outputInfluenceDebugging(String title, BodyInfo target, Vector movement) throws GameActionException {
//        String from = target == null ? "" : " from (" + target.getLocation().x + "," + target.getLocation().y + ") ";
//        System.out.println(title + from + " on (" + robotController.getLocation().x + "," + robotController.getLocation().y + "): ("
//                + movement.dx + "," + movement.dy + ")");
    }

    public void debugBytecodeUsed(String title) throws GameActionException {
//        System.out.println(title + ": "
//                + (100f * Clock.getBytecodesLeft() / Clock.getBytecodeNum()) + "% ("
//                + (Clock.getBytecodeNum() - Clock.getBytecodesLeft()) + ") of Bytecode used.");
    }

    //try to build trees at certain locations relative to gardener
    protected boolean plantGarden1() throws GameActionException {
        if(robotController.canPlantTree(Direction.NORTH)) {
            robotController.plantTree(Direction.NORTH);
            return true;
        }
        else if (robotController.canPlantTree(Direction.EAST)) {
            robotController.plantTree(Direction.EAST);
            return true;
        }
        else if (robotController.canPlantTree(Direction.SOUTH)) {
            robotController.plantTree(Direction.SOUTH);
            return true;
        }
        //maybe NE and SE as well, leaving west open for building units
        return false;
    }
    protected void maintain() throws GameActionException {
        //todo: stop sensing! use sensed trees and break on first one farther away than this limit
        TreeInfo[] trees = robotController.senseNearbyTrees(2f, robotController.getTeam());

        if(trees.length>0){
            //find lowest tree
            TreeInfo lowest = null;
            for(TreeInfo checkTree : trees){

                if(lowest == null && robotController.canWater(checkTree.getID())){
                    lowest = checkTree;
                } else if(lowest.getHealth()>checkTree.getHealth() && robotController.canWater(checkTree.getID())){
                    lowest = checkTree;
                }

            }

            if(lowest!=null && robotController.canWater(lowest.getID())){
                robotController.water(lowest.getID());
            }
        }
    }

}
