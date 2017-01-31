package josiah_boid_garden.roles;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;
import josiah_boid_garden.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;

public class Lumberjack extends RobotBase {
    public Lumberjack(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void runOnce() {
        System.out.println("I wanted to be... A " + getBaseType().name() + "!");
    }

    @Override
    public void beforeRun() {
        System.out.println("I am a " + getBaseType().name() + " and I'm okay");
    }

    @Override
    public void afterRun() {
        System.out.println("I sleep all night and I work all day");
    }

    @Override
    public void dying() {
        System.out.println("I never wanted to do this in the first place!");
    }

    @Override
    public void run() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = robotController.readBroadcast(0);
        int yPos = robotController.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        // See if there are any enemy and friendly robots or trees within striking range (distance 1 from lumberjack's radius)
        float strikingRange = RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;
//        RobotInfo[] nearbyEnemyRobots = robotController.senseNearbyRobots(strikingRange, robotController.getTeam().opponent());
        RobotInfo[] nearbyFriendlyRobots = robotController.senseNearbyRobots(strikingRange, robotController.getTeam());
//        TreeInfo[] nearbyEnemyTrees = robotController.senseNearbyTrees(strikingRange, robotController.getTeam().opponent());
        TreeInfo[] nearbyFriendlyTrees = robotController.senseNearbyTrees(strikingRange, robotController.getTeam());
//        TreeInfo[] nearbyNeutralTrees = robotController.senseNearbyTrees(strikingRange, Team.NEUTRAL);

        // No close robots, so search for robots within sight radius
        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, robotController.getTeam().opponent());
        TreeInfo[] enemyTrees = robotController.senseNearbyTrees(-1, robotController.getTeam().opponent());
        TreeInfo[] neutralTrees = robotController.senseNearbyTrees(-1, Team.NEUTRAL);

//        MapLocation[] targets = nearbyEnemyRobots + nearbyEnemyTrees + nearbyNeutralTrees;
//        MapLocation[] friends = nearbyFriendlyRobots + nearbyFriendlyTrees;

        if (nearbyFriendlyRobots.length > 0) {
            // Too close to friendly units
            if (rightHanded) {
                tryMove(robotController, robotController.getLocation().directionTo(nearbyFriendlyRobots[0].getLocation()).opposite().rotateRightDegrees(45));
            }
            if (!robotController.hasMoved()) {
                tryMove(robotController, robotController.getLocation().directionTo(nearbyFriendlyRobots[0].getLocation()).opposite().rotateLeftDegrees(45));
            }
            nearbyFriendlyRobots = robotController.senseNearbyRobots(strikingRange, robotController.getTeam());
        } else if (nearbyFriendlyTrees.length > 0) {
            // Too close to our trees
            if (rightHanded) {
                tryMove(robotController, robotController.getLocation().directionTo(nearbyFriendlyTrees[0].getLocation()).opposite().rotateRightDegrees(5));
            }
            if (!robotController.hasMoved()) {
                tryMove(robotController, robotController.getLocation().directionTo(nearbyFriendlyTrees[0].getLocation()).opposite().rotateLeftDegrees(5));
            }
            nearbyFriendlyTrees = robotController.senseNearbyTrees(strikingRange, robotController.getTeam());
        } else if (enemyRobots.length > 0) {
            // If there is an enemy robot, move towards it
            if (rightHanded) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateRightDegrees(45));
            }
            if (!robotController.hasMoved()) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateLeftDegrees(45));
            }
        } else if (enemyTrees.length > 0) {
            // If there is an enemy tree, move towards it
            tryMove(robotController, robotController.getLocation().directionTo(enemyTrees[0].getLocation()));
        } else if (enemyArchonLoc.x > 0 || enemyArchonLoc.y > 0) {
            if (rightHanded) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyArchonLoc).rotateRightDegrees(45));
            }
            if (!robotController.hasMoved()) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyArchonLoc).rotateLeftDegrees(45));
            }
        } else if (neutralTrees.length > 0) {
            // If there is a neutral tree, move towards it
            if (rightHanded) {
                tryMove(robotController, robotController.getLocation().directionTo(neutralTrees[0].getLocation()).rotateRightDegrees(45));
            }
            if (!robotController.hasMoved()) {
                tryMove(robotController, robotController.getLocation().directionTo(neutralTrees[0].getLocation()).rotateLeftDegrees(45));
            }
        }
        if (nearbyFriendlyRobots.length == 0 && nearbyFriendlyTrees.length == 0 && robotController.canStrike() &&
                (enemyRobots.length > 0 || enemyTrees.length > 0 || neutralTrees.length > 0)) {
            //todo: figure out why this can still hit friendly units if we don't sense them. Probably them moving into range on this turn; they should stop that
            robotController.strike();
        }
        if (!robotController.hasMoved()) {
            // Move randomly
            tryMove(robotController, randomDirection());
        }
    }

    private boolean tryChop(TreeInfo[] trees) throws GameActionException {
        for (TreeInfo tree : trees) {
            if (tryChop(tree)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryChop(TreeInfo tree) throws GameActionException {
        if (robotController.canChop(tree.getLocation())) {
            if (tree.getHealth() <= GameConstants.LUMBERJACK_CHOP_DAMAGE) {
                System.out.println("Timber!");
            } else {
                System.out.println("Chop!");
            }
            robotController.chop(tree.getLocation());
            return true;
        }
        return false;
    }
}
