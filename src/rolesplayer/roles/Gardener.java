package rolesplayer.roles;

import battlecode.common.*;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

public class Gardener extends RobotBase {
    public Gardener(RobotController rc) {
        super(rc);
    }

    @Override
    public RobotType getBaseType() {
        return RobotType.GARDENER;
    }

    @Override
    public void run() throws GameActionException {
        // Listen for home Archon's location
//        int xPos = rc.readBroadcast(0);
//        int yPos = rc.readBroadcast(1);
//        MapLocation archonLoc = new MapLocation(xPos, yPos);

        // Generate a random direction
        Direction dir = randomDirection();

        // Randomly attempt to build a Soldier or lumberjack in this direction
        if (rc.canBuildRobot(RobotType.SCOUT, dir) && (rc.getRoundNum() <= 4 || Math.random() < .005) && rc.isBuildReady()) {
            rc.buildRobot(RobotType.SCOUT, dir);
        } else if (rc.canPlantTree(dir) && Math.random() < .05) {
            rc.plantTree(dir);
        } else if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
            rc.buildRobot(RobotType.SOLDIER, dir);
        } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
            rc.buildRobot(RobotType.LUMBERJACK, dir);
        } else if (rc.canBuildRobot(RobotType.TANK, dir) && Math.random() < .01 && rc.isBuildReady()) {
            rc.buildRobot(RobotType.TANK, dir);
        }

        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        TreeInfo[] ourTrees = rc.senseNearbyTrees(-1, rc.getTeam());

        if (enemyRobots.length > 0) {
            // If there is an enemy robot, move away from it
            if (Math.random() < .5) {
                tryMove(rc, rc.getLocation().directionTo(enemyRobots[0].getLocation()).rotateLeftDegrees(90 + 30));
            } else {
                tryMove(rc, rc.getLocation().directionTo(enemyRobots[0].getLocation()).rotateRightDegrees(90 + 30));
            }
        } else if (ourTrees.length > 0) {
            boolean watering = false;
            for (TreeInfo ourTree : ourTrees) {
                if (ourTree.getHealth() < .6 * ourTree.getMaxHealth() && rc.canWater(ourTree.getLocation())) {
                    tryMove(rc, rc.getLocation().directionTo(ourTree.getLocation()));
                    rc.water(ourTree.getLocation());
                    watering = true;
                    break;
                }
            }
            if (!watering) {
                // Move randomly
                tryMove(rc, randomDirection());
            }
        }
    }
}
