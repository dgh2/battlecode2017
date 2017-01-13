package gardener;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.TreeInfo;

public class Maintainer {
	RobotController rc;
	
	public Maintainer(RobotController rc){
		this.rc = rc;
	}
	
	public void maintain(){
		
		TreeInfo[] trees = rc.senseNearbyTrees(2f, rc.getTeam());
		
		if(trees.length>0){
			//find lowest tree
			TreeInfo lowest = null;
			for(TreeInfo checkTree : trees){
				
				if(lowest == null && rc.canWater(checkTree.getID())){
					lowest = checkTree;
				} else if(lowest.getHealth()>checkTree.getHealth() && rc.canWater(checkTree.getID())){
					lowest = checkTree;
				}
				
			}
			
			if(lowest!=null && rc.canWater(lowest.getID())){
				try {
					rc.water(lowest.getID());
				} catch (GameActionException e) {
					
				}
			}
		}
	}
	

}
