package boidroles.roles;

import battlecode.common.BulletInfo;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import boidroles.util.RobotBase;
import boidroles.util.Vector;

import static rolesplayer.util.Util.randomDirection;

public class Gardener extends RobotBase {
    public Gardener(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        //Handle movement
        Vector movement = calculateInfluence();
        tryMove(movement.getDirection(), movement.getDistance());

        //Handle actions

        // Generate a random direction
        Direction dir = randomDirection();

        //if (rc.canBuildRobot(RobotType.SCOUT, dir) && (rc.getRoundNum() <= 4 || Math.random() < .5) && rc.isBuildReady()) {
        //    rc.buildRobot(RobotType.SCOUT, dir);
        //} else if (rc.canPlantTree(dir) && Math.random() < .3) {
        //    rc.plantTree(dir);
        //} else if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .25) {
        //    rc.buildRobot(RobotType.SOLDIER, dir);
        //} else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .2 && rc.isBuildReady()) {
        //    rc.buildRobot(RobotType.LUMBERJACK, dir);
        //} else if (rc.canBuildRobot(RobotType.TANK, dir) && Math.random() < .1 && rc.isBuildReady()) {
        //    rc.buildRobot(RobotType.TANK, dir);
        //}

        // Randomly attempt to build a Soldier or lumberjack in this direction
        if (robotController.canBuildRobot(RobotType.SCOUT, dir) && (robotController.getRoundNum() <= 4 || Math.random() < .2) && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.SCOUT, dir);
        } else if (robotController.canPlantTree(dir) && Math.random() < .1) {
            robotController.plantTree(dir);
        } else if (robotController.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .15) {
            robotController.buildRobot(RobotType.SOLDIER, dir);
        } else if (robotController.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .15 && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.LUMBERJACK, dir);
        } else if (robotController.canBuildRobot(RobotType.TANK, dir) && Math.random() < .05 && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.TANK, dir);
        }

        TreeInfo[] ourTrees = robotController.senseNearbyTrees(-1, robotController.getTeam());
        if (ourTrees.length > 0) {
            for (TreeInfo ourTree : ourTrees) {
                if (ourTree.getHealth() < .8 * ourTree.getMaxHealth() && robotController.canWater(ourTree.getLocation())) {
                    robotController.water(ourTree.getLocation());
                    break;
                }
            }
        }
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