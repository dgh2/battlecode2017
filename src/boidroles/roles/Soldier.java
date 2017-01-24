package boidroles.roles;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
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
        robotController.setIndicatorLine(robotController.getLocation(),
                robotController.getLocation().translate(movement.dx, movement.dy), 255, 255, 255);
        tryMove(movement.getDirection(), movement.getDistance());

        //Handle actions

        //todo: add multiple shots if "adjacent" to enemy

        attackClosestEnemy();
//        if (!attackClosestEnemy()) {
//            attackArchons();
//        }
    }

    @Override
    protected Vector calculateInfluence() throws GameActionException {
        Vector movement = new Vector();
        for (RobotInfo robot : sensedRobots) {
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
                        robotController.getType().strideRadius)
                        .scale(getInverseScaling(robot.getLocation())));
            }
            if (RobotType.LUMBERJACK.equals(robot.getType())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 2f).scale(getInverseScaling(robot.getLocation())));
            }
            outputInfluenceDebugging("Soldier robot influence", robot, movement);
        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, .2f));
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees));
        movement.add(getInfluenceFromTrees(sensedTrees));
        movement.add(dodgeBullets(sensedBullets));
        //todo: repel from the map's edges too
        outputInfluenceDebugging("Soldier total influence", movement);
        return movement;
    }
}