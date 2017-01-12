package rolesplayer.roles;

import battlecode.common.Direction;
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

public class Scout extends RobotBase {
    private Direction lastDirection;

    public Scout(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = robotController.readBroadcast(0);
        int yPos = robotController.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        Team enemy = robotController.getTeam().opponent();

        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, enemy);
        RobotInfo[] closeEnemyRobots = robotController.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius + 2 * GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
        if (enemyRobots.length > 0) {
            if (closeEnemyRobots.length > 0) {
                if (rightHanded) {
                    tryMove(robotController, robotController.getLocation().directionTo(closeEnemyRobots[0].getLocation()).opposite().rotateRightDegrees(45).opposite());
                }
                if (!robotController.hasMoved()) {
                    tryMove(robotController, robotController.getLocation().directionTo(closeEnemyRobots[0].getLocation()).opposite().rotateLeftDegrees(45).opposite());
                }
            } else {
                if (rightHanded) {
                    tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateLeftDegrees(45));
                }
                if (!robotController.hasMoved()) {
                    tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateRightDegrees(45));
                }
            }
            if (!robotController.hasMoved()) {
                // Move randomly
                tryMove(robotController, randomDirection());
            }
            if (robotController.canFireSingleShot()) {
                robotController.fireSingleShot(robotController.getLocation().directionTo(enemyRobots[0].location));
            }
        } else if (enemyArchonLoc.x != 0 && enemyArchonLoc.y != 0) {
            if (rightHanded) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyArchonLoc).rotateLeftDegrees(90));
            }
            if (!robotController.hasMoved()) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyArchonLoc).rotateRightDegrees(90));
            }
            if (!robotController.hasMoved()) {
                // Move randomly
                tryMove(robotController, randomDirection());
            }
            if (robotController.canFireSingleShot()) {
                if (rightHanded) {
                    robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc).rotateRightDegrees((float) (1 * Math.random())));
                } else {
                    robotController.fireSingleShot(robotController.getLocation().directionTo(enemyArchonLoc).rotateLeftDegrees((float) (1 * Math.random())));
                }
            }
        } else {
            if (!robotController.hasMoved()) {
                // Move randomly
                tryMove(robotController, randomDirection());
            }
        }
    }
}