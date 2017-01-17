package boidroles.roles;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import boidroles.util.RobotBase;

public class Scout extends RobotBase {
    public Scout(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        attackClosestEnemy();
//        if (!attackClosestEnemy()) {
//            attackArchons();
//        }
    }
}