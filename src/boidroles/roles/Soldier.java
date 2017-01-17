package boidroles.roles;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import boidroles.util.RobotBase;

public class Soldier extends RobotBase {
    public Soldier(RobotController robotController) {
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