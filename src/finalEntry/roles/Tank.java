package finalEntry.roles;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.TreeInfo;
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

        if (!attackClosestEnemy()) {
            for (TreeInfo tree : sensedTrees) {
                if (robotController.getTeam().equals(tree.getTeam())) {
                    continue;
                }
                if (robotController.canFirePentadShot() || robotController.canFireTriadShot()) {
                    float distance = robotController.getLocation().distanceTo(tree.getLocation());
                    float angle = (float) Math.toDegrees(Math.atan(tree.getRadius() / distance));
                    if (angle > 2 * GameConstants.PENTAD_SPREAD_DEGREES && robotController.canFirePentadShot()) {
                        robotController.firePentadShot(robotController.getLocation().directionTo(tree.getLocation()));
                    } else if (angle > GameConstants.TRIAD_SPREAD_DEGREES && robotController.canFireTriadShot()) {
                        robotController.fireTriadShot(robotController.getLocation().directionTo(tree.getLocation()));
                    } else {
                        robotController.fireSingleShot(robotController.getLocation().directionTo(tree.getLocation()));
                    }
                } else if (robotController.canFireSingleShot()) {
                    robotController.fireSingleShot(robotController.getLocation().directionTo(tree.getLocation()));
                }
            }
        }
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
//        movement.add(getInfluenceFromTreesWithBullets(sensedTrees, 1f));
        movement.add(getInfluenceAwayFromTrees(sensedTrees, .75f));
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges(2f));
        movement.add(repelFromPreviousPoint(2f));
        outputInfluenceDebugging("Tank total influence", movement);
        return movement;
    }
}