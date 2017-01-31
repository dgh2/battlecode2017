package boidrolesv2.roles;

import battlecode.common.*;
import boidrolesv2.util.RobotBase;
import boidrolesv2.util.Vector;
import boidrolesv2.gardening.Formation;
import boidrolesv2.gardening.Maintainer;
import boidrolesv2.util.InformationStack;

import static rolesplayer.util.Util.randomDirection;


public class Gardener extends RobotBase {

    //variables for garden making
    private boolean Glock = false;
//    private boolean JustSpawned = true;
Maintainer maintainer; //need this
    Formation formation;

    InformationStack stack; //need this
    MapLocation readEnemy;

    public Gardener(RobotController robotController) {
        super(robotController);
        stack = new InformationStack(this.robotController); //need this
        maintainer = new Maintainer(robotController); //need this
        formation = new Formation(robotController, Math.random() < .5 ? Direction.SOUTH : Direction.NORTH, Formation.Form.C);
    }

//    private TreeInfo[] neutralTrees;
    float speed;

    @Override
    public void run() throws GameActionException {
//        maintain(); //TreeInfo[] trees = robotController.senseNearbyTrees(2f, robotController.getTeam());
        maintainer = new Maintainer(this.robotController); //need this
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
        RobotInfo[] enemyRobots = this.robotController.senseNearbyRobots(-1,this.robotController.getTeam().opponent()); //need this
        if(enemyRobots.length>0){
            System.out.println("Found robot at "+ enemyRobots[0].getLocation()+ ".Broadcasting it");
            stack.writeToStack(new MapLocation[]{enemyRobots[0].getLocation()}, 1f);
        }

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
//            formation = new Formation(robotController, getDirectionToInitialEnemyArchonLocation(), Formation.Form.C); //need this
            formation = new Formation(robotController, Direction.EAST, Formation.Form.C); //need this
            formation.plant();
            if(formation.hasPlanted()) {
                Glock = true;
            }
        }
        //defence!?
        if(robotController.getRoundNum() > 10 && robotController.getRoundNum() <= 120 && robotController.canBuildRobot(RobotType.LUMBERJACK, dir) && robotController.isBuildReady()) { // 15 turns hoping to build a lumberjack
            robotController.buildRobot(RobotType.LUMBERJACK, dir);
        }
        if(Glock) {
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
            if (robotController.canBuildRobot(RobotType.LUMBERJACK, Direction.WEST) && Math.random() < .4 && robotController.isBuildReady()) {
                robotController.buildRobot(RobotType.LUMBERJACK, Direction.WEST);
            } else if (robotController.canBuildRobot(RobotType.TANK, Direction.WEST) && Math.random() < .6 && robotController.isBuildReady()) {
                robotController.buildRobot(RobotType.TANK, Direction.WEST);
            }
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
            if (RobotType.ARCHON.equals(robot.getType())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 2f).scale(getInverseScaling(robot.getLocation())));
            }
            if (!robotController.getTeam().equals(robot.getTeam())) {
//                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
//                        robotController.getType().strideRadius)
//                        .scale(getScaling(robot.getLocation())));
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius)
                        .scale(getScaling(robot.getLocation())));
            } else {
//                if (!robotController.getType().equals(robot.getType())) {
//                    movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()),
//                            robotController.getType().strideRadius)
//                            .scale(getScaling(robot.getLocation())));
//                }
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 3f)
                        .scale(getInverseScaling(robot.getLocation())));
            }
            if (RobotType.LUMBERJACK.equals(robot.getType())) {
                movement.add(new Vector(robotController.getLocation().directionTo(robot.getLocation()).opposite(),
                        robotController.getType().strideRadius * 2f).scale(getInverseScaling(robot.getLocation())));
            }
            outputInfluenceDebugging("Gardener robot influence", robot, movement);
        }
        for (TreeInfo tree : sensedTrees) {
            if (robotController.getTeam().equals(tree.getTeam()) && tree.getHealth() < tree.getMaxHealth()) {
                movement.add(new Vector(robotController.getLocation().directionTo(tree.getLocation()),
                        robotController.getType().strideRadius).scale(tree.getHealth() / tree.getMaxHealth()));
                outputInfluenceDebugging("Gardener robot + tree influence", tree, movement);
            }
        }
        movement.add(getInfluenceFromInitialEnemyArchonLocations(false, .05f));
//        movement.add(getInfluenceFromTreesWithBullets(sensedTrees));
//        movement.add(getInfluenceFromTrees(sensedTrees));
        movement.add(dodgeBullets(sensedBullets));
        movement.add(repelFromMapEdges(.5f));
        outputInfluenceDebugging("Gardener total influence", movement);
        return movement;
    }
}

