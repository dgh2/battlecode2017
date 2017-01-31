package josiah_boid_garden.boid;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.TreeInfo;
import josiah_boid_garden.UnitVector;

public class WaterATreeInNeed {
	
	
	Boid controller;
	
	static private final int waterScaling = 30;
	
	public WaterATreeInNeed(Boid base){
		this.controller = base;
	}
	
	public void run( TreeInfo[] trees){
		
		boolean canWater = controller.rc.canWater();
		
		TreeInfo neediestTreeInRange = null;
		
		for(int i = 0 ; i < trees.length ; i ++){
			
			if(trees[i].getTeam() == controller.rc.getTeam()){
				//Find the neediest tree that you can water
				if(canWater){
					if( controller.rc.canInteractWithTree(trees[i].getID()) ){
						if( neediestTreeInRange == null || trees[i].health < neediestTreeInRange.health){
							neediestTreeInRange = trees[i];
						}
					}
				}	
				//apply forces to all the hungry trees out there
				respond(controller , trees[i] );
			
			}
			//Try to water the neediest tree.
			
		}
		
		if(neediestTreeInRange!=null){
			try {
				controller.rc.water(neediestTreeInRange.getID());
			} catch (GameActionException e) {
				System.out.println("Oh no, I can't water this. F@%$!");
				e.printStackTrace();
			}
		
		}

	}
	
	private void respond ( Boid controller , TreeInfo tree){

		UnitVector dir = new UnitVector(controller.rc.getLocation(),tree.location);
		
		int magnitude = (int) (tree.getHealth() / GameConstants.BULLET_TREE_MAX_HEALTH) * waterScaling;
		
		controller.addForce(dir, magnitude);
		
	}
	
	
	

}
