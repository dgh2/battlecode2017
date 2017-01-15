package comm;

import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class TestCommander {
	Commander commander;
	int lastInvited;

	public TestCommander(RobotController rc){
		commander = new Commander(rc);
	}
	
	public void run(){
		setInvitesToEveryoneWeSee();
		commander.run();
		sendBogusCommands();
		sendBogusGroupCommands();
		sendBogusWrongIDCommands();
	}
	
	boolean robotInvited = false;
	
	public void setInvitesToEveryoneWeSee(){
		if(!robotInvited){
			RobotInfo[] robotsNearby = commander.rc.senseNearbyRobots( 5 , commander.rc.getTeam());
			if(robotsNearby.length>0){
				System.out.println("Sending invitation");
				commander.sendInvite(robotsNearby[0].getID());
				lastInvited = robotsNearby[0].getID();
				robotInvited = true;
			}
		}
	}
	
	int commandIndex = 1;
	public void sendBogusCommands(){
		if(commandIndex<10){
			commander.sendIndividualCommand(commandIndex, new int[]{}, lastInvited);
			commandIndex++;
		}
	}
	
	int groupCommandIndex = 10000;
	public void sendBogusGroupCommands(){
		if(commandIndex>=10 && groupCommandIndex <10010){
			commander.sendGroupCommand(groupCommandIndex, new int[]{});
			groupCommandIndex++;
		}
	}
	
	int wrongCommandIndex = 3000;
	public void sendBogusWrongIDCommands(){
		if(groupCommandIndex>=10010 && wrongCommandIndex<3005){
			commander.sendIndividualCommand(commandIndex, new int[]{}, 6541);
			groupCommandIndex++;
		}
	}

}
