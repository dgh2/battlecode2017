package josiah_boid_garden.boid;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import josiah_boid_garden.UnitVector;
import josiah_boid_garden.Vector;

public class Boid{
	
	int forceCount = 0;
	
	Vector cumulative = new Vector( 0 , 0);
	
	RobotController rc;
	
	public Boid(RobotController rc){
		this.rc = rc;
	}
	
	public RobotController getRobotController(){
		return rc;
	}
	
	/**
	 * adds a force to this Boid behavior. Forces must have a magnitude of -1 to 1 (inclusive) and the multiplier must be >0 
	 * @param force
	 * @param multiplier
	 */
	public void addForce(UnitVector force , int multiplier){

		if(multiplier > 0){
					
			Vector cumaForce = new Vector(force).scale(multiplier) ;
			
			cumulative.add( cumaForce );
			
			forceCount += multiplier;
		}
		
	}
	
	public void addLinearAttraction(MapLocation loc , float magnitude){
		this.addForce(new UnitVector(rc.getLocation() , loc), (int)(magnitude / (1+loc.distanceTo(rc.getLocation()))));
	}
	
	public void addLinearRepulsion(MapLocation loc , float magnitude){
		this.addForce(new UnitVector(loc , rc.getLocation()  ), (int)(magnitude / (1+loc.distanceTo(rc.getLocation()))));
	}
	
	public void addSquaredRepulsion(MapLocation loc , float magnitude){
		
		this.addForce(new UnitVector(loc , rc.getLocation()  ), (int)(magnitude / (1+loc.distanceSquaredTo(rc.getLocation()))));
		
	}
	
	public void addPreferedDistance(MapLocation loc , float distance , float bandWidth , int magnitude){
		float dist = loc.distanceTo(rc.getLocation());
		if(dist < (distance - (bandWidth/2))){
			addLinearAttraction(loc, magnitude);
		} else if (dist > distance+(bandWidth/2)){
			addLinearRepulsion(loc,magnitude);
		}
		
	}
	
	public void addSquaredAttraction(MapLocation loc , float magnitude){
		
		this.addForce(new UnitVector( rc.getLocation() , loc  ), (int)(magnitude / (1+loc.distanceSquaredTo(rc.getLocation()))));
		
	}
	
	public void applyConstantRotationalForce(MapLocation loc, boolean counterClockwise , int magnitude){
		
		Direction dir = new Direction (rc.getLocation() , loc);
		
		if(counterClockwise){
			dir = dir.rotateLeftDegrees(90);
		} else {
			dir = dir.rotateRightDegrees(90);
		}
		
		UnitVector moveDir = new UnitVector(dir);
		
		this.addForce(moveDir, magnitude);
	}
	
	public boolean apply(){
	try {
			
			float strideLength = getAdjustedMagnitude();
			if(strideLength>0){
				Direction strideDirection = cumulative.getDirection();
				//try to move to the place you can
				if(rc.canMove(strideDirection, strideLength)){
						rc.move(strideDirection, strideLength);
						return true;
					//if you can't, at least try to move in that general direction
				} else if (rc.canMove(strideDirection)){
					rc.move(strideDirection);
				} // well, if you can't even move the direction, guess you're SOL
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public enum Dir{LEFT,RIGHT}
	
	public void applyPerpendicular(Dir direction){
		try{
			float strideLength = getAdjustedMagnitude();
			if(strideLength>0){
				Direction strideDirection = cumulative.getDirection();
				if(direction == Dir.LEFT){
					strideDirection = strideDirection.rotateLeftDegrees(90);
				}else {
					strideDirection = strideDirection.rotateRightDegrees(90);
				}
				//try to move to the place you can
				if(rc.canMove(strideDirection, strideLength)){
						rc.move(strideDirection, strideLength);
					//if you can't, at least try to move in that general direction
				} else if (rc.canMove(strideDirection)){
					rc.move(strideDirection);
				} // well, if you can't even move the direction, guess you're SOL
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}	
	}
	
	
	/**
	 * The cumulative vector stored is actually the summation of a bunch of unit vectors (effectively).
	 * In order to decompose that into a single vector of magnitude =<1, we divide it by the number of 
	 * forces have affected it.
	 * 
	 * After we have a vector with a magnitude between 1 and 0, we scale this to match the robot's stride radius 
	 * so that we end up with a direction and a percentage of the robot's available movement speed.
	 * @return
	 */
	private float getAdjustedMagnitude(){
		float baseMagnitude = cumulative.getMagnitude();
		baseMagnitude /= forceCount;
		baseMagnitude *= rc.getType().strideRadius;
		return baseMagnitude;
	}
	
	public Direction getDirection(){
		return cumulative.getDirection();
	}
	
	public float getMagnitude(){
		return cumulative.getMagnitude();
	}

}
