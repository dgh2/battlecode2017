package josiah_boid_garden.gardening;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Migrant {
	
	RobotController rc;
	PlantingFormation formation;
	Direction migrationDirection = Direction.getWest();
	float adjustmentDegrees = 15;
	
	int threshHold = 3;
	
	public Migrant(RobotController rc , PlantingFormation formation){
		this.rc = rc;
		this.formation = formation;
	}
	
	
	
	public boolean migrate(){
		
		if( formation.getTreesPlantableInFormation() >= threshHold ){
			return true;
		} else {
			
			if(rc.canMove(migrationDirection)){
				try {
					rc.move(migrationDirection);
				} catch (GameActionException e) {
					migrationDirection=migrationDirection.rotateRightDegrees(adjustmentDegrees);
				}
			} else {
				migrationDirection=migrationDirection.rotateRightDegrees(adjustmentDegrees);
			}
			
			return false;
		}
		
		
	}
	
	

}
