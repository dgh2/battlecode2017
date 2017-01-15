package comm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Stack;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Commander {
	
	Communicator comms;
	
	RobotController rc;
	
	private static final int maxCommandSize = 32;
	
	enum CommanderState { SECUREGROUP , GETINVITES , INVITING} 
	CommanderState state = CommanderState.SECUREGROUP;
	
	List<Integer> acceptedIDs = new ArrayList<Integer>();
	Deque<Integer> invitedBots = new ArrayDeque<Integer>();
	int invitedRobotID = 0;
	int maxStrikes = 5;
	int strikes = 0;
	
	int lastTurnACommandWasSent = 0;
	
	Commander(RobotController rc){
		this.rc = rc;
		comms = new Communicator(rc);
		
	}
	
	/**
	 * a very important function. It must be run every turn if you want the commander to be able to 
	 * secure it's group and send invites and recieve confirmations from other robots
	 */
	public void run(){
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
		
		
		
	}


	
	/**
	 * 
	 * @param id
	 */
	public void sendInvite(int id){
		invitedBots.addLast(new Integer(id));
	}
	
	/**
	 * send a command to the whole group
	 * @param command
	 * @param data
	 */
	public void sendGroupCommand(int command , int[] data){
		comms.sendCommand(data,command, true , 0);
	}
	
	/**
	 * send a command to an individual
	 * @param command
	 * @param data
	 * @param targetID
	 */
	public void sendIndividualCommand(int command , int[] data, int targetID){
		comms.sendCommand(data,command, false , targetID);
	}
	
	/**
	 * returns all the bots in this group
	 * @return
	 * 		All the bots under the command of this Commander
	 */
	public Integer[] getBotsInGroup(){
		return (Integer[]) acceptedIDs.toArray();
	}
	
	
	/**
	 * get a group for the commander
	 * @return
	 * 	true - reservation gotten
	 *  false - reservation hasn't been secured
	 */
	private boolean secureGroup(){
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
	private boolean acquireRobot(int id){
		//only allow one robot to be invited at a time
		boolean successfulInvite = comms.sendGroupInvite(id);
		if(successfulInvite){
			invitedRobotID = id;
			state = CommanderState.INVITING;
		}
		return successfulInvite;
	}
	
}
