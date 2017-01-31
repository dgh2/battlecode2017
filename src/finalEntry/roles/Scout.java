package finalEntry.roles;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import finalEntry.util.RobotBase;
import finalEntry.util.Vector;

public class Scout extends RobotBase {
//    private MapLocation previousLocation = null;

    public Scout(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        //Handle movement
//        if (previousLocation == null) {
//            previousLocation = robotController.getLocation();
//        }
//        if (previousLocation == robotController.getLocation()) {
//            previousLocation = previousLocation.add(randomDirection(), robotController.getType().strideRadius);
//        }
        Vector movement = calculateInfluence();
        robotController.setIndicatorLine(robotController.getLocation(),
                robotController.getLocation().translate(movement.dx, movement.dy), 255, 255, 255);
        tryMove(movement.getDirection(), movement.getDistance());

        //Handle actions

        // attack closest enemy except archons
        for (RobotInfo robot : sensedRobots) {
            if (robotController.getTeam().opponent().equals(robot.getTeam())
                    && robot.getType() != RobotType.ARCHON
                    && robotController.canFireSingleShot()
                    && hasLineOfSight(robot)) {
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
            Vector attraction = new Vector(robotController.getLocation().directionTo(robot.getLocation()),
                    robotController.getLocation().distanceTo(robot.getLocation()))
                    .normalize(robotController.getType().sensorRadius)
                    .scale(robotController.getType().strideRadius);
            if (RobotType.LUMBERJACK.equals(robot.getType())) {
                movement.add(attraction.opposite());
            } else if (!robotController.getTeam().equals(robot.getTeam())) {
                if (RobotType.GARDENER.equals(robot.getType())) {
                    movement.add(new Vector(attraction.getDirection(),
                            Math.min(robotController.getType().strideRadius,
                                    robotController.getLocation().distanceTo(robot.getLocation()))).scale(50f));
                } else {
                    movement.add(attraction.opposite().scale(.75f));
                }
            } else {
                movement.add(attraction.opposite().scale(.75f));
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
//        if (!robotController.getLocation().equals(previousLocation)) {
//            movement.add(new Vector(robotController.getLocation().x - previousLocation.x,
//                    robotController.getLocation().y - previousLocation.y)
//                    .scale(1f));
//        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, 0.5f));
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees));
//        movement.add(getInfluenceFromTrees(sensedTrees));
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges(2f));
        movement.add(repelFromPreviousPoint(3f));
        outputInfluenceDebugging("Total influence", movement);
        return movement;
    }
}