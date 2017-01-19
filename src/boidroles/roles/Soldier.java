package boidroles.roles;

import battlecode.common.BulletInfo;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import boidroles.util.RobotBase;
import boidroles.util.Vector;

public class Soldier extends RobotBase {
    public Soldier(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        //Handle movement
        Vector movement = calculateInfluence();
        tryMove(movement.getDirection(), movement.getDistance());

        //Handle actions

        attackClosestEnemy();
//        if (!attackClosestEnemy()) {
//            attackArchons();
//        }
    }

    @Override
    protected Vector calculateInfluence() throws GameActionException {
        RobotInfo[] nearbyRobots = robotController.senseNearbyRobots();
        TreeInfo[] nearbyTrees = robotController.senseNearbyTrees();
        BulletInfo[] nearbyBullets = robotController.senseNearbyBullets();
        Vector movement = new Vector();
        for (RobotInfo robot : nearbyRobots) {
            if (!robotController.getTeam().equals(robot.getTeam())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
                        robotController.getType().strideRadius)
                        .scale(getScaling(robot.getLocation())));
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius)
                        .scale(getInverseScaling(robot.getLocation())));
            } else {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
                        robotController.getType().strideRadius)
                        .scale(getScaling(robot.getLocation())));
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius)
                        .scale(getInverseScaling(robot.getLocation())));
            }
            if (RobotType.LUMBERJACK.equals(robot.getType())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 2f).scale(getInverseScaling(robot.getLocation())));
            }
        }
        movement.add(dodgeBullets(nearbyBullets));
        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, .2f));
        movement.add(getInfluenceFromTreesWithBullets(nearbyTrees));
        movement.add(getInfluenceFromTrees(nearbyTrees));
        //todo: repel from the map's edges too
        return movement;
    }
}