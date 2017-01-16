package boidroles.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import boidroles.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;

public class Archon extends RobotBase {
    public Archon(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        // Generate a random direction
        Direction dir = randomDirection();

        // Randomly attempt to build a Gardener in this direction
        if (robotController.canHireGardener(dir) && Math.random() < .25) {
            robotController.hireGardener(dir);
        }
    }
}
