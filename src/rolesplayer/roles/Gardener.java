package rolesplayer.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

public class Gardener extends RobotBase {
    public Gardener(RobotController robotController) {
        super(robotController);
    }

    @Override
    public RobotType getBaseType() {
        return RobotType.GARDENER;
    }

    @Override
    public void run() throws GameActionException {
        // Listen for home Archon's location
//        int xPos = robotController.readBroadcast(0);
//        int yPos = robotController.readBroadcast(1);
//        MapLocation archonLoc = new MapLocation(xPos, yPos);

        // Generate a random direction
        Direction dir = randomDirection();

        // Randomly attempt to build a Soldier or lumberjack in this direction
        if (robotController.canBuildRobot(RobotType.SCOUT, dir) && (robotController.getRoundNum() <= 4 || Math.random() < .005) && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.SCOUT, dir);
        } else if (robotController.canPlantTree(dir) && Math.random() < .05) {
            robotController.plantTree(dir);
        } else if (robotController.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
            robotController.buildRobot(RobotType.SOLDIER, dir);
        } else if (robotController.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.LUMBERJACK, dir);
        } else if (robotController.canBuildRobot(RobotType.TANK, dir) && Math.random() < .01 && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.TANK, dir);
        }

        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, robotController.getTeam().opponent());
        TreeInfo[] ourTrees = robotController.senseNearbyTrees(-1, robotController.getTeam());

        if (enemyRobots.length > 0) {
            // If there is an enemy robot, move away from it
            if (Math.random() < .5) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateLeftDegrees(90 + 30));
            } else {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateRightDegrees(90 + 30));
            }
        } else if (ourTrees.length > 0) {
            for (TreeInfo ourTree : ourTrees) {
                if (ourTree.getHealth() < .6 * ourTree.getMaxHealth() && robotController.canWater(ourTree.getLocation())) {
                    tryMove(robotController, robotController.getLocation().directionTo(ourTree.getLocation()));
                    robotController.water(ourTree.getLocation());
                    break;
                }
            }
        }
        if (!robotController.hasMoved()) {
            // Move randomly
            tryMove(robotController, randomDirection());
        }
    }
}
