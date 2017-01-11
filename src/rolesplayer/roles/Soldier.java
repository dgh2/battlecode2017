package rolesplayer.roles;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

public class Soldier extends RobotBase {
    public Soldier(RobotController robotController) {
        super(robotController);
    }

    @Override
    public RobotType getBaseType() {
        return RobotType.SOLDIER;
    }

    @Override
    public void run() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = robotController.readBroadcast(0);
        int yPos = robotController.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        Team enemy = robotController.getTeam().opponent();

        // See if there are any nearby enemy robots
        RobotInfo[] robots = robotController.senseNearbyRobots(-1, enemy);

        // If there are some...
        if (robots.length > 0) {
            // And we have enough bullets, and haven't attacked yet this turn...
            if (robotController.canFireSingleShot()) {
                // ...Then fire a bullet in the direction of the enemy.
                robotController.fireSingleShot(robotController.getLocation().directionTo(robots[0].location));
            }
            tryMove(robotController, robotController.getLocation().directionTo(robots[0].getLocation()));
            if(Math.random() < .5) {
                tryMove(robotController, robotController.getLocation().directionTo(robots[0].getLocation()).rotateLeftDegrees(30));
            } else {
                tryMove(robotController, robotController.getLocation().directionTo(robots[0].getLocation()).rotateRightDegrees(30));
            }
        } else {
            if (enemyArchonLoc.x != 0 || enemyArchonLoc.y != 0) {
                if (robotController.canFireSingleShot()) {
                    if (Math.random() > .5) {
                        robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc).rotateLeftDegrees((float) (2 * Math.random())));
                    } else {
                        robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc).rotateRightDegrees((float) (2 * Math.random())));
                    }
                } else {
                    tryMove(robotController, robotController.getLocation().directionTo(enemyArchonLoc));
                }
            } else {
                // Move randomly
                tryMove(robotController, randomDirection());
            }
        }
    }
}