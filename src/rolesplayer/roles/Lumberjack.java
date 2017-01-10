package rolesplayer.roles;

import battlecode.common.*;
import rolesplayer.util.RobotBase;

import static rolesplayer.util.Util.randomDirection;
import static rolesplayer.util.Util.tryMove;

public class Lumberjack extends RobotBase {
    public Lumberjack(RobotController rc) {
        super(rc);
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
        int xPos = rc.readBroadcast(0);
        int yPos = rc.readBroadcast(1);
        MapLocation enemyArchonLoc = new MapLocation(xPos, yPos);

        // See if there are any enemy and friendly robots or trees within striking range (distance 1 from lumberjack's radius)
        float strikingRange = RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS;
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(strikingRange, rc.getTeam().opponent());
        RobotInfo[] friendlyRobots = rc.senseNearbyRobots(strikingRange, rc.getTeam());
        TreeInfo[] enemyTrees = rc.senseNearbyTrees(strikingRange, rc.getTeam().opponent());
        TreeInfo[] ourTrees = rc.senseNearbyTrees(strikingRange, rc.getTeam());
        TreeInfo[] neutralTrees = rc.senseNearbyTrees(strikingRange, Team.NEUTRAL);

        if((enemyRobots.length > 0 || enemyTrees.length > 0 || neutralTrees.length > 0) && !rc.hasAttacked()) {
            if(friendlyRobots.length != 0) {
                // Too close to friendly units
                tryMove(rc, rc.getLocation().directionTo(friendlyRobots[0].getLocation()).opposite());
            } else if(ourTrees.length != 0) {
                // Too close to our trees
                tryMove(rc, rc.getLocation().directionTo(ourTrees[0].getLocation()).opposite());
            } else {
                // Use strike() to hit all nearby robots!
                rc.strike();
            }
        } else {
            // No close robots, so search for robots within sight radius
            enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            enemyTrees = rc.senseNearbyTrees(-1, rc.getTeam().opponent());
            neutralTrees = rc.senseNearbyTrees(-1, Team.NEUTRAL);

            if(enemyTrees.length > 0) {
                // If there is an enemy tree, move towards it
                tryMove(rc, rc.getLocation().directionTo(enemyTrees[0].getLocation()));
            } else if(enemyRobots.length > 0) {
                // If there is an enemy robot, move towards it
                tryMove(rc, rc.getLocation().directionTo(enemyRobots[0].getLocation()));
                //if(Math.random() < .5) {
                //    tryMove(rc, rc.getLocation().directionTo(enemyRobots[0].getLocation()).rotateLeftDegrees(45));
                //} else {
                //    tryMove(rc, rc.getLocation().directionTo(enemyRobots[0].getLocation()).rotateRightDegrees(45));
                //}
            } else if (enemyArchonLoc.x != 0 || enemyArchonLoc.y != 0) {
                tryMove(rc, rc.getLocation().directionTo(enemyArchonLoc));
            } else if (neutralTrees.length > 0) {
                // If there is a neutral tree, move towards it
                tryMove(rc, rc.getLocation().directionTo(neutralTrees[0].getLocation()));
            } else {
                // Move randomly
                tryMove(rc, randomDirection());
            }
        }
    }
}
