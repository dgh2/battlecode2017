package josiah_boid_garden.roles;


import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import josiah_boid_garden.UnitVector;
import josiah_boid_garden.boid.AntiSocialResponse;
import josiah_boid_garden.boid.AvoidEdges;
import josiah_boid_garden.boid.Boid;
import josiah_boid_garden.boid.BulletResponse;
import josiah_boid_garden.util.GlobalMap;
import josiah_boid_garden.util.RobotBase;
import josiah_boid_garden.util.TeamMapArray;



public class Scout extends RobotBase {
	
	MapLocation[] archons;
	
	boolean startFound = false;
	
	private static final int CardinalDirAttraction = 10;
	
	GlobalMap map;
	
	Random rand;
	
	int searchingness = 5;
	
	enum Dir{NORTH,WEST,EAST,SOUTH,NONE}
	
	Dir checkingDirection = Dir.NONE;
	
	MapLocation start ;
	
    public Scout(RobotController robotController) {
        super(robotController);
        rand = new Random();
        rand.setSeed(robotController.getRoundNum());
        try{
        map = new GlobalMap(new TeamMapArray(this.robotController));
        } catch (Exception e){
        	System.out.println(e.getMessage());
        }
        
        start = robotController.getLocation();
    }

    @Override
    public void run() throws GameActionException {
    	
    	Boid actionController = new Boid (this.robotController);
    	
    	AntiSocialResponse antiSocial = new AntiSocialResponse(actionController);
    	antiSocial.run(this.robotController.senseNearbyRobots());

    	BulletResponse bulletDodge = new BulletResponse(actionController);
    	bulletDodge.run(this.robotController.senseNearbyBullets());
    	try {
    	//scout for edges of the map
    	scoutEdges(actionController);
    	
    	AvoidEdges edgeAvoid = new AvoidEdges(actionController);
    	edgeAvoid.run();
    	
    	} catch (Exception e){
    		System.out.println(e.getMessage());
    		e.printStackTrace(System.out);
    	}
    	
    	//apply those movements
    	actionController.apply();
    	
    	int index = map.getChannelOffsetFromLocation(this.robotController.getLocation());
    	
    	if(index > -1){
    		map.markMapLocation(this.robotController, map.translateByGridLocations( this.robotController.getLocation() , 1, 0));
    		map.markMapLocation(this.robotController, map.translateByGridLocations( this.robotController.getLocation() , -1, 0));
    		map.markMapLocation(this.robotController, map.translateByGridLocations( this.robotController.getLocation() , 0, 1));
    		map.markMapLocation(this.robotController, map.translateByGridLocations( this.robotController.getLocation() , 0, -1));

    		map.markMapLocation(this.robotController, this.robotController.getLocation());
    	} else {
    		System.out.println("Map returned : " + index);
    	}
    }
    
    public void scoutEdges(Boid controller){
    	
    	System.out.println("Scouting");
    	if(! map.check()){
    		System.out.println("  not all edges are accounted for");
    		
	    	if(checkingDirection == Dir.NONE){
	    		System.out.println("    Changing direction");
	    		checkingDirection = findDirection();
	    	}
	    	
	    	float directionMagnitude = RobotType.SCOUT.sensorRadius;
	    	
	    	switch(checkingDirection){
		    	case NORTH:
		    		try {
		    			System.out.println("      checking North");
		    			controller.addForce(new UnitVector(0,1), searchingness);
		    			MapLocation checkLocation = this.robotController.getLocation().translate(0,directionMagnitude);
						if(! this.robotController.onTheMap(checkLocation) ){
							System.out.println("      found North");
							map.setNorthBound(checkLocation.y);
							checkingDirection = Dir.NONE;
						}
						break;
					} catch (GameActionException e) {
						e.printStackTrace();
					}
		    	case EAST:
		    		try {
		    			System.out.println("      checking East");
		    			controller.addForce(new UnitVector(1,0), searchingness);
		    			MapLocation checkLocation = this.robotController.getLocation().translate(directionMagnitude,0);
						if(!this.robotController.onTheMap(checkLocation) ){
							System.out.println("      found East");
							map.setEastBound(checkLocation.y);
							checkingDirection = Dir.NONE;
						}
						break;
					} catch (GameActionException e) {
						e.printStackTrace();
					}
		    		break;
		    	case WEST:
		    		try {
		    			System.out.println("      checking West");
		    			controller.addForce(new UnitVector(-1,0), searchingness);
		    			MapLocation checkLocation = this.robotController.getLocation().translate(-directionMagnitude,0);
						if(!this.robotController.onTheMap(checkLocation) ){
							System.out.println("      found West");
							map.setWestBound(checkLocation.y);
							checkingDirection = Dir.NONE;
						}
						break;
					} catch (GameActionException e) {
						e.printStackTrace();
					}
		    		break;
		    	case SOUTH:
		    		try {
		    			System.out.println("      checking South");
		    			controller.addForce(new UnitVector(0,-1), searchingness);
		    			MapLocation checkLocation = this.robotController.getLocation().translate(0,-directionMagnitude);
						if(!this.robotController.onTheMap(checkLocation) ){
							System.out.println("      found South");
							map.setSouthBound(checkLocation.y);
							checkingDirection = Dir.NONE;
						}
						break;
					} catch (GameActionException e) {
						e.printStackTrace();
					}
		    		break;
	    		default:
	    			System.out.println("      checking ... well nothing.");
	    	}
	    	
    	} else {
    		System.out.println("All edges found");
    		scoutRandom(controller);
    	}
    }
    
    private Dir findDirection(){
    	
    	
    	int dirIndex = rand.nextInt(4);
    	
    	switch(dirIndex){
    	case 0:
    		if(! map.hasWestBound()){
    			return Dir.WEST;
    		}
    		

    	case 1:
    		if(! map.hasNorthBound()){
    			return Dir.NORTH;
    		}

    	case 2:
    		if(! map.hasEastBound()){
    			return Dir.EAST;
    		}

    	case 3:
    	default:
    		return Dir.SOUTH;
    	}
    	
    }
    
    boolean targetAcquired;
    
    MapLocation target;
    
    
    public void scoutRandom(Boid controller){
    	
	    if( ! targetAcquired){
	    	System.out.println("Acquiring target");
	    	targetAcquired = true;
	    	target = acquireUnexploredLocation();
	    	
	    } else {
	    	System.out.println("Moving to location");
	    	controller.addLinearAttraction(target, 10);
	    	if(this.robotController.getLocation().distanceTo(target) < 2){
	    		targetAcquired = false;
	    	}
	    }
    	
    	
    }
    
    private MapLocation acquireUnexploredLocation(){
    	//check 10 times for a place that's not been checked
    	for(int i = 0 ; i < 10 ; i++){
    		MapLocation check = this.robotController.getLocation().translate( (float) (Math.random()*30-15),(float) (Math.random()*30-15));
    		if(map.getQualityOfData(check)==0){
    			return check;
    		}
    	}
    	//or go somewhere random
    	return this.robotController.getLocation().translate( (float) (Math.random()*20-10),(float) (Math.random()*20-5));
    	
    }
    
    
    
    
}