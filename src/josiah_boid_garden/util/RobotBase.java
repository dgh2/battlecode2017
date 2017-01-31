package josiah_boid_garden.util;

import battlecode.common.BodyInfo;
import battlecode.common.BulletInfo;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;
import josiah_boid_garden.roles.Archon;
import josiah_boid_garden.roles.Gardener;
import josiah_boid_garden.roles.Lumberjack;
import josiah_boid_garden.roles.Scout;
import josiah_boid_garden.roles.Soldier;
import josiah_boid_garden.roles.Tank;

import java.util.Arrays;

import static rolesplayer.util.Util.concatArrays;

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

    //variable used in afterRun()
    float lastTurnBullets = 0;

    public void afterRun() throws GameActionException {
        shakeTrees();
        detectArchons();
        //markIncoming();
        if (robotController.getTeamVictoryPoints() + getAllVP() >= 1000) {
            //if current victory points plus all our bullets turned into victory points is at least 1k, sell all bullets
            robotController.donate(robotController.getTeamBullets());
        }

        if (robotController.getTeamBullets() > 500f) { //don't amass bullets once u have enough for a tank and some activity
            robotController.donate(robotController.getTeamBullets() - 500f);
        }


        lastTurnBullets = robotController.getTeamBullets(); //record how many bullets for our comparison above next turn

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

    public boolean getWillToLive() {
        return true;
    }

//    private boolean markIncoming() throws GameActionException {
//        BulletInfo[] bullets = robotController.senseNearbyBullets(-1);
//        for (BulletInfo bullet : bullets) {
//            float theta = bullet.getDir().radiansBetween(bullet.getLocation().directionTo(robotController.getLocation()));
//            // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
//            if (Math.abs(theta) > Math.PI / 2) {
//                continue;
//            }
//
////            robotController.setIndicatorLine(bullet.getLocation().add(bullet.getDir(), 6 * bullet.getSpeed()), bullet.getLocation().add(bullet.getDir(), 8 * bullet.getSpeed()), 255, 255, 0);
////            robotController.setIndicatorLine(bullet.getLocation().add(bullet.getDir(), 4 * bullet.getSpeed()), bullet.getLocation().add(bullet.getDir(), 6 * bullet.getSpeed()), 255, 115, 0);
////            robotController.setIndicatorLine(bullet.getLocation().add(bullet.getDir(), 2 * bullet.getSpeed()), bullet.getLocation().add(bullet.getDir(), 4 * bullet.getSpeed()), 255, 70, 0);
////            robotController.setIndicatorLine(bullet.getLocation(), bullet.getLocation().add(bullet.getDir(), 2 * bullet.getSpeed()), 255, 0, 0);
//            robotController.setIndicatorDot(bullet.getLocation(), 0, 0, 0);
//
//            if (willCollideWithMe(bullet)) {
////                MapLocation collision = bullet.getLocation().add(bullet.getDir(), );
//                robotController.setIndicatorDot(robotController.getLocation(), 255, 0, 0);
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

    private void shakeTrees() throws GameActionException {
        if (robotController.canShake()) {
            TreeInfo[] trees = robotController.senseNearbyTrees(2.0f * robotController.getType().bodyRadius, Team.NEUTRAL);
            for (TreeInfo tree : trees) {
                if (tree.getContainedBullets() > 0) {
                    robotController.shake(tree.getID());
                    break;
                }
            }
        }
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

    protected boolean hasLineOfSight(MapLocation target) {
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
}
