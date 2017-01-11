package rolesplayer.roles;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

public class Lumberjack extends RobotBase {
    public Lumberjack(RobotController robotController) {
        super(robotController);
    }

    @Override
    public RobotType getBaseType() {
        return RobotType.LUMBERJACK;
    }

    @Override
    public void runOnce() {
        System.out.print("I wanted to be... A " + getBaseType().name() + "!");
    }

    @Override
    public void beforeRun() {
        System.out.print("I am a " + getBaseType().name() + " and I'm okay");
    }

    @Override
    public void afterRun() {
        System.out.print("I sleep all night and I work all day");
    }

    @Override
    public void dying() {
        System.out.print("I never wanted to do this in the first place!");
    }

    @Override
    public void run() throws GameActionException {
        // Listen for enemy Archon's location
        int xPos = robotController.readBroadcast(0);
        int yPos = robotController.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        // See if there are any enemy and friendly robots or trees within striking range (distance 1 from lumberjack's radius)
        float strikingRange = RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;
        RobotInfo[] enemyRobots = robotController.senseNearbyRobots(strikingRange, robotController.getTeam().opponent());
        RobotInfo[] friendlyRobots = robotController.senseNearbyRobots(strikingRange, robotController.getTeam());
        TreeInfo[] enemyTrees = robotController.senseNearbyTrees(strikingRange, robotController.getTeam().opponent());
        TreeInfo[] ourTrees = robotController.senseNearbyTrees(strikingRange, robotController.getTeam());
        TreeInfo[] neutralTrees = robotController.senseNearbyTrees(strikingRange, Team.NEUTRAL);

        if ((enemyRobots.length > 0 || enemyTrees.length > 0 || neutralTrees.length > 0) && !robotController.hasAttacked()) {
            if(friendlyRobots.length != 0) {
                // Too close to friendly units
                tryMove(robotController, robotController.getLocation().directionTo(friendlyRobots[0].getLocation()).opposite());
            } else if(ourTrees.length != 0) {
                // Too close to our trees
                tryMove(robotController, robotController.getLocation().directionTo(ourTrees[0].getLocation()).opposite());
            } else {
                // Use strike() to hit all nearby robots!
                robotController.strike();
            }
        } else {
            // No close robots, so search for robots within sight radius
            enemyRobots = robotController.senseNearbyRobots(-1, robotController.getTeam().opponent());
            enemyTrees = robotController.senseNearbyTrees(-1, robotController.getTeam().opponent());
            neutralTrees = robotController.senseNearbyTrees(-1, Team.NEUTRAL);

            if(enemyTrees.length > 0) {
                // If there is an enemy tree, move towards it
                tryMove(robotController, robotController.getLocation().directionTo(enemyTrees[0].getLocation()));
            } else if(enemyRobots.length > 0) {
                // If there is an enemy robot, move towards it
                tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()));
                //if(Math.random() < .5) {
                //    tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateLeftDegrees(45));
                //} else {
                //    tryMove(robotController, robotController.getLocation().directionTo(enemyRobots[0].getLocation()).rotateRightDegrees(45));
                //}
            } else if (enemyArchonLoc.x != 0 || enemyArchonLoc.y != 0) {
                tryMove(robotController, robotController.getLocation().directionTo(enemyArchonLoc));
            } else if (neutralTrees.length > 0) {
                // If there is a neutral tree, move towards it
                tryMove(robotController, robotController.getLocation().directionTo(neutralTrees[0].getLocation()));
            } else {
                // Move randomly
                tryMove(robotController, randomDirection());
            }
        }
    }
}
