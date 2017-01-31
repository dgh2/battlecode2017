package finalStretch.roles;

import battlecode.common.*;
import finalStretch.util.RobotBase;
import finalStretch.util.Vector;
import finalStretch.util.InformationStack;

public class Scout extends RobotBase {
//    private MapLocation previousLocation = null;

    InformationStack stack;
    MapLocation readEnemy;

    public Scout(RobotController robotController) {
        super(robotController);
        stack = new InformationStack(this.robotController);
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
        RobotInfo[] enemyRobots = this.robotController.senseNearbyRobots(-1,this.robotController.getTeam().opponent());
        if(enemyRobots.length>0){
            System.out.println("Found robot at "+ enemyRobots[0].getLocation()+ ".Broadcasting it");
            stack.writeToStack(new MapLocation[]{enemyRobots[0].getLocation()}, 1f);
        }

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
        readEnemy = stack.readFromStack();
        movement.add(new Vector(robotController.getLocation().directionTo(readEnemy),
                    robotController.getLocation().distanceTo(readEnemy)))
                    .normalize(robotController.getType().strideRadius * 4f)
                    .scale(robotController.getType().strideRadius * 4f);
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
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees, 3f));
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges(2f));
        movement.add(repelFromPreviousPoint(3f));
        outputInfluenceDebugging("Total influence", movement);
        return movement;
    }
}