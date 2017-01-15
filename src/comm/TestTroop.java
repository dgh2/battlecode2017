package comm;

import battlecode.common.RobotController;

public class TestTroop {

	Troop troop;
	
	public TestTroop(RobotController rc){
		troop = new Troop(rc);
	}
	
	public void run(){
		troop.run();
	}
}
