package boidrolesv2.gardening;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.TreeInfo;

public class Maintainer {
	
	RobotController rc;
	
	public Maintainer(RobotController rc){
		this.rc = rc;
	}
	
	public void maintain(){
		TreeInfo[] nearbyTrees = rc.senseNearbyTrees(2f, rc.getTeam());
	
		//if there are trees neaby
		if(nearbyTrees.length > 0){
			
			//find the one with the lowest hp...
			TreeInfo lowest = nearbyTrees[0];
			
			for( TreeInfo tree : nearbyTrees ){
				
				if(rc.canWater(tree.getID()) && tree.getHealth() < lowest.getHealth()){
					lowest = tree;
				}
				
			}
			
			//...and water it 
			try {
				rc.water(lowest.getID());
			} catch (GameActionException e) {
				//something bad happened, but there's not much we can do about it.
			}
		}
	}
	
}
