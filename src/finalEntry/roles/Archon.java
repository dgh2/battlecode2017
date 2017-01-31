package finalEntry.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import finalEntry.util.RobotBase;
import finalEntry.util.Vector;

import static boidroles.util.Util.randomDirection;

public class Archon extends RobotBase {
    public Archon(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void run() throws GameActionException {
        //Handle movement
        Vector movement = calculateInfluence();
        robotController.setIndicatorLine(robotController.getLocation(),
                robotController.getLocation().translate(movement.dx, movement.dy), 255, 255, 255);
        Direction go = movement.getDirection();
        tryMove(go, movement.getDistance());

        //Handle actions
        //todo: remove randomness, plan building better

        // Generate a random direction
        Direction dir = randomDirection();



        //todo: more behavior code like below:


        // if turn zero or one, buy a victory point
        if(robotController.getRoundNum() <=1){
            robotController.donate(getDonationQty(1)); //getDonationQty returns #bullets based on current round's exchange rate
        }

        // Build a gardener on the first turn possible! even if that's "zero"
        if(robotController.getRoundNum() <=45) {
                if(robotController.canHireGardener(Direction.NORTH)) {
                    robotController.hireGardener(Direction.NORTH);
                }
                else if (robotController.canHireGardener(Direction.EAST)) {
                    robotController.hireGardener(Direction.EAST);
                }
                else if (robotController.canHireGardener(Direction.SOUTH)) {
                    robotController.hireGardener(Direction.SOUTH);
                }
            } else if (robotController.canHireGardener(dir)) {
                robotController.hireGardener(dir);
        }


        if(robotController.getRoundNum() > 150) {
            // Randomly attempt to build a Gardener in this direction
            if (Math.random() < .2 && robotController.getTeamBullets() > 500f) {
                if (robotController.canHireGardener(Direction.NORTH)) {
                    robotController.hireGardener(Direction.NORTH);
                } else if (robotController.canHireGardener(Direction.EAST)) {
                    robotController.hireGardener(Direction.EAST);
                } else if (robotController.canHireGardener(Direction.SOUTH)) {
                    robotController.hireGardener(Direction.SOUTH);
                }
            } else if (robotController.canHireGardener(dir)) {
                robotController.hireGardener(dir);
            }
        }

    }

    @Override
    protected Vector calculateInfluence() throws GameActionException {
        Vector movement = new Vector();
        for (RobotInfo robot : sensedRobots) {
            Vector attraction = new Vector(robotController.getLocation().directionTo(robot.getLocation()),
                    robotController.getLocation().distanceTo(robot.getLocation()))
                    .normalize(robotController.getType().sensorRadius)
                    .scale(robotController.getType().strideRadius);
            if (RobotType.LUMBERJACK.equals(robot.getType())) {
                movement.add(attraction.opposite().scale(1.5f));
            } else if (!robotController.getTeam().equals(robot.getTeam())) {
                movement.add(attraction.opposite().scale(1.25f));
            }
            outputInfluenceDebugging("Robot influence", robot, movement);
        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, 0.25f));
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees, 2f));
        movement.add(getInfluenceAwayFromTrees(sensedTrees, .5f));
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges(3f));
        movement.add(repelFromPreviousPoint(1f));
        outputInfluenceDebugging("Total influence", movement);
        return movement;
    }
}
