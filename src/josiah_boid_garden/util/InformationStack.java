package josiah_boid_garden.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class InformationStack {
	
	
	int stackOffset = 500;
	
	int dataStart = 501;
	
	int packageSize = 3;
	
	int maxStackSize = 32;
	
	RobotController rc;
	
	
	
	public InformationStack(RobotController rc){
		this(rc,500,100);
	}
	
	public InformationStack(RobotController rc , RobotType listeningType){
		int so = 50;
		switch(listeningType){
		case TANK:
			so=1050;
			break;
		case SOLDIER:
			so=1150;
			break;
		case ARCHON:
			so=1250;
			break;
		case LUMBERJACK:
			so=1350;
			break;
		case SCOUT:
			so=1450;
			break;
		case GARDENER:
			so=1550;
			break;
		default:
		}
		this.rc=rc;
		init(so,32);

	}
	
	InformationStack(RobotController rc , int stackOffset ){
		this(rc,stackOffset,32);
	}
	
	InformationStack(RobotController rc , int stackOffset, int maxStackSize ){
		this.rc = rc;
		init(stackOffset,maxStackSize);
	}
	
	private void init(int stackOffset , int maxStackSize){
		this.stackOffset = stackOffset;
		this.maxStackSize = maxStackSize;
		dataStart = stackOffset+1;
	}
	
	public boolean writeToStack(MapLocation location , float heuristic){
		return writeToStack(new MapLocation[]{location},heuristic);
	}
	
	public boolean writeToStack(MapLocation[] location , float heuristic){
		boolean success = false;
		if(location==null){
			return success;
		}
		
		try{
			int currentStackSize = rc.readBroadcastInt(stackOffset);
			int j = 0;
			for(int i = 0 ; i<maxStackSize; i++){
				int index = dataStart + i * packageSize;
				float stackItem = rc.readBroadcastFloat(index);
				System.out.println( "Reading "+stackItem+"at "+index );
				if(stackItem == 0.0f){
					System.out.println("adding to stack at "+currentStackSize);
					rc.broadcastInt(stackOffset, currentStackSize++);
					rc.broadcastFloat(dataStart + i*packageSize, heuristic);
					rc.broadcastFloat(dataStart + i*packageSize+1, location[j].x);
					rc.broadcastFloat(dataStart + i*packageSize+2, location[j].y);
					success= true;
					//after whole array is written, you're done
					j++;
					if(j==location.length){
						break;
					}
				}
			}
			
			
			System.out.println("Stack after write");
			for(int i = 0;i<15;i++){
				System.out.println("["+i+"]="+rc.readBroadcastInt(stackOffset+i));
			}	
		} catch (Exception e){
			
		}

		return success;
		
	}
	
	public MapLocation readFromStack(){
		
		MapLocation max = null;
		
		try{
			
			int stackSize = this.rc.readBroadcastInt(stackOffset);
			System.out.println("Stack size ="+stackSize);
			
			int maxIndex = -1;
			float maxValue = 0;
			
			for(int i = 0 ; i<maxStackSize;i++){
				int index = dataStart + i * packageSize;
				float stackItem = rc.readBroadcastFloat(index);
				
				if(stackItem > maxValue){
					
					maxIndex = index;
					maxValue = stackItem;
					
				}
				
				
			}
			
			if(maxIndex!=-1){
				max = new MapLocation(rc.readBroadcastFloat(maxIndex+1) , rc.readBroadcastFloat(maxIndex+2));
				removeIndex(maxIndex);
			}
			
			
		} catch (Exception e){
			
		}
		
		return max;
	}

	

	void removeIndex(int channel) throws GameActionException{
		//you only need to zeroize the indexer. The others can be whatever.
		rc.broadcastFloat(channel, 0);
		
	}
	

}
