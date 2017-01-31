package finalEntry.roles;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
import finalEntry.util.RobotBase;
import finalEntry.util.Vector;
import finalEntry.gardening.*;

import static rolesplayer.util.Util.randomDirection;


public class Gardener extends RobotBase {

    //variables for garden making
    private boolean Glock = false;
    Maintainer maintainer;
    Formation formation;
//    private boolean JustSpawned = true;

    public Gardener(RobotController robotController) {
        super(robotController);
        maintainer = new Maintainer(robotController);
        formation = new Formation(robotController, Math.random() < .5 ? Direction.SOUTH : Direction.NORTH, Formation.Form.C);
    }

//    private TreeInfo[] neutralTrees;
    float speed;

    @Override
    public void run() throws GameActionException {

        maintainer.maintain(); //josiah's maintain code

        //Handle movement
        if (!Glock) { // only perform movement when not in gardening mode
            Vector movement = calculateInfluence();
            robotController.setIndicatorLine(robotController.getLocation(),
                    robotController.getLocation().translate(movement.dx, movement.dy), 255, 255, 255);
            speed = movement.getDistance();
            tryMove(movement.getDirection(), speed);
        }

        //Handle actions

        // Generate a random direction
        Direction dir = randomDirection();

        //if (rc.canBuildRobot(RobotType.SCOUT, dir) && (rc.getRoundNum() <= 4 || Math.random() < .5) && rc.isBuildReady()) {
        //    rc.buildRobot(RobotType.SCOUT, dir);
        //} else if (rc.canPlantTree(dir) && Math.random() < .3) {
        //    rc.plantTree(dir);
        //} else if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .25) {
        //    rc.buildRobot(RobotType.SOLDIER, dir);
        //} else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .2 && rc.isBuildReady()) {
        //    rc.buildRobot(RobotType.LUMBERJACK, dir);
        //} else if (rc.canBuildRobot(RobotType.TANK, dir) && Math.random() < .1 && rc.isBuildReady()) {
        //    rc.buildRobot(RobotType.TANK, dir);
        //}

        //todo: use a method for finding available direction to build something or plant a tree

        //todo: remember what you've built, build units on a build order

        //scoutrush?
        if(robotController.getRoundNum() <=4 && robotController.canBuildRobot(RobotType.SCOUT, dir) && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.SCOUT, dir); // maybe 3 turns to build a scout or two or three lol
        }
        //trees!! need those haha
        if(robotController.getRoundNum() > 3 && speed < 0.5) { // we might have built a scout by now and we have stopped running away from things
            System.out.println("Trying to build a garden");
//            if (plantGarden1()){
//                Glock = true; //if planting was sucessful, lock us in this position. maybe later check if all trees r dead
//            }

            //plant trees, if
            formation.plant();
            if(formation.hasPlanted()) {
                Glock = true;
            }


        }
        //defence!?
        if(robotController.getRoundNum() > 10 && robotController.getRoundNum() <= 120 && robotController.canBuildRobot(RobotType.LUMBERJACK, dir) && robotController.isBuildReady()) { // 15 turns hoping to build a lumberjack
            robotController.buildRobot(RobotType.LUMBERJACK, dir);
        }

        if(Glock) { //make sure we continue planting the garden
            formation.plant();
        }

//         Randomly attempt to build a Soldier or lumberjack in this direction
//        if (robotController.canBuildRobot(RobotType.SCOUT, dir) && Math.random() < .1 && robotController.isBuildReady()) {
//            robotController.buildRobot(RobotType.SCOUT, dir);
//        } else if (robotController.canPlantTree(dir) && Math.random() < .1 && robotController.isBuildReady()) {
//            robotController.plantTree(dir);
//        } else if (robotController.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .15 && robotController.isBuildReady()) {
//            robotController.buildRobot(RobotType.SOLDIER, dir);
//        } else if (robotController.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .15 && robotController.isBuildReady()) {
//            robotController.buildRobot(RobotType.LUMBERJACK, dir);
//        } else if (robotController.canBuildRobot(RobotType.TANK, dir) && Math.random() < .08 && robotController.isBuildReady()) {
//            robotController.buildRobot(RobotType.TANK, dir);
//        }
        if (robotController.canBuildRobot(RobotType.TANK, dir) && Math.random() < .5 && robotController.isBuildReady()) {
            robotController.buildRobot(RobotType.TANK, dir);
        }

        //chop down forrest - if you see a neutral tree, make a lumberjack
        if(sensedTrees.length > 0) {
            robotController.buildRobot(RobotType.LUMBERJACK, Direction.WEST);
        }

//        for (TreeInfo tree : sensedTrees) {
//            if (robotController.getTeam().equals(tree.getTeam())
//                    && tree.getHealth() < .8 * tree.getMaxHealth()
//                    && robotController.canWater(tree.getLocation())) {
//                robotController.water(tree.getLocation());
//                break;
//            }
//        }



    }

    @Override
    protected Vector calculateInfluence() throws GameActionException {
        Vector movement = new Vector();
        for (RobotInfo robot : sensedRobots) {
            Vector attraction = new Vector(robotController.getLocation().directionTo(robot.getLocation()),
                    robotController.getLocation().distanceTo(robot.getLocation()))
                    .normalize(robotController.getType().sensorRadius)
                    .scale(robotController.getType().strideRadius);
            movement.add(attraction.opposite().scale(1.25f));
            outputInfluenceDebugging("Gardener robot influence", robot, movement);
        }
        for (TreeInfo tree : sensedTrees) {
            if (robotController.getTeam().equals(tree.getTeam()) && tree.getHealth() < tree.getMaxHealth()) {
                Vector attraction = new Vector(robotController.getLocation().directionTo(tree.getLocation()),
                        robotController.getLocation().distanceTo(tree.getLocation()))
                        .normalize(robotController.getType().sensorRadius)
                        .scale(robotController.getType().strideRadius);
                movement.add(attraction.scale(1.25f).scale(tree.getHealth() / tree.getMaxHealth()));
                outputInfluenceDebugging("Gardener robot + tree influence", tree, movement);
            }
        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(false, .05f));
//        movement.add(getInfluenceFromTreesWithBullets(sensedTrees));
//        movement.add(getInfluenceFromTrees(sensedTrees));
        movement.add(dodgeBullets(sensedBullets));
        //movement.add(repelFromMapEdges());
        outputInfluenceDebugging("Gardener total influence", movement);
        return movement;
    }
}

