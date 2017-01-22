package josiah_boid_garden.monteCarlo;

import battlecode.common.Direction;
import battlecode.common.RobotController;

/**
 * this monte carlo point generator creates 16 points.
 * There are two points at each major and minor cardinal direction (one for a full step and one for a half step)
 * @author user
 *
 */
public class CardinalPoints implements PointGenerator {

	RobotController rc;
	
	//The 4 cardinal directions and 4 intermediate directions
	private static final Direction[] dirs = new Direction[]{
															Direction.NORTH,
															Direction.SOUTH,
															Direction.EAST,
															Direction.WEST,
															new Direction(Direction.NORTH.radiansBetween(Direction.WEST)),
															new Direction(Direction.NORTH.radiansBetween(Direction.EAST)),
															new Direction(Direction.SOUTH.radiansBetween(Direction.WEST)),
															new Direction(Direction.SOUTH.radiansBetween(Direction.EAST))
														}; 

	
	CardinalPoints(RobotController rc){
		this.rc = rc;
	}
	
	@Override
	public void addPoints(monteCarlo mc) {
		
		for(int i = 0 ; i<dirs.length;i++){
			//get full stride away
			mc.addPoint(rc.getLocation().add(dirs[i], rc.getType().strideRadius));
			//get half stride away
			mc.addPoint(rc.getLocation().add(dirs[i], rc.getType().strideRadius));
		}
				
		
	
		
	}

}
