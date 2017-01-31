package finalStretch;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import finalStretch.util.RobotBase;

@SuppressWarnings("unused")
public strictfp class RobotPlayer {
    private static RobotBase self;
    private static int rememberedRound;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController robotController) throws GameActionException {
        RobotPlayer.self = RobotBase.createForController(robotController);
        try {
            self.runOnce();
            self.debugBytecodeUsed("runOnce");
        } catch (Exception e) {
            self.logRobotException("runOnce", e);
        }

        do {
            rememberedRound = robotController.getRoundNum();
            try {
                self.beforeRun();
                self.debugBytecodeUsed("beforeRun");
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
                self.debugBytecodeUsed("afterRun");
            } catch (Exception e) {
                self.logRobotException("afterRun", e);
            }
            if (robotController.getRoundNum() == rememberedRound) {
                Clock.yield(); // continue from here next turn...
            }
        } while (self.getWillToLive()); // loop or die

        try {
            self.dying();
            self.debugBytecodeUsed("dying");
        } catch (Exception e) {
            self.logRobotException("dying", e);
        }
    }
}
