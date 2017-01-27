package josiah_boid_garden.util;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import josiah_boid_garden.roles.Archon;
import josiah_boid_garden.roles.Gardener;
import josiah_boid_garden.roles.Lumberjack;
import josiah_boid_garden.roles.Scout;
import josiah_boid_garden.roles.Soldier;
import josiah_boid_garden.roles.Tank;


public abstract class RobotBase {
    protected RobotController robotController;
    protected boolean rightHanded = Math.random() > .33;

    protected RobotBase(RobotController robotController) {
        if (robotController == null || robotController.getType() == null) {
            throw new IllegalArgumentException("Unable to build robot from invalid RobotController!");
        }
        this.robotController = robotController;
    }

    public static RobotBase createForController(RobotController robotController) {
        if (robotController == null || robotController.getType() == null) {
            throw new IllegalArgumentException("Unable to build robot from invalid RobotController!");
        }
        switch (robotController.getType()) {
            case ARCHON:
                return new Archon(robotController);
            case GARDENER:
                return new Gardener(robotController);
            case SOLDIER:
                return new Soldier(robotController);
            case TANK:
                return new Tank(robotController);
            case SCOUT:
                return new Scout(robotController);
            case LUMBERJACK:
                return new Lumberjack(robotController);
            default:
                throw new IllegalArgumentException("Unable to build robot of an unrecognized type: " + robotController.getType().name());
        }
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException thrown when an illegal action is attempted
     */
    protected static boolean tryMove(RobotController robotController, Direction dir) throws GameActionException {
//        if (tryMove(robotController, dir, 20, 3)) {
//            return true;
//        }
        return tryMove(robotController, dir, 20, 5);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir           The intended direction of movement
     * @param degreeOffset  Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException thrown when an illegal action is attempted
     */
    protected static boolean tryMove(RobotController robotController, Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (robotController.canMove(dir)) {
            robotController.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        int currentCheck = 1;

        while (currentCheck <= checksPerSide) {
            // Try the offset of the left side
            if (robotController.canMove(dir.rotateLeftDegrees(degreeOffset * currentCheck))) {
                robotController.move(dir.rotateLeftDegrees(degreeOffset * currentCheck));
                return true;
            }
            // Try the offset on the right side
            if (robotController.canMove(dir.rotateRightDegrees(degreeOffset * currentCheck))) {
                robotController.move(dir.rotateRightDegrees(degreeOffset * currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    public final RobotType getBaseType() {
        return robotController.getType();
    }

    public abstract void run() throws GameActionException;

}
