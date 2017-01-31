package josiah_boid_garden.util;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class TeamMapArray implements MapArray{

	RobotController rc;
	
	public TeamMapArray(RobotController rc){
		this.rc=rc;
	}
	
	@Override
	public float readBroadcast(int channel) {
		float retValue = 0;
		try {
			retValue = rc.readBroadcastFloat(channel);
		} catch (GameActionException e) {

		}
		return retValue;
		
	}

	@Override
	public void broadcast(int channel, float value) {
		try {
			rc.broadcastFloat(channel, value);
		} catch (GameActionException e) {

		}
		
	}

}
