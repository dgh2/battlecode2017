package josiah_boid_garden.monteCarlo;

import battlecode.common.Direction;
import battlecode.common.RobotController;

public class CanPlantMetric implements AttractivenessMetric{

	RobotController rc;
	private static float plantingDistanceThreshold = 1.5f;
	
	CanPlantMetric(RobotController rc){
		this.rc = rc;
	}
	//we REALLY don't want to plant places that we can't
	@Override
	public int appraiseAttractiveness(McPoint point) {
		
		if(rc.getLocation().distanceTo(point.location)<plantingDistanceThreshold
				&& rc.canPlantTree(new Direction(rc.getLocation(),point.location))){
			return 0;
		} else {
			return -10000;
		}
	}
	
	

}
