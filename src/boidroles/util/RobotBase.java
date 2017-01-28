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
            //todo: fix line of sight, since this incorrectly marks incoming bullets
//            if (!hasLineOfSight(bullet, false, true, false)) {
//                robotController.setIndicatorDot(bullet.getLocation(), 0, 0, 0);
//                continue;
//            }
            float theta = bullet.getDir().radiansBetween(bullet.getLocation().directionTo(robotController.getLocation()));
            // If theta > /*90*/ 100 degrees, then the bullet is traveling away from us and we can skip processing it
            if (1.8f * Math.abs(theta) > Math.PI) {
//                robotController.setIndicatorDot(bullet.getLocation(), 0, 255, 0);
                continue;
            }
            //The intersection is the point on the bullet's path closest to this robot
            MapLocation intersection = bullet.getLocation()
                    .add(bullet.getDir(), (float) Math.abs(robotController.getLocation().distanceTo(bullet.getLocation()) * Math.cos(theta)));
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
        for (TreeInfo tree : trees) {
            //todo: improve bytecode use here
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
        for (TreeInfo tree : trees) {
            //todo: improve bytecode use here
//            if (hasLineOfSight(tree)) { //commenting this out solved issue of maxing out bytecode use
                movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(),
                        robotController.getType().strideRadius)
//                        .scale(getInverseScaling(tree.getLocation()))
                );
