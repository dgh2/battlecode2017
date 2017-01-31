package josiah_boid_garden.boid;

import battlecode.common.TreeInfo;
import josiah_boid_garden.UnitVector;

public class MoveAwayFromTreeBehavior {
	
	
	
	Boid controller;
	
	int antiTreeValue;
	
	 boolean counterClockWise;
	
	public MoveAwayFromTreeBehavior(Boid controller , int antiTreeValue , boolean counterClockWise){
		this.controller = controller;
		this.antiTreeValue = antiTreeValue;
		this.counterClockWise = counterClockWise;
	}
	
	public void runAway(TreeInfo[] trees){
		for(int i = 0; i<trees.length;i++){
				controller.addSquaredRepulsion(trees[i].location, antiTreeValue);
				controller.applyConstantRotationalForce(trees[i].location, counterClockWise, antiTreeValue);
		}
	}
	
	public void runToward(TreeInfo[] trees){
		for(int i = 0; i<trees.length;i++){
				controller.addSquaredAttraction(trees[i].location, antiTreeValue);
				controller.applyConstantRotationalForce(trees[i].location, counterClockWise, antiTreeValue);
		}
	}
	
	
}
