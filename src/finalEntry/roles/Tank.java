package finalEntry.roles;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import finalEntry.util.RobotBase;
import finalEntry.util.Vector;

public class Tank extends RobotBase {
    public Tank(RobotController robotController) {
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

        attackClosestEnemy();
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
            if (!robotController.getTeam().equals(robot.getTeam())) {
                movement.add(attraction.scale(1.25f));
            } else {
                movement.add(attraction.opposite().scale(.75f));
            }
            outputInfluenceDebugging("Tank robot influence", robot, movement);
        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, 1f));
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees));
//        movement.add(getInfluenceFromTrees(sensedTrees));
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges(2f));
        movement.add(repelFromPreviousPoint(2f));
        outputInfluenceDebugging("Tank total influence", movement);
        return movement;
    }
}