package boidroles.roles;

import battlecode.common.BodyInfo;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;
import boidroles.util.RobotBase;

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
        float strikingRange = RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;
        BodyInfo[] enemyRobots = robotController.senseNearbyRobots(strikingRange, robotController.getTeam().opponent());
        BodyInfo[] friendlyRobots = robotController.senseNearbyRobots(strikingRange, robotController.getTeam());
        TreeInfo[] enemyTrees = robotController.senseNearbyTrees(strikingRange, robotController.getTeam().opponent());
        TreeInfo[] friendlyTrees = robotController.senseNearbyTrees(strikingRange, robotController.getTeam());
        TreeInfo[] neutralTrees = robotController.senseNearbyTrees(strikingRange, Team.NEUTRAL);
        if (robotController.canStrike()
                && enemyRobots.length > 0
                && (enemyRobots.length + enemyTrees.length > friendlyRobots.length + friendlyTrees.length)) {
            robotController.strike();
        } else {
            if (enemyTrees.length < 0 || !tryChop(enemyTrees)) {
                tryChop(neutralTrees);
            }
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
