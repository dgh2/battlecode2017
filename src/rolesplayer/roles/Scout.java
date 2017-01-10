package rolesplayer.roles;

import battlecode.common.*;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

public class Scout extends RobotBase {
    public Scout(RobotController rc) {
        super(rc);
    }

    @Override
    public RobotType getBaseType() {
        return RobotType.SCOUT;
    }

    @Override
    public void run() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = rc.readBroadcast(0);
        int yPos = rc.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        Team enemy = rc.getTeam().opponent();

        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, enemy);
        if (enemyRobots.length > 0) {
            if (Math.random() > .5) {
                tryMove(rc, rc.getLocation().directionTo(enemyRobots[0].getLocation()).rotateLeftDegrees(90+45));
            } else {
                tryMove(rc, rc.getLocation().directionTo(enemyRobots[0].getLocation()).rotateRightDegrees(90+45));
            }
        } else {
            if (enemyArchonLoc.x != 0 || enemyArchonLoc.y != 0) {
                if (rc.canFireSingleShot()) {
                    if (Math.random() > .5) {
                        rc.fireSingleShot(rc.getLocation().directionTo(enemyArchonLoc).rotateLeftDegrees((float) (1 * Math.random())));
                    } else {
                        rc.fireSingleShot(rc.getLocation().directionTo(enemyArchonLoc).rotateRightDegrees((float) (1 *  Math.random())));
                    }
                }
                tryMove(rc, rc.getLocation().directionTo(enemyArchonLoc));
            } else {
                // Move randomly
                tryMove(rc, randomDirection());
            }
        }
    }
}