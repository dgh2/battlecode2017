package rolesplayer.roles;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

public class Tank extends RobotBase {
    public Tank(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = robotController.readBroadcast(0);
        int yPos = robotController.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        Team enemy = robotController.getTeam().opponent();

        // See if there are any nearby enemy robots
        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, enemy);
        RobotInfo[] closeEnemyRobots = robotController.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius + 2 * GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
        RobotInfo[] closestEnemyRobots = robotController.senseNearbyRobots(1, enemy);

        // If there are some...
        if (enemyRobots.length > 0) {
            MapLocation enemyLocation = (closestEnemyRobots.length > 0 ? closestEnemyRobots[0].location :
                    (closeEnemyRobots.length > 0 ? closeEnemyRobots[0].location : enemyRobots[0].location)); // cuz I felt like it
            // And we have enough bullets, and haven't attacked yet this turn...
            if (robotController.canFireSingleShot()) {
                // ...Then fire a bullet in the direction of the enemy.
                robotController.fireSingleShot(robotController.getLocation().directionTo(enemyLocation));
            }
            tryMove(robotController, robotController.getLocation().directionTo(enemyLocation));
        } else {
            if (enemyArchonLoc.x != 0 || enemyArchonLoc.y != 0) {
                if (robotController.canFireSingleShot()) {
                    if (rightHanded) {
                        robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc).rotateRightDegrees((float) (.5 * Math.random())));
                    } else {
                        robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc).rotateLeftDegrees((float) (.5 * Math.random())));
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