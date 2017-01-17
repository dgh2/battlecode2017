package comm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import battlecode.common.RobotController;

/**
 * The commander 
 * @author user
 *
 */
public abstract class Commander {
	
	private Communicator comms;
	
	private enum CommanderState { SECUREGROUP , GETINVITES , INVITING} 
	private CommanderState state = CommanderState.SECUREGROUP;
	
	private List<Integer> acceptedIDs = new ArrayList<Integer>();
	private Deque<Integer> invitedBots = new ArrayDeque<Integer>();
	private int maxStrikes = 5;
	private int strikes = 0;
	
	/**
	 * constructor
	 * @param rc
	 */
	protected Commander(RobotController rc){
		comms = new Communicator(rc);
		
	}
	
	/**
	 * a very important function. It must be run every turn if you want the commander to be able to 
	 * secure it's group and send invites and recieve confirmations from other robots
	 */
	final public void run(){
		switch(state){
		case SECUREGROUP:
			boolean groupFound = secureGroup();
			if(groupFound){
				state = CommanderState.GETINVITES;
			}
			break;
		case INVITING:
			if(comms.getAcceptedInviteBotID() == invitedBots.getFirst()){
				acceptedIDs.add(invitedBots.pop());
				state = CommanderState.GETINVITES;
			} else {
				strikes++;
			}
			//We don't want one invite clogging all the other invites.
			if(strikes>maxStrikes){
				System.out.println(invitedBots.getFirst() + "failed to accept invitation");
				invitedBots.pop();
				state = CommanderState.GETINVITES;
			}
			break;
		case GETINVITES:
			if(! invitedBots.isEmpty() ){
				acquireRobot(invitedBots.getFirst());
				strikes = 0;
				state = CommanderState.INVITING;
			}
			break;
		}
		
		if(comms.fetchReport()){
			processReport ( comms.getLastReportID() , comms.getLastReportData() );
		}

	}

	/**
	 * This function NEEDS to be overriden to process reports recieved by troops.
	 * @param BotID
	 * 		The bot that sent the report
	 * @param reportData
	 * 		The data contained in the report.
	 */
	protected abstract void processReport(int BotID , int[] reportData);
	
	/**
	 * Send an invitation to a bot to join the commander's group
	 * @param id
	 */
	final public void sendInvite(int id){
		invitedBots.addLast(new Integer(id));
	}
	
	/**
	 * send a command to the whole group
	 * @param command
	 * @param data
	 */
	final public void sendGroupCommand(int command , int[] data){
		comms.sendCommand(data,command, true , 0);
	}
	
	/**
	 * send a command to an individual
	 * @param command
	 * @param data
	 * @param targetID
	 */
	final public void sendIndividualCommand(int command , int[] data, int targetID){
		comms.sendCommand(data,command, false , targetID);
	}
	
	/**
	 * returns all the bots in this group
	 * @return
	 * 		All the bots under the command of this Commander
	 */
	final public Integer[] getBotsInGroup(){
		return (Integer[]) acceptedIDs.toArray();
	}
	
	/**
	 * get a group for the commander
	 * @return
	 * 	true - reservation gotten
	 *  false - reservation hasn't been secured
	 */
	final private boolean secureGroup(){
		return comms.getGroupReservation();
	}
	
	/**
	 * acquires a robot for the group that this commander commands
	 * @param id
	 * 		The id of the robot you are requesting to join the group
	 * @return
	 * 		true - invite successfully sent.
	 * 		false - invite not sent.
	 */
	final private boolean acquireRobot(int id){
		//only allow one robot to be invited at a time
		boolean successfulInvite = comms.sendGroupInvite(id);
		if(successfulInvite){
			state = CommanderState.INVITING;
		}
		return successfulInvite;
	}
	
}
