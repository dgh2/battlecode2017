package comm;

import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class TestCommander extends Commander{
	int lastInvited;
	RobotController rc;
	
	
	public TestCommander(RobotController rc){
		super(rc);
		this.rc = rc;
	}
	
	public void runTestCommander(){
		setInvitesToEveryoneWeSee();
		run();
		sendBogusCommands();
		sendBogusGroupCommands();
		sendBogusWrongIDCommands();
	}
	
	boolean robotInvited = false;
	
	public void setInvitesToEveryoneWeSee(){
		if(!robotInvited){
			RobotInfo[] robotsNearby = rc.senseNearbyRobots ( 5 , rc.getTeam());
			if(robotsNearby.length>0){
				System.out.println("Sending invitation");
				sendInvite(robotsNearby[0].getID());
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

	
	
	@Override
	protected void processReport(int BotID, int[] reportData){
		System.out.println(" Received report from bot "+BotID);
		for(int i = 0 ; i < reportData.length ; i++){
			System.out.println("Report Data:"+reportData[i]);
		}
	}

}
