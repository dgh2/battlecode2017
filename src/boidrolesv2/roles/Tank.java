package boidrolesv2.roles;

import battlecode.common.*;
import boidrolesv2.util.RobotBase;
import boidrolesv2.util.Vector;
import finalStretch.util.InformationStack;

public class Tank extends RobotBase {
    public Tank(RobotController robotController) {
        super(robotController);
        stack = new InformationStack(this.robotController); //need this
    }

    InformationStack stack; //need this
    MapLocation readEnemy;

    @Override
    public void run() throws GameActionException {
        //Handle movement
        Vector movement = calculateInfluence();
        robotController.setIndicatorLine(robotController.getLocation(),
                robotController.getLocation().translate(movement.dx, movement.dy), 255, 255, 255);
        tryMove(movement.getDirection(), movement.getDistance());

        //Handle actions
        RobotInfo[] enemyRobots = this.robotController.senseNearbyRobots(-1,this.robotController.getTeam().opponent()); //need this
        if(enemyRobots.length>0){
            System.out.println("Found robot at "+ enemyRobots[0].getLocation()+ ".Broadcasting it");
            stack.writeToStack(new MapLocation[]{enemyRobots[0].getLocation()}, 1f);
        }

        attackClosestEnemy();
//        if (!attackClosestEnemy()) {
//            attackArchons();
//        }
    }

    @Override
    protected Vector calculateInfluence() throws GameActionException {
        Vector movement = new Vector();
        readEnemy = stack.readFromStack(); //need this
        if(readEnemy != null) {
//            System.out.println("Read robot at "+ readEnemy+ ".Rebroadcasting it");
//            stack.writeToStack(new MapLocation[]{readEnemy}, 0.5f); //lower heuristic?
            movement.add(new Vector(robotController.getLocation().directionTo(readEnemy),
                    robotController.getLocation().distanceTo(readEnemy)))
                    .normalize(robotController.getType().strideRadius)
                    .scale(robotController.getType().strideRadius);
        }
        for (RobotInfo robot : sensedRobots) {
            if (!robotController.getTeam().equals(robot.getTeam())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
                        robotController.getType().strideRadius * 2f)
                        .scale(getScaling(robot.getLocation())));
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius)
                        .scale(getInverseScaling(robot.getLocation())));
            } else {
//                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
//                                robotController.getType().strideRadius)
//                                .scale(getScaling(robot.getLocation())));
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 2f)
                        .scale(getScaling(robot.getLocation())));
            }
            if (RobotType.LUMBERJACK.equals(robot.getType())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 2f).scale(getInverseScaling(robot.getLocation())));
            }
            outputInfluenceDebugging("Tank robot influence", robot, movement);
        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, 1f));
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees));
//        movement.add(getInfluenceFromTrees(sensedTrees));
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges(1f));
        outputInfluenceDebugging("Tank total influence", movement);
        return movement;
    }
}