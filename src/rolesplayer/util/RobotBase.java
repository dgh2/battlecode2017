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
    protected RobotController rc;

    protected RobotBase(RobotController rc) {
        if (!getBaseType().equals(rc.getType())) {
            throw new IllegalArgumentException("A robot of type " + rc.getType() + " cannot be created with a " + getBaseType() + " base!");
        }
        this.rc = rc;
    }

    public static class RobotFactory {
        private RobotController rc;
        public RobotFactory(RobotController rc) {
            this.rc = rc;
        }
        public RobotBase build() {
            if(rc == null || rc.getType() == null) {
                throw new IllegalArgumentException("Unable to build robot from invalid RobotController!");
            }
            switch (rc.getType()) {
                case ARCHON:
                    return new Archon(rc);
                case GARDENER:
                    return new Gardener(rc);
                case SOLDIER:
                    return new Soldier(rc);
                case TANK:
                    return new Tank(rc);
                case SCOUT:
                    return new Scout(rc);
                case LUMBERJACK:
                    return new Lumberjack(rc);
                default:
                    throw new IllegalArgumentException("Unable to build robot of an unrecognized type: " + rc.getType().name());
            }
        }
    }

    public abstract RobotType getBaseType();

    public void runOnce() throws GameActionException {
        System.out.println("I am!");
    }

    public void beforeRun() throws GameActionException {
        System.out.print("I'm a bot, ");
        Util.detectArchons(rc);
    }

    public abstract void run() throws GameActionException;

    public void afterRun() throws GameActionException {
        Util.detectArchons(rc);
        System.out.println("We're done here!");
    }

    public void dying() throws GameActionException {
        Util.detectArchons(rc);
        System.out.println("Oh, what a world!");
    }

    public boolean getWillToLive() {
        return true;
    }
}
