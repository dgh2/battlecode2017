package rolesplayer.util;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import rolesplayer.roles.Archon;
import rolesplayer.roles.Gardener;
import rolesplayer.roles.Lumberjack;
import rolesplayer.roles.Scout;
import rolesplayer.roles.Soldier;
import rolesplayer.roles.Tank;

public abstract class RobotBase {
    protected RobotController robotController;

    protected RobotBase(RobotController robotController) {
        if (!getBaseType().equals(robotController.getType())) {
            throw new IllegalArgumentException("A robot of type " + robotController.getType() + " cannot be created with a " + getBaseType() + " base!");
        }
        this.robotController = robotController;
    }

    public abstract RobotType getBaseType();

    public void runOnce() throws GameActionException {
        System.out.println("I am!");
    }

    public void beforeRun() throws GameActionException {
        System.out.print("I'm a bot, ");
        Util.detectArchons(robotController);
    }

    public abstract void run() throws GameActionException;

    public void afterRun() throws GameActionException {
        Util.detectArchons(robotController);
        System.out.println("We're done here!");
    }

    public void dying() throws GameActionException {
        Util.detectArchons(robotController);
        System.out.println("Oh, what a world!");
    }

    public boolean getWillToLive() {
        return true;
    }

    public static class RobotFactory {
        private RobotController robotController;

        public RobotFactory(RobotController robotController) {
            this.robotController = robotController;
        }

        public RobotBase build() {
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
    }
}
