package comm;

import battlecode.common.RobotController;

public class TestTroop extends Troop{

	public TestTroop(RobotController rc) {
		super(rc);
	}

	@Override
	protected void interpretCommand() {
		int command = comms.getCommand();
		if(command!=0){
			System.out.println("Command Received: " + command);
			System.out.println("Command Data recieved: " + command);
		}
		
		
	}
	

	
	
}
