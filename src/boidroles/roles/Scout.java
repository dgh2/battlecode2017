package boidroles.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import boidroles.util.RobotBase;
import boidroles.util.Vector;

public class Scout extends RobotBase {
    public Scout(RobotController robotController) {
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

        // attack closest enemy except archons
        for (RobotInfo robot : sensedRobots) {
            if (robotController.getTeam().opponent().equals(robot.getTeam())
                    && robot.getType() != RobotType.ARCHON //wanted to add this line
                    && robotController.canFireSingleShot()
                    && checkLineOfSight(robot)) { // had to change this one as a result, and make an accessor function to run the function that runs the fuction
                robotController.fireSingleShot(robotController.getLocation().directionTo(robot.location));
            }
        }
//        attackClosestEnemy(); //this has basically been written above
//        if (!attackClosestEnemy()) {
//            attackArchons();
//        }
    }

    @Override
    protected Vector calculateInfluence() throws GameActionException {
        Vector movement = new Vector();
        for (RobotInfo robot : sensedRobots) {
            Direction robotDirection = robotController.getLocation().directionTo(robot.getLocation());
            if (!robotController.getTeam().equals(robot.getTeam())) {
                if (RobotType.GARDENER.equals(robot.getType())) {
                    movement.add(new Vector(robotDirection,
                            robotController.getType().strideRadius * 3f)
                            .scale(getScaling(robot.getLocation())));
                } else {
                    movement.add(new Vector(robotDirection,
                            robotController.getType().strideRadius)
                            .scale(getScaling(robot.getLocation())));
                }
//                movement.add(new Vector(robotDirection.opposite(),
//                        robotController.getType().strideRadius)
//                        .scale(getInverseScaling(robot.getLocation())));
            } else {
//                movement.add(new Vector(robotDirection,
//                        robotController.getType().strideRadius)
//                        .scale(getScaling(robot.getLocation())));
                movement.add(new Vector(robotDirection.opposite(),
                        robotController.getType().strideRadius * 2f)
                        .scale(getInverseScaling(robot.getLocation())));
            }
            if (RobotType.LUMBERJACK.equals(robot.getType())) {
                movement.add(new Vector(robotDirection.opposite(),
                        robotController.getType().strideRadius * 2.5f).scale(getInverseScaling(robot.getLocation())));
            }
            outputInfluenceDebugging("Robot influence", robot, movement);
        }
//        for (TreeInfo tree : sensedTrees) {
//            Direction treeDirection = robotController.getLocation().directionTo(tree.getLocation());
////            movement.add(new Vector(treeDirection,
////                    robotController.getType().strideRadius*.1f).scale(1f));
//            movement.add(new Vector(treeDirection.opposite(),
//                    robotController.getType().strideRadius * .1f)
//                    .scale(getInverseScaling(tree.getLocation())));
//            outputInfluenceDebugging("Scout robot + tree influence", tree, movement);
//        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, 0.5f));
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees));
//        movement.add(getInfluenceFromTrees(sensedTrees));
        movement.add(dodgeBullets(sensedBullets));
        //todo: repel from the map's edges too
        outputInfluenceDebugging("Total influence", movement);
        return movement;
    }
}