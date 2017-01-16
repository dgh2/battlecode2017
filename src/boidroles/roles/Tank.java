package boidroles.roles;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import boidroles.util.RobotBase;

public class Tank extends RobotBase {
    public Tank(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        if (!attackClosestEnemy()) {
            attackArchons();
        }
    }
}