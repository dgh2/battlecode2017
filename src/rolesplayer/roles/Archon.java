package rolesplayer.roles;

import battlecode.common.*;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

public class Archon extends RobotBase {
    public Archon(RobotController rc) {
        super(rc);
    }

    @Override
    public RobotType getBaseType() {
        return RobotType.ARCHON;
    }

    @Override
    public void run() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = rc.readBroadcast(0);
        int yPos = rc.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        // Generate a random direction
        Direction dir = randomDirection();

        // Randomly attempt to build a Gardener in this direction
        if (rc.canHireGardener(dir) && Math.random() < .02) {
            rc.hireGardener(dir);
        }

        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemyRobots.length > 0) {
            // If there is an enemy robot, move away from it
            if (Math.random() < .5) {
                tryMove(rc, rc.getLocation().directionTo(enemyRobots[0].getLocation()).rotateLeftDegrees(90 + 30));
            } else {
                tryMove(rc, rc.getLocation().directionTo(enemyRobots[0].getLocation()).rotateRightDegrees(90 + 30));
            }
        } else {
            if (enemyArchonLoc.x != 0 || enemyArchonLoc.y != 0) {
                tryMove(rc, rc.getLocation().directionTo(enemyArchonLoc).opposite());
            } else {
                // Move randomly
                tryMove(rc, randomDirection());
            }
        }

        // Broadcast Archon's location for other robots on the team to know
//        MapLocation myLocation = rc.getLocation();
//        rc.broadcast(0,(int)myLocation.x);
//        rc.broadcast(1,(int)myLocation.y);
    }
}
