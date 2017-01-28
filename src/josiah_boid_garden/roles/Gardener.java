package josiah_boid_garden.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import josiah_boid_garden.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;

public class Gardener extends RobotBase {
    public Gardener(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        // Listen for home Archon's location
//        int xPos = robotController.readBroadcast(0);
//        int yPos = robotController.readBroadcast(1);
//        MapLocation archonLoc = new MapLocation(xPos, yPos);

        // Generate a random direction
        Direction dir = randomDirection();

        //scoutrush?
        if(robotController.getRoundNum() <=4) {
            robotController.buildRobot(RobotType.SCOUT, dir); // maybe 3 turns to build a scout or two or three lol
        }
        //trees!! need those haha
        if(robotController.getRoundNum() > 3 && robotController.getRoundNum() <= 20) { // 16 turns hoping to build a tree
            robotController.plantTree(dir);
        }
        //defence!?
        if(robotController.getRoundNum() > 10 && robotController.getRoundNum() <= 25) { // 15 turns hoping to build a lumberjack
            robotController.buildRobot(RobotType.LUMBERJACK, dir);
        }

        if(robotController.getRoundNum() > 500 && robotController.getRoundNum() <= 25) { // 15 turns hoping to build a lumberjack
            robotController.buildRobot(RobotType.TANK, dir);
        }
        if (robotController.canPlantTree(dir) && Math.random() < .85) {
            robotController.plantTree(dir);
        }

        if (robotController.canBuildRobot(RobotType.SCOUT, dir) && Math.random() < .1 && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.SCOUT, dir);
        } else if (robotController.canPlantTree(dir) && Math.random() < .1 && robotController.isBuildReady()) {
            robotController.plantTree(dir);
        } else if (robotController.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .15 && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.SOLDIER, dir);
        } else if (robotController.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .15 && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.LUMBERJACK, dir);
        } else if (robotController.canBuildRobot(RobotType.TANK, dir) && Math.random() < .08 && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.TANK, dir);
        }

        // Randomly attempt to build a Soldier or lumberjack in this direction
//        if (robotController.canBuildRobot(RobotType.SCOUT, dir) && (robotController.getRoundNum() <= 4 || Math.random() < .005) && robotController.isBuildReady()) {
//            robotController.buildRobot(RobotType.SCOUT, dir);
//        }
//            
//        } else if (robotController.canPlantTree(dir) && Math.random() < .05) {
//            robotController.plantTree(dir);
//        } else if (robotController.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
//            robotController.buildRobot(RobotType.SOLDIER, dir);
//        } else if (robotController.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && robotController.isBuildReady()) {
//            robotController.buildRobot(RobotType.LUMBERJACK, dir);
//        } else if (robotController.canBuildRobot(RobotType.TANK, dir) && Math.random() < .01 && robotController.isBuildReady()) {
//            robotController.buildRobot(RobotType.TANK, dir);
//        }

        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, robotController.getTeam().opponent());
        TreeInfo[] ourTrees = robotController.senseNearbyTrees(-1, robotController.getTeam());

        if (enemyRobots.length > 0) {
            // If there is an enemy robot, move away from it
            if (rightHanded) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).opposite().rotateRightDegrees(30));
            }
            if (!robotController.hasMoved()) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).opposite().rotateLeftDegrees(30));
            }
        }if (ourTrees.length > 0) {
            for (TreeInfo ourTree : ourTrees) {
                if (ourTree.getHealth() < .8 * ourTree.getMaxHealth() && robotController.canWater(ourTree.getLocation())) {
                    tryMove(robotController, robotController.getLocation().directionTo(ourTree.getLocation()));
                    robotController.water(ourTree.getLocation());
                    break;
                }
            }
        }
        if (!robotController.hasMoved() && ourTrees.length < 1) {
            // Move randomly
            tryMove(robotController, randomDirection());
        }
    }
}
