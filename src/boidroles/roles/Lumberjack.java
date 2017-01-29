package boidroles.roles;

import battlecode.common.BodyInfo;
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
    public static final float STRIKE_RANGE = RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;
    private BodyInfo[] enemyRobots;
    private BodyInfo[] friendlyRobots;
    private TreeInfo[] enemyTrees;
    private TreeInfo[] friendlyTrees;
    private TreeInfo[] neutralTrees;

    public Lumberjack(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void runOnce() throws GameActionException {
        super.runOnce();
        //System.out.println("I wanted to be... A " + getBaseType().name() + "!");
    }

    @Override
    public void beforeRun() throws GameActionException {
        super.beforeRun();
        enemyRobots = robotController.senseNearbyRobots(STRIKE_RANGE, robotController.getTeam().opponent());
        friendlyRobots = robotController.senseNearbyRobots(STRIKE_RANGE, robotController.getTeam());
        enemyTrees = robotController.senseNearbyTrees(STRIKE_RANGE, robotController.getTeam().opponent());
        friendlyTrees = robotController.senseNearbyTrees(STRIKE_RANGE, robotController.getTeam());
        neutralTrees = robotController.senseNearbyTrees(STRIKE_RANGE, Team.NEUTRAL);
        detectArchons();
        //System.out.println("I am a " + getBaseType().name() + " and I'm okay");
    }

    @Override
    public void afterRun() throws GameActionException {
        super.afterRun();
        //System.out.println("I sleep all night and I work all day");
    }

    @Override
    public void dying() throws GameActionException {
        super.dying();
        //System.out.println("I never wanted to do this in the first place!");
    }

    @Override
    public void run() throws GameActionException {
        //Handle movement
        Vector movement = calculateInfluence();
        robotController.setIndicatorLine(robotController.getLocation(),
                robotController.getLocation().translate(movement.dx, movement.dy), 255, 255, 255);
        tryMove(movement.getDirection(), movement.getDistance());

        //Handle actions
        if (robotController.canStrike()
                && enemyRobots.length > 0
                && (enemyRobots.length + enemyTrees.length > friendlyRobots.length)) {
            robotController.strike();
        } else if (enemyTrees.length < 0 || !tryChop(enemyTrees)) {
            tryChop(neutralTrees);
        }
    }

    private boolean tryChop(TreeInfo[] trees) throws GameActionException {
        for (TreeInfo tree : trees) {
            if (robotController.getLocation().distanceTo(tree.getLocation()) - tree.getRadius()
                    > GameConstants.LUMBERJACK_STRIKE_RADIUS) {
                break;
            }
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
        Vector movement = new Vector();
        for (RobotInfo robot : sensedRobots) { //todo: continue if id is equal to own id
            if (!robotController.getTeam().equals(robot.getTeam())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
                        robotController.getType().strideRadius)
                        .scale(getScaling(robot.getLocation())));
//                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
//                        robotController.getType().strideRadius)
//                        .scale(getInverseScaling(robot.getLocation())));
            } else {
//                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
//                        robotController.getType().strideRadius)
//                        .scale(getScaling(robot.getLocation())));
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 3f)
                        .scale(getInverseScaling(robot.getLocation())));
            }
            if (RobotType.LUMBERJACK.equals(robot.getType())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 2f).scale(getInverseScaling(robot.getLocation())));
            }
            outputInfluenceDebugging("Lumberjack robot influence", robot, movement);
        }
        for (TreeInfo tree : sensedTrees) {
            if (tree.getContainedRobot() != null) {
                movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()),
                        robotController.getType().strideRadius * 1f)
                        .scale(getScaling(tree.getLocation())));
            }
//            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()),
//                    robotController.getType().strideRadius*.1f).scale(1f));
//            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(),
//                    robotController.getType().strideRadius * .1f)
//                    .scale(getInverseScalingUntested(tree.getLocation())));
            outputInfluenceDebugging("Lumberjack robot + tree influence", tree, movement);
        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, .2f));
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees));
//        movement.add(getInfluenceFromTrees(sensedTrees));
        //todo: stay away from our own bullet trees
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges());
        outputInfluenceDebugging("Lumberjack total influence", movement);
        return movement;
    }
}
