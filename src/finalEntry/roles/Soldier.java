package finalEntry.roles;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import finalEntry.util.RobotBase;
import finalEntry.util.Vector;

public class Soldier extends RobotBase {
    public Soldier(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        //Handle movement
//        boid.addForce();
//        boid.addLinearAttraction();
//        boid.addLinearRepulsion();
//        boid.addPreferedDistance();
//        boid.addSquaredAttraction();
//        boid.addSquaredRepulsion();

        //Handle actions

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
        movement.add(repelFromMapEdges());
        outputInfluenceDebugging("Soldier total influence", movement);
        return movement;
    }
}