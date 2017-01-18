package boidroles.roles;

import battlecode.common.BulletInfo;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.TreeInfo;
import boidroles.util.RobotBase;
import boidroles.util.Vector;

import static rolesplayer.util.Util.randomDirection;

public class Archon extends RobotBase {
    public Archon(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        //Handle movement
        Vector movement = calculateInfluence();
        tryMove(movement.getDirection(), movement.getDistance());

        //Handle actions

        // Generate a random direction
        Direction dir = randomDirection();

        // Randomly attempt to build a Gardener in this direction
        if (robotController.canHireGardener(dir) && Math.random() < .25) {
            robotController.hireGardener(dir);
        }
    }

    @Override
    protected Vector calculateInfluence() throws GameActionException {
        RobotInfo[] nearbyRobots = robotController.senseNearbyRobots();
        TreeInfo[] nearbyTree = robotController.senseNearbyTrees();
        BulletInfo[] nearbyBullets = robotController.senseNearbyBullets();
        Vector movement = new Vector();
        for (RobotInfo robot : nearbyRobots) {
            movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()), robotController.getType().strideRadius));
            movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(), robotController.getType().strideRadius));
        }
        for (TreeInfo tree : nearbyTree) {
            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()), robotController.getType().strideRadius));
            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(), robotController.getType().strideRadius));
        }
        for (BulletInfo bullet : nearbyBullets) {
            movement.add(new Vector(robotController.getLocation().directionTo(bullet.getLocation()), robotController.getType().strideRadius));
            movement.add(new Vector(robotController.getLocation().directionTo(bullet.getLocation()).opposite(), robotController.getType().strideRadius));
        }
        //todo: repel from the map's edges too
        return movement;
    }
}