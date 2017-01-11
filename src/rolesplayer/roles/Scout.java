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

public class Scout extends RobotBase {
    public Scout(RobotController robotController) {
        super(robotController);
    }

    @Override
    public RobotType getBaseType() {
        return RobotType.SCOUT;
    }

    @Override
    public void run() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = robotController.readBroadcast(0);
        int yPos = robotController.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        Team enemy = robotController.getTeam().opponent();

        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, enemy);
        if (enemyRobots.length > 0) {
            if (Math.random() > .5) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateLeftDegrees(90 + 45));
            } else {
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateRightDegrees(90 + 45));
            }
        } else {
            if (enemyArchonLoc.x != 0 || enemyArchonLoc.y != 0) {
                if (robotController.canFireSingleShot()) {
                    if (Math.random() > .5) {
                        robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc).rotateLeftDegrees((float) (1 * Math.random())));
                    } else {
                        robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc).rotateRightDegrees((float) (1 * Math.random())));
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