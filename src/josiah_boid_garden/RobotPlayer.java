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


        do {

            try {
                self.run();
            } catch (Exception e) {
                logRobotException("run", e);
            }

            Clock.yield(); // continue from here next turn
        } while (true); // loop or die

    }

    private static void logRobotException(String method, Exception e) {
        System.out.print("An exception was caught from " + self.getBaseType().name() + "." + method + "(): " + e.getMessage());
    }
}
