package josiah_boid_garden.boid;

import battlecode.common.TreeInfo;
import josiah_boid_garden.UnitVector;

public class MoveAwayFromTreeBehavior {
	
	
	
	Boid controller;
	
	float antiTreeValue;
	
	public MoveAwayFromTreeBehavior(Boid controller , float antiTreeValue){
		this.controller = controller;
		this.antiTreeValue = antiTreeValue;
	}
	
	public void run(TreeInfo[] trees){
		for(int i = 0; i<trees.length;i++){
			if(trees[i].getTeam()==controller.getRobotController().getTeam()){
				
				controller.addForce(new UnitVector(trees[i].location,controller.getRobotController().getLocation()),
						(int) (antiTreeValue/(controller.getRobotController().getLocation().distanceSquaredTo(trees[i].location) + 1)));
				
			}
		}
	}
	
	
}
