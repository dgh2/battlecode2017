package rolesplayer.roles;

import battlecode.common.*;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

public class Tank extends RobotBase {
    public Tank(RobotController rc) {
        super(rc);
    }

    @Override
    public RobotType getBaseType() {
        return RobotType.TANK;
    }

    @Override
    public void run() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = rc.readBroadcast(0);
        int yPos = rc.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        Team enemy = rc.getTeam().opponent();

        // See if there are any nearby enemy robots
        RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

        // If there are some...
        if (robots.length > 0) {
            // And we have enough bullets, and haven't attacked yet this turn...
            if (rc.canFireSingleShot()) {
                // ...Then fire a bullet in the direction of the enemy.
                rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
            }
            tryMove(rc, rc.getLocation().directionTo(robots[0].getLocation()));
        } else {
            if (enemyArchonLoc.x != 0 || enemyArchonLoc.y != 0) {
                if (rc.canFireSingleShot()) {
                    if (Math.random() > .5) {
                        rc.fireSingleShot(rc.getLocation().directionTo(enemyArchonLoc).rotateLeftDegrees((float) (.5 * Math.random())));
                    } else {
                        rc.fireSingleShot(rc.getLocation().directionTo(enemyArchonLoc).rotateRightDegrees((float) (.5 *  Math.random())));
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