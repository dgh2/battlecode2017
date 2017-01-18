package boidroles;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import boidroles.util.RobotBase;

@SuppressWarnings("unused")
public strictfp class RobotPlayer {
    private static RobotBase self;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController robotController) throws GameActionException {
        RobotPlayer.self = RobotBase.createForController(robotController);
        try {
            self.runOnce();
        } catch (Exception e) {
            self.logRobotException("runOnce", e);
        }

        do {
            try {
                self.beforeRun();
            } catch (Exception e) {
                self.logRobotException("beforeRun", e);
            }
            try {
                self.run();
            } catch (Exception e) {
                self.logRobotException("run", e);
            }
            try {
                self.afterRun();
            } catch (Exception e) {
                self.logRobotException("afterRun", e);
            }
            Clock.yield(); // continue from here next turn
        } while (self.getWillToLive()); // loop or die

        try {
            self.dying();
        } catch (Exception e) {
            self.logRobotException("dying", e);
        }
    }
}
