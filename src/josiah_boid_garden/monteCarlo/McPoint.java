package josiah_boid_garden.monteCarlo;

import battlecode.common.MapLocation;

public class McPoint{
	
	MapLocation location;
	int value = 0;
	
	McPoint(MapLocation location){
		this.location = location;
	}
	
	public MapLocation getLocation(){
		return location;
	}
	
	public McPoint addValue(int value){
		this.value += value;
		return this;
	}
	
	public int getValue(){
		return value;
	}
	
	
	
	
}
