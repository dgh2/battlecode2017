package comm;

import battlecode.common.RobotController;

public class Troop {
	
	enum TroopState {FINDGROUP , OBEY}
	
	TroopState state = TroopState.FINDGROUP;
	
	RobotController rc;
	
	Communicator comms;
	
	Troop (RobotController rc){
		this.rc=rc;
		comms = new Communicator(rc);
	}
	
	/**
	 *find a commander and react to incoming commands. This should be run every cycle that you want this troop to react.
	 */
	public void run(){
		switch(state){
		
		case FINDGROUP:
			System.out.println("Checking for groups");
			boolean invited = comms.acceptGroupInvite();
			if(invited){
				System.out.println("I have accepted a group invite");
				this.state = TroopState.OBEY;
			}
			
			break;
		case OBEY:
			System.out.println("Interpretting commands");
			interpretCommand();
			interpretData();
			break;
		}
	}
	
	/**
	 * This function is what controls the troop based on what command is recieved.
	 */
	protected void interpretCommand(){
		int command = comms.getCommand();
		if(command!=0){
			System.out.println("Command Received: " + command);
		}
	}
	
	protected void interpretData(){
		int[] command = comms.getCommandData();
		if(command.length!=0){
			String com = new String();
			System.out.println("Command Data recieved: " + command);
		}
	}
}
