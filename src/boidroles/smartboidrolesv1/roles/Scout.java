package boidroles.smartboidrolesv1.roles;

import battlecode.common.*;
import boidroles.smartboidrolesv1.util.RobotBase;
import boidroles.smartboidrolesv1.util.Vector;
import finalStretch.util.InformationStack;


public class Scout extends RobotBase {
//    private MapLocation previousLocation = null;

    InformationStack stack; //need this
    MapLocation readEnemy;

    public Scout(RobotController robotController) {
        super(robotController);
        stack = new InformationStack(this.robotController); //need this
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

        //Handle
        RobotInfo[] enemyRobots = this.robotController.senseNearbyRobots(-1,this.robotController.getTeam().opponent()); //need this
        if(enemyRobots.length>0){
            System.out.println("Found robot at "+ enemyRobots[0].getLocation()+ ".Broadcasting it");
            stack.writeToStack(new MapLocation[]{enemyRobots[0].getLocation()}, 1f);
        }

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
//        if (!robotController.getLocation().equals(previousLocation)) {
//            movement.add(new Vector(robotController.getLocation().x - previousLocation.x,
//                    robotController.getLocation().y - previousLocation.y)
//                    .scale(1f));
//        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, 0.5f));
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees));
//        movement.add(getInfluenceFromTrees(sensedTrees));
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges(1f));
        outputInfluenceDebugging("Total influence", movement);
        return movement;
    }
}