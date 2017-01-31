package josiah_boid_garden.monteCarlo;

import battlecode.common.RobotController;

public class CanMoveMetric implements AttractivenessMetric{
	
	RobotController rc;
	
	public CanMoveMetric(RobotController rc){
		this.rc = rc;
	}
	
	/**
	 * this metric makes it so that places we can't move are severly punished.
	 */
	public int appraiseAttractiveness( McPoint point){
		if(rc.canMove(point.location)){
			return 1;
		} else {
			return -1000;
		}
	}
	
	
}