//            }
        }
        return movement;
    }

    public void afterRun() throws GameActionException {
        shakeTrees();
        detectArchons();
        //markIncoming();

        if (robotController.getTeamVictoryPoints() + getAllVP() >= 1000) {//if current victory points plus all our bullets turned into victory points is at least 1k, sell all bullets
            robotController.donate(robotController.getTeamBullets());
        }
        if (robotController.getTeamBullets() > 500f) { //don't amass bullets once u have enough for a tank and some activity
            robotController.donate(robotController.getTeamBullets() - 500f);
        }
        if (robotController.getRoundNum() > 100 && robotController.getTeamBullets() > 150) { //buy some while they are cheap
            robotController.donate(getDonationQty(1));
        }
        System.out.println("We're done here!");
    }

    private float getAllVP() { //if u donate all bullets, how many victory points do we get?
        float vpCost = 7.5f + (robotController.getRoundNum() * 12.5f) / 3000f;
        vpCost = robotController.getTeamBullets() / vpCost;
        return vpCost;
    }

    protected float getDonationQty( float desiredVP )  {
        //1 victory point = 7.5 bullets + (round)*12.5 / 3000
        float factor = (robotController.getRoundNum() *12.5f ) / 3000f;
        return (factor + 7.5f) * desiredVP;
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

    //private void markIncoming() throws GameActionException {
//        BulletInfo[] bullets = robotController.senseNearbyBullets(-1);
//        for (BulletInfo bullet : bullets) {
//            float theta = bullet.getDir().radiansBetween(bullet.getLocation().directionTo(robotController.getLocation()));
//            // If theta > /*90*/ 100 degrees, then the bullet is traveling away from us and we can break early
//            if (1.8f * Math.abs(theta) > Math.PI) {
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
        if (2 * Math.abs(theta) > Math.PI) {
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

    protected MapLocation projectBulletLocation(BulletInfo bulletInfo) throws GameActionException {
        return projectBulletLocation(bulletInfo, 1);
    }

    protected MapLocation projectBulletLocation(BulletInfo bulletInfo, int rounds) throws GameActionException {
        return bulletInfo.getLocation().add(bulletInfo.getDir(), rounds * bulletInfo.getSpeed());
    }

    protected float distanceToIntersection(MapLocation a, MapLocation b, MapLocation c) throws GameActionException {
        float slope = (b.y - a.y) / (b.x - a.x);
        return (float) (Math.abs(c.y - (slope * c.x) - a.y + (slope * a.x)) / Math.sqrt(1 + (slope * slope)));
//        return Math.abs(c.y - (slope * c.x) - a.y + (slope * a.x)) * invSqrt(1 + (slope * slope));
        //x,y = rc.loc
        //a,b = MapLoc a; c,d = MapLoc b
//        //Coordinates are (a,b) and (c,d)
//        //the point (x,y) is the required point.
//        $a=1;
//        $b=2;
//        $c=3;
//        $d=4;
//
//        $m=($d-$b)/($c-$a);
//        //echo $m."\n";
//
//        $x=10;
//        $y=20;
//        //echo $y-($m*$x)-$b+($m*$a)."\n";
//        $distance=abs($y-($m*$x)-$b+($m*$a))/sqrt(1+($m*$m));


//        float triangleArea = Math.abs((b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y));
//        float triangleHeight = triangleArea / a.distanceTo(b);
//        if (a.distanceTo(c) < a.distanceTo(b) && b.distanceTo(c) < b.distanceTo(a)) {
//            return triangleHeight;
//        } else {
//            return -1;
//        }
    }

//    protected Vector vectorToIntersection(BodyInfo target, Direction direction) throws GameActionException {
//        float targetDistance = robotController.getLocation().distanceTo(target.getLocation());
//        float theta = direction.radiansBetween(target.getLocation().directionTo(robotController.getLocation()));
//        MapLocation intersection = target.getLocation().add(direction, (float) Math.abs(targetDistance * Math.cos(theta)));
//        return new Vector(robotController.getLocation().directionTo(intersection), robotController.getLocation().distanceTo(intersection));
//    }

    private boolean hasLineOfSight(BodyInfo target) throws GameActionException {
        return hasLineOfSight(target, false, false, false);
    }

    //todo: check for line of sight to any of target, not just target's location/center
    //todo: but also improve efficiency because calling this often quickly maxes out bytecode use
    private boolean hasLineOfSight(BodyInfo target, boolean returnValueIfTargetNotInRange, boolean ignoreTrees, boolean ignoreRobots) throws GameActionException {
        if ((robotController.getLocation().distanceTo(target.getLocation()) - robotController.getType().bodyRadius - target.getRadius())
                <= GameConstants.BULLET_SPAWN_OFFSET) {
            return true;
        }
        boolean targetDetected = false;
        BodyInfo[][] arrays = {sensedTrees, sensedRobots};
        for (BodyInfo[] array : arrays) {
            if (ignoreTrees) {
                continue;
            }
            for (BodyInfo thing : array) {
                if (thing.getID() == target.getID()) {
                    targetDetected = true;
                    continue;
                } else if (robotController.getID() == thing.getID()) {
                    continue;
                } else if (ignoreRobots && thing.isRobot()) {
                    break;
                }
                float theta1 = robotController.getLocation().directionTo(target.getLocation())
                        .radiansBetween(robotController.getLocation().directionTo(thing.getLocation()));
                float theta2 = target.getLocation().directionTo(robotController.getLocation())
                        .radiansBetween(target.getLocation().directionTo(thing.getLocation()));
                // If theta > /*90*/ 100 degrees, then the thing is not between the two us and we can skip processing it
                float limit = (float) Math.PI / 1.8f;
                if (Math.abs(theta1) > limit || Math.abs(theta2) > limit) {
                    continue;
                }
                if (distanceToIntersection(robotController.getLocation(), target.getLocation(), thing.getLocation()) < thing.getRadius() * 1.1) {
                    robotController.setIndicatorDot(thing.getLocation(), 0, 0, 0);
                    robotController.setIndicatorLine(thing.getLocation(), target.getLocation(), 42, 42, 42);
                    return false;
                }
            }
        }
        return targetDetected || returnValueIfTargetNotInRange;
    }

    //mason put this back in because he doesn't know what will happen
    protected boolean checkLineOfSight(BodyInfo robot) {
        if (hasLineOfSight(robot)){
            return true;
        }
        else {
            return false;
        }
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
        if (robotController.hasAttacked()) {
            return false;
        }
        float angle, distance;
        for (RobotInfo robot : sensedRobots) {
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

    protected float getDonationQty(float desiredVP) throws GameActionException {
    //victory point = 7.5 bullets + (round)*12.5 / 3000
    float factor = (robotController.getRoundNum() *12.5f ) / 3000f;
            return (factor + 7.5f) * desiredVP;
//    robotController.plantTree(dir);

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


}
