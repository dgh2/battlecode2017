package smartboidrolesv1.roles;

import battlecode.common.*;
import smartboidrolesv1.util.RobotBase;
import smartboidrolesv1.util.Vector;
import finalStretch.util.InformationStack; //need this

public class Lumberjack extends RobotBase {
    public static final float STRIKE_RANGE = RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;
    private BodyInfo[] enemyRobots;
    private BodyInfo[] friendlyRobots;
    private TreeInfo[] enemyTrees;
    private TreeInfo[] friendlyTrees;
    private TreeInfo[] neutralTrees;

    InformationStack stack; //need this
    MapLocation readEnemy;

    public Lumberjack(RobotController robotController) {
        super(robotController);
        stack = new InformationStack(this.robotController); //need this
    }

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
        if (robotController.canStrike()
                && enemyRobots.length > 0
                && (enemyRobots.length + enemyTrees.length > friendlyRobots.length)) {
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
                    .scale(robotController.getType().strideRadius);
        }
        for (RobotInfo robot : sensedRobots) { //todo: continue if id is equal to own id
            if (!robotController.getTeam().equals(robot.getTeam())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
                        robotController.getType().strideRadius)
                        .scale(getScaling(robot.getLocation())));
//                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
//                        robotController.getType().strideRadius)
//                        .scale(getInverseScaling(robot.getLocation())));
            } else {
//                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
//                        robotController.getType().strideRadius)
//                        .scale(getScaling(robot.getLocation())));
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 3f)
                        .scale(getInverseScaling(robot.getLocation())));
            }
            if (RobotType.LUMBERJACK.equals(robot.getType()) && robotController.getTeam().equals(robot.getTeam())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 2f).scale(getInverseScaling(robot.getLocation())));
            }
            outputInfluenceDebugging("Lumberjack robot influence", robot, movement);
        }
        for (TreeInfo tree : sensedTrees) {
            if (tree.getContainedRobot() != null) {
                movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()),
                        robotController.getType().strideRadius * 1f)
                        .scale(getScaling(tree.getLocation())));
            }
//            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()),
//                    robotController.getType().strideRadius*.1f).scale(1f));
//            movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()).opposite(),
//                    robotController.getType().strideRadius * .1f)
//                    .scale(getInverseScalingUntested(tree.getLocation())));
            outputInfluenceDebugging("Lumberjack robot + tree influence", tree, movement);
        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(true, .4f));
        movement.add(getInfluenceFromTreesWithBullets(sensedTrees));
//        movement.add(getInfluenceFromTrees(sensedTrees));
        //todo: stay away from our own bullet trees
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges(1f));
        outputInfluenceDebugging("Lumberjack total influence", movement);
        return movement;
    }
}
