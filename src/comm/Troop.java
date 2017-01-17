package comm;

import battlecode.common.RobotController;

public abstract class Troop {
	
	private enum TroopState {FINDGROUP , OBEY}
	
	private TroopState state = TroopState.FINDGROUP;
	
	protected RobotController rc;
	
	private Communicator comms;
	
	protected Troop (RobotController rc){
		this.rc=rc;
		comms = new Communicator(rc);
	}
	
	/**
	 *find a commander and react to incoming commands. This should be run every cycle that you want this troop to react.
	 */
	final public void run(){
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
			interpretCommand(comms.getCommand(),comms.getCommandData());
			break;
		}
	}
	
	/**
	 * This function is what controls the troop based on what command is recieved.
	 */
	protected abstract void interpretCommand(int command , int[] commandData);
	
	/**
	 * sends a report to the commander
	 * @param data
	 * @return
	 */
	final public boolean sendReport(int[] data){
		comms.sendReport(data);
		return false;
	}
}
