package josiah_boid_garden.boid;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
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
	
	/**
	 * adds a force to this Boid behavior. Forces must have a magnitude of -1 to 1 (inclusive) and the multiplier must be >0 
	 * @param force
	 * @param multiplier
	 */
	public void addForce(UnitVector force , int multiplier){
		
		
		if(multiplier > 0){
		
			System.out.println(" Adding a force of magnitude " + multiplier + "and direction "+force.getDirection().getAngleDegrees());
			
			Vector cumaForce = new Vector(force).scale(multiplier) ;
			
			cumulative.add( cumaForce );
			
			forceCount += multiplier;
		}
		
	}
	
	public void apply(){
	try {
			
			float strideLength = getAdjustedMagnitude();
			if(strideLength>0){
				Direction strideDirection = cumulative.getDirection();
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

}
