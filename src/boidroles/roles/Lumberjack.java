package boidroles.roles;

import battlecode.common.BodyInfo;
import battlecode.common.BulletInfo;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;
import boidroles.util.RobotBase;
import boidroles.util.Vector;

public class Lumberjack extends RobotBase {
    public Lumberjack(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void runOnce() throws GameActionException {
        super.runOnce();
        System.out.println("I wanted to be... A " + getBaseType().name() + "!");
    }

    @Override
    public void beforeRun() throws GameActionException {
        super.beforeRun();
        detectArchons();
        System.out.println("I am a " + getBaseType().name() + " and I'm okay");
    }

    @Override
    public void afterRun() throws GameActionException {
        super.afterRun();
        System.out.println("I sleep all night and I work all day");
    }

    @Override
    public void dying() throws GameActionException {
        super.dying();
        System.out.println("I never wanted to do this in the first place!");
    }

    @Override
    public void run() throws GameActionException {
        //Handle movement
        Vector movement = calculateInfluence();
        tryMove(movement.getDirection(), movement.getDistance());

        //Handle actions
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

    @Override
    protected Vector calculateInfluence() throws GameActionException {
        RobotInfo[] nearbyRobots = robotController.senseNearbyRobots();
        TreeInfo[] nearbyTree = robotController.senseNearbyTrees();
        BulletInfo[] nearbyBullets = robotController.senseNearbyBullets();
        Vector movement = new Vector();
        for (RobotInfo robot : nearbyRobots) {
            movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()), robotController.getType().strideRadius));
            movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(), robotController.getType().strideRadius));
        }
        for (TreeInfo tree : nearbyTree) {
            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()), robotController.getType().strideRadius));
            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(), robotController.getType().strideRadius));
        }
        for (BulletInfo bullet : nearbyBullets) {
            movement.add(new Vector(robotController.getLocation().directionTo(bullet.getLocation()), robotController.getType().strideRadius));
            movement.add(new Vector(robotController.getLocation().directionTo(bullet.getLocation()).opposite(), robotController.getType().strideRadius));
        }
        //todo: repel from the map's edges too
        return movement;
    }
}
