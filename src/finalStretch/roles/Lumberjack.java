package finalStretch.roles;

import battlecode.common.*;
import finalStretch.util.RobotBase;
import finalStretch.util.Vector;
import finalStretch.util.InformationStack; //need this

public class Lumberjack extends RobotBase {
    public static final float STRIKE_RANGE = RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;
    private BodyInfo[] enemyRobots;
    private BodyInfo[] friendlyRobots;
    private TreeInfo[] enemyTrees;
    private TreeInfo[] friendlyTrees;
    private TreeInfo[] neutralTrees;

    public Lumberjack(RobotController robotController) {
        super(robotController);
        stack = new InformationStack(this.robotController); //need this
    }

    InformationStack stack; //need this
    MapLocation readEnemy;

    @Override
    public void runOnce() throws GameActionException {
        super.runOnce();
        //System.out.println("I wanted to be... A " + getBaseType().name() + "!");
    }

    @Override
    public void beforeRun() throws GameActionException {
        super.beforeRun();
        enemyRobots = robotController.senseNearbyRobots(STRIKE_RANGE, robotController.getTeam().opponent());
        friendlyRobots = robotController.senseNearbyRobots(STRIKE_RANGE, robotController.getTeam());
        enemyTrees = robotController.senseNearbyTrees(STRIKE_RANGE, robotController.getTeam().opponent());
        friendlyTrees = robotController.senseNearbyTrees(STRIKE_RANGE, robotController.getTeam());
        neutralTrees = robotController.senseNearbyTrees(STRIKE_RANGE, Team.NEUTRAL);
        detectArchons();
        //System.out.println("I am a " + getBaseType().name() + " and I'm okay");
    }

    @Override
    public void afterRun() throws GameActionException {
        super.afterRun();
        //System.out.println("I sleep all night and I work all day");
    }

    @Override
    public void dying() throws GameActionException {
        super.dying();
        //System.out.println("I never wanted to do this in the first place!");
    }

    @Override
    public void run() throws GameActionException {
        //Handle movement
        Vector movement = calculateInfluence();
        robotController.setIndicatorLine(robotController.getLocation(),
                robotController.getLocation().translate(movement.dx, movement.dy), 255, 255, 255);
        tryMove(movement.getDirection(), movement.getDistance());

        //Handle actions
        RobotInfo[] enemyRobots = this.robotController.senseNearbyRobots(-1,this.robotController.getTeam().opponent()); //need this
        if(enemyRobots.length>0){
            System.out.println("Found robot at "+ enemyRobots[0].getLocation()+ ".Broadcasting it");
            stack.writeToStack(new MapLocation[]{enemyRobots[0].getLocation()}, 1f);
        }

        if (robotController.canStrike() && enemyRobots.length > 0) {
            robotController.strike();
        } else if (enemyTrees.length < 0 || !tryChop(enemyTrees)) {
            tryChop(neutralTrees);
        }
    }

    private boolean tryChop(TreeInfo[] trees) throws GameActionException {
        for (TreeInfo tree : trees) {
            if (robotController.getLocation().distanceTo(tree.getLocation()) - tree.getRadius()
                    > GameConstants.LUMBERJACK_STRIKE_RADIUS) {
                break;
            }
            if (tryChop(tree)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryChop(TreeInfo tree) throws GameActionException {
        if (robotController.canChop(tree.getLocation())) {
            if (tree.getHealth() <= GameConstants.LUMBERJACK_CHOP_DAMAGE) {
                System.out.println("Timber!");
            } else {
                System.out.println("Chop!");
            }
            robotController.chop(tree.getLocation());
            return true;
        }
        return false;
    }

    @Override
    protected Vector calculateInfluence() throws GameActionException {
        Vector movement = new Vector();
        readEnemy = stack.readFromStack(); //need this
        if(readEnemy != null) {
//            System.out.println("Read robot at "+ readEnemy+ ".Rebroadcasting it");
//            stack.writeToStack(new MapLocation[]{readEnemy}, 0.5f); //lower heuristic?
            movement.add(new Vector(robotController.getLocation().directionTo(readEnemy),
                    robotController.getLocation().distanceTo(readEnemy)))
                    .normalize(robotController.getType().strideRadius)
                    .scale(robotController.getType().strideRadius * 1.3f);
        }
//        for (RobotInfo robot : sensedRobots) {
//            Vector attraction = new Vector(robotController.getLocation().directionTo(robot.getLocation()),
//                    robotController.getLocation().distanceTo(robot.getLocation()))
//                    .normalize(robotController.getType().sensorRadius)
//                    .scale(robotController.getType().strideRadius);
//            if (robotController.getTeam().equals(robot.getTeam())) {
//                if (RobotType.LUMBERJACK.equals(robot.getType())) {
//                    movement.add(attraction.opposite().scale(.75f));
//                } else {
//                    movement.add(attraction.opposite().scale(.5f));
//                }
//            } else {
//                movement.add(new Vector(attraction.getDirection(),
//                        Math.min(robotController.getLocation().distanceTo(robot.getLocation()),
//                                robotController.getType().strideRadius)).scale(1.25f));
//            }
//            outputInfluenceDebugging("Lumberjack robot influence", robot, movement);
//        }
//        for (TreeInfo tree : sensedTrees) {
//            Vector attraction = new Vector(robotController.getLocation().directionTo(tree.getLocation()),
//                    robotController.getLocation().distanceTo(tree.getLocation()))
//                    .normalize(robotController.getType().sensorRadius)
//                    .scale(robotController.getType().strideRadius);
//            if (tree.getContainedRobot() != null
//                    || !robotController.getTeam().equals(tree.getTeam())) {
//                movement.add(attraction.scale(1.25f));
//            } else {
//                movement.add(attraction.opposite()).scale(.5f);
//            }
//            outputInfluenceDebugging("Lumberjack robot + tree influence", tree, movement);
//        }
//        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, .4f));
//        movement.add(dodgeBullets(sensedBullets));
//        movement.add(repelFromMapEdges(2f));
//        movement.add(repelFromPreviousPoint(1f));
        outputInfluenceDebugging("Lumberjack total influence", movement);
        return movement;
    }
}
