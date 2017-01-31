package finalEntry.roles;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import finalEntry.util.RobotBase;
import finalEntry.util.Vector;

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

        if (!attackClosestEnemy()) {
            for (TreeInfo tree : sensedTrees) {
                if (robotController.getTeam().equals(tree.getTeam()) || !hasLineOfSight(tree)) {
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
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees, 1f));
        movement.add(getInfluenceAwayFromTrees(sensedTrees, .5f));
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges(2f));
        movement.add(repelFromPreviousPoint(2f));
        outputInfluenceDebugging("Soldier total influence", movement);
        return movement;
    }
}