package boidroles.roles;

import battlecode.common.BulletInfo;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.TreeInfo;
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
        tryMove(movement.getDirection(), movement.getDistance());

        //Handle actions

        attackClosestEnemy();
//        if (!attackClosestEnemy()) {
//            attackArchons();
//        }
    }

    @Override
    protected Vector calculateInfluence() throws GameActionException {
        RobotInfo[] nearbyRobots = robotController.senseNearbyRobots();
        TreeInfo[] nearbyTree = robotController.senseNearbyTrees();
        BulletInfo[] nearbyBullets = robotController.senseNearbyBullets();
        Vector movement = new Vector();
        for (RobotInfo robot : nearbyRobots) {
            if (!robotController.getTeam().equals(robot.getTeam())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius).scale(1f));
            } else {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
                        robotController.getType().strideRadius)
                        .scale(getScaling(robot.getLocation())));
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius)
                        .scale(getInverseScaling(robot.getLocation())));
            }
        }
        for (TreeInfo tree : nearbyTree) {
//            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()),
//                    robotController.getType().strideRadius*.1f).scale(1f));
            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(),
                    robotController.getType().strideRadius * .1f)
                    .scale(getInverseScaling(tree.getLocation())));
        }
        for (BulletInfo bullet : nearbyBullets) {
            float theta = bullet.getDir().radiansBetween(bullet.getLocation().directionTo(robotController.getLocation()));
            // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
            if (Math.abs(theta) > Math.PI / 2) {
                break;
            }
            movement.add(new Vector(robotController.getLocation().directionTo(bullet.getLocation()).opposite(),
                    robotController.getType().strideRadius + 2f * bullet.getSpeed())
                    .scale(getInverseScaling(bullet.getLocation())));
            movement.add(new Vector(robotController.getLocation().directionTo(bullet.getLocation()).opposite(),
                    robotController.getType().strideRadius + 2f * bullet.getSpeed())
                    .scale(getInverseScaling(projectBulletLocation(bullet))));
            movement.add(new Vector(robotController.getLocation().directionTo(bullet.getLocation()).opposite(),
                    robotController.getType().strideRadius + 2f * bullet.getSpeed())
                    .scale(getInverseScaling(projectBulletLocation(bullet, 2))));
            movement.add(new Vector(robotController.getLocation().directionTo(bullet.getLocation()).opposite(),
                    robotController.getType().strideRadius + 2f * bullet.getSpeed())
                    .scale(getInverseScaling(projectBulletLocation(bullet, 3))));
        }
        for (MapLocation archonLocation : robotController.getInitialArchonLocations(robotController.getTeam().opponent())) {
            movement.add(new Vector(robotController.getLocation().directionTo(archonLocation).opposite(),
                    robotController.getType().strideRadius).scale(.05f));
        }
        //todo: repel from the map's edges too
        return movement;
    }
}