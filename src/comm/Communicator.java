package comm;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

/**
 * This communicator sends messages over the wire to communicate messages for the team
 * Follows the following communication standard.
 * The first 16 channels are reserved
 * 0 - [groupReservation]used for reserving groups for commanders
 * 1 - [joinRequestID]used for commanders to request units to join their group
 * 2 - [joinRequestGroup]used to designate which group those units are supposed to join
 * 3
 * 4
 * 5
 * 6
 * 7
 * 8
 * 9
 * 10
 * 11
 * 12
 * 13
 * 14
 * 15
 * 
 * Each group gets 16 channels, each channel has a purpose
 * 
 *  0- [commandScope] designates whether the command is group wide or for an individual
 *  	0 - group
 *  	1 - individual
 *  1- [target] the indivual bot's ID who is to recieve the command. Ignored if commandScope is group level
 *  2- [commandType]the command type that is being issued to the unit
 *  3- [data 0]
	... - [data ...]
 *  10- [data 7]
 *  11- [commandConfirmation]used to confirm that the last command has been recieved
 *  12- []
 *  ...
 *  ...
 *  ...
 *  [packageSize*-1] - [groupConfirmation]reserved for troops to accept offers from commanders 
 * 
 * @author user
 *
 */
class Communicator {
	
	//global  
	static final int groupReservation = 0; //Used to resolve which commanders get which groups
	static final int joinRequestID = 1; // Bot Invitation Field : ID of bot being invited
	static final int joinRequestGroup = 2;	 // Bot Invitation Field : ID of the group inviting the bot

	
	
	//group level
	
	static final int commandScope ; // Whether the command is targeting the group or an individual robot 0 - group, anything else is the ID of the bot being targeted
	static final int commandType ; // The type of command being sent
	static final int dataOffset; // The index that the data accompanying a command starts
	static final int dataSize = 8; // The max number of data slots that can accompany a command
	static final int commandConfirmation; // This slot is reserved for a target robot to put that the designated command was recieved
	
	static final int reportingID ; //The ID of the bot making the report back to the commander
	static final int reportDataOffset; //The start of where the data of the report goes
	static final int reportDataSize = 8 ; //The max number of blocks that the data can occupy
	
	static final int groupConfirmation; // This slot is reserved for bots to let the commander know that they've accepted the requrest for group transfer

	//misc constants
	static final int maxGroupIndex;
	static final int maxNumberOfGroups; // The maximum number of groups that could possibly exist.
	private static int packageSize = 32; // The number of channels that belongs to this group		

	static{
		commandScope = 0;
		
		commandType = commandScope + 1;
		
		dataOffset = commandType + 1; // comes after commandType
		
		commandConfirmation = (dataOffset+dataSize); // comes after data
		
		reportingID = commandConfirmation+1; // comes after command confirmation
		
		reportDataOffset = reportingID + 1; //
		
		groupConfirmation = packageSize - 1;	
		
		maxNumberOfGroups = (int) Math.floor( 1000/packageSize ) - 1;//you have to take one off for the global group
	
		maxGroupIndex = maxNumberOfGroups; 
	}
	
	
	int groupNumber = 0; // The group that this communicator will be 
	
	int offset = 0; // The channel offset that belongs to this group
	
	private int lastInviteIDSent; // The ID of the robot that was sent the last group invite 
	
	RobotController rc;
	
	Communicator(RobotController rc){
		this.rc = rc;
	}
	
	/**
	 * send a data command onto the channel
	 * @param data
	 * 			the data to send
	 * @param command
	 * 			the command id
	 * @param includeGroup
	 * 			true - include whole group
	 * 			false - include only a single target
	 * @param targetID
	 * 			if includeGroup == false, this is the target ID of the bot that will read the command.
	 * @return
	 * 			true  - command successfully sent
	 * 			false - something went horribly wrong
	 */
	boolean sendCommand(int[] data,int command,boolean includeGroup,int targetID){
		//System.out.println("Sending command scope");
		//write who is targeted, group or individual
		if(includeGroup){
			if( !writeToChannel( groupReservation , 0 )) return false;
		} else {
			if( ! writeToChannel( groupReservation , targetID )) return false;
		}		
		//System.out.println("Sending command");
		//write what type of command is being issued
		if( !writeToChannel( commandType , command ) ) return false;
		//System.out.println("resetting command recieved");
		//send a confirmation that the command was recieved
		if( !writeToChannel( commandConfirmation , 0 )) return false;
		//System.out.println("Sending command data");
		//write the data to the channel
		for(int i = 0 ; i<dataSize && i<data.length ;i++){
			if( !writeToChannel( dataOffset + i , data[i] ) ) return false;
		}
		
		return true;
	}
	
	/**
	 * Get the command from the target channel.
	 * @return
	 * 	0 - no command given
	 *  not 0 - the integer command number
	 */
	public int getCommand(){
		
		if(this.groupNumber == 0){
			return 0;
		}
		//System.out.println("reading command");
		int scope = readFromChannel(commandScope);
		int command = readFromChannel(commandType);
		
		//System.out.println("scope = "+scope);
		//System.out.println("command= " + command);
		
		switch (scope){
		case 0:
			return command;
		default:
			if(scope == rc.getID()){
				return command;
			} else {
				return 0;
			}
		}
		
	}
	
	/**
	 * get data embedded in a command.
	 * @return
	 * 		The data stored in this command. returns empty array if there is no data or the command does not apply to this 
	 * bot.
	 */
	public int[] getCommandData(){
		if(this.groupNumber == 0){
			return new int[]{};
		}
		//System.out.println("reading command");
		int scope = readFromChannel(commandScope);
		int[] data = new int[dataSize];
		
		for(int i = 0; i<dataSize;i++){
			data[i] = readFromChannel(dataOffset+i);
		}
		
		//System.out.println("scope = "+scope);
		//System.out.println("command= " + command);
		
		switch (scope){
		case 0:
			return data;
		default:
			if(scope == rc.getID()){
				return data;
			} else {
				return new int[]{};
			}
		}
	}
	
	/**
	 * Sets the groupd that this commander commands.
	 * @param groupNumber
	 */
	protected void setGroup(int groupNumber){
		this.groupNumber = groupNumber;
		offset = groupNumber * packageSize; 
	}
	
	/**
	 * Gets the group number that this communicator is assigned to. 0 means no group.
	 * @return
	 */
	int getGroup(){
		return groupNumber;
	}

	/**
	 * gets a reservation to acquire a group for a commander.
	 * @return
	 *  0 - failure to secure a group
	 *  >0 - the group secured
	 */ 
	boolean getGroupReservation(){
		int group;
		try {
			group = rc.readBroadcast(groupReservation);
			group++;
			rc.broadcast(groupReservation, group);
			setGroup(group);
			return true;
		} catch (GameActionException e) {
			return false;
		}
	}
	
	/**
	 * sends an invitation to the designated robot to join the group.
	 * @return
	 */
	boolean sendGroupInvite(int robotID){
		try {
			//send the invite
			rc.broadcast(joinRequestID, robotID);
			rc.broadcast(joinRequestGroup, getGroup());
			
			//System.out.println("Sending " + robotID + " to channel " + (joinRequestID));
			//System.out.println("Sending " + getGroup() + " to channel " + (joinRequestGroup));
			//clear the registry for group confirmation
			writeToChannel(groupConfirmation , 0);
			//
			lastInviteIDSent = robotID;
		} catch (GameActionException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the ID of the robot that has accepted a request ID for this group.
	 * @param robotID
	 * 			The ID of the robot that should be accepting the group invite.
	 * 
	 * @return
	 * 			0 - no robots have accepted this group invite
	 * 			ID - the invite was not accepted by any robots.
	 */
	int getAcceptedInviteBotID(){
		int accepted = readFromChannel(groupConfirmation);
		respondToHijackedInvite( accepted );
		return accepted;
	}
	
	/**
	 * If another commander has sent an invite before the bot could respond, you need to send another invite.
	 * If for some REALLY dumb reason the accepted ID is not the same as the ID of the invite you just sent, you 
	 * need to send another invite.
	 * @param acceptedID
	 * 			The ID that was found in the groupConfirmation.
	 */
	private void respondToHijackedInvite( int acceptedID){
		if( acceptedID != 0 && acceptedID != lastInviteIDSent ){
			try {
				//if the current request doesn't match the last, resend it
				// or if the acceptedID is different from the send one, something went wrong
				if( rc.readBroadcast( joinRequestID ) != lastInviteIDSent || rc.readBroadcast( joinRequestGroup ) != getGroup() || acceptedID != lastInviteIDSent){
					sendGroupInvite(lastInviteIDSent);
				}
			} catch (GameActionException e) {
				//there's really not a whole lot you can do except wait until the next cycle to try doing the same thing again.
			}
			
		}
	}
	
	/**
	 * automatically accept any group invites coming from a commander group
	 * @param rc
	 * @return
	 */
	boolean acceptGroupInvite(){
		
		int requestID,requestGroup;
		try {
			//check for unit requests
			requestID = rc.readBroadcast(joinRequestID);
			requestGroup = rc.readBroadcast(joinRequestGroup);		
			
			//System.out.println("Recieved " + requestID + " to channel " + (joinRequestID));
			//System.out.println("Recieved " + requestGroup + " to channel " + (joinRequestGroup));
			//if the request is for the ID that matches this bot, set the group and accept it.
			if(requestID == rc.getID()){
				//set the group
				setGroup(requestGroup);
				//accept the request
				boolean acceptanceSuccess = broadcastGroupAccept();
				return acceptanceSuccess;
			}
		} catch (GameActionException e) {
			System.out.println("A problem was had accepting group invite");
			return false;
		}
		
		return false;
		
	}
	
	/**
	 * sends the acceptance signal to the proper groups channel.
	 * @param rc
	 * 		the robot accepting the group request
	 * @return
	 * 		true - success
	 * 		false - failure
	 */
	private boolean broadcastGroupAccept(){
		return writeToChannel( groupConfirmation , rc.getID() );
	}
	
	/**
	 * sends the information to the proper channel given the group.
	 * @param channel
	 * @param information
	 */
	private boolean writeToChannel(int packageIndex, int information){
		try {
			rc.broadcast(offset + packageIndex, information);
			//System.out.println("Sending " + information + " to channel " + (offset+packageIndex));
			return true;
		} catch (GameActionException e) {
			System.out.println("error sending command on channel " + (offset+packageIndex) + " for group "+groupNumber);
			return false;
		}
	}
	
	/**
	 * sends the information to the proper channel given the group.
	 * @param channel
	 * @param information
	 */
	private int readFromChannel(int packageIndex){
		try {
			int value = rc.readBroadcast(offset + packageIndex);
			//System.out.println("Received " + value + " on channel " + (offset+packageIndex));
			return value;
		} catch (GameActionException e) {
			System.out.println("error sending command on channel " + (offset+packageIndex) + " for group "+groupNumber);
		}
		return 0;
	}
	
}
