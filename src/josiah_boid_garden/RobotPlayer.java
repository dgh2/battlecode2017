package josiah_boid_garden;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import josiah_boid_garden.util.RobotBase;

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
            logRobotException("runOnce", e);
        }

        do {
            try {
                self.beforeRun();
            } catch (Exception e) {
                logRobotException("beforeRun", e);
            }
            try {
                self.run();
            } catch (Exception e) {
                logRobotException("run", e);
            }
            try {
                self.afterRun();
            } catch (Exception e) {
                logRobotException("afterRun", e);
            }
            Clock.yield(); // continue from here next turn
        } while (self.getWillToLive()); // loop or die

        try {
            self.dying();
        } catch (Exception e) {
            logRobotException("dying", e);
        }
    }

    private static void logRobotException(String method, Exception e) {
        System.out.print("An exception was caught from " + self.getBaseType().name() + "." + method + "(): " + e.getMessage());
    }
}
