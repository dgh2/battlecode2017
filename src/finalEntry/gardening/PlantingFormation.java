package finalEntry.gardening;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

import java.util.Random;

public class PlantingFormation {
	
	public enum Formation{CIRCLE,HALFCIRCLE,TRIANGLE}
	
	RobotController rc; //the robot making the tree formation
	private Direction baseDirection; //the chosen direction that the formation is based off of
	Formation plantingFormation; //the chosen formation
	
	private Direction[] formation; //an array of directions that form 
	private boolean[] plantedMatrix; //the array of values indicating whether their corresponding 
	
	
	private Random rand = new Random();
	/**
	 * 
	 * @param rc 
	 * 			The robot making the formation
	 * @param baseDirection
	 * 			The direction used to determine where around the bot the formation is placed
	 * @param plantingFormation
	 * 			Which formation the plants are put into
	 */
	public PlantingFormation(RobotController rc , Direction baseDirection , Formation plantingFormation){
		this.rc = rc;
		this.baseDirection = baseDirection;
		this.plantingFormation=plantingFormation;
	}
	
	
	/**
	 * plants trees in a given formation.
	 * @return true when planting is complete. If a formation isn't set, circle formation is chosen
	 * 			false while planting is in progress.
	 */
	public boolean plant(){
		//set formation to circle by default
		if(formation == null){
			setCircleFormation();
		}
		
		//check if you're done
		boolean done = true;
		//make a list of all the unplanted plots
		int[] unplantedPlots = new int[6];
		int unplantedCount = 0;
		
		for(int i = 0 ; i<plantedMatrix.length ; i++){
			
			if(! (plantedMatrix[i]) ){
				done = false;
				unplantedPlots[unplantedCount] = i;
				unplantedCount ++;
			}
		}
		
		//if you're done, don't try to plant stuff
		if(done){
			return true;
		}
		//randomly pick an unplanted plot and try to plant it.
		int targetTree = rand.nextInt(unplantedCount);
		
		if( rc.canPlantTree(formation[targetTree]) ){
			try {
				rc.plantTree(formation[targetTree]);
				plantedMatrix[targetTree] = true;
			} catch (GameActionException e) {
				
			}
		}
		
		return false;
	}
	
	/**
	 * checks if the given formation can be planted.
	 * @return true - the whole formation can be planted.
	 */
	public boolean canPlantFormation(){
		
		if(formation == null){
			return false;
		}
		
		boolean canPlant = true;
		
		for(Direction dir : formation){
			if(! this.rc.canPlantTree(dir) ){
				canPlant = false;
			}
		}
		
		return canPlant;
	}
	
	/**
	 * returns the number of trees that can be planted in this formation.
	 * @return number of trees that can be planted in this formation
	 */
	public int getTreesPlantableInFormation(){
		
		if(formation == null){
			System.out.println("I can't plant trees because there's no formation.");
			return 0;
		}
		
		int plantable = 0;
		
		for(Direction dir : formation){
			if(this.rc.canPlantTree(dir) ){
				plantable++;
			}
		}
		
		System.out.println("I can plant "+plantable+" trees.");
		return plantable;
		
	}
	
	public void setCircleFormation(){
		formation = new Direction[6];
		plantedMatrix = new boolean[6];
		
		Direction ref = new Direction(baseDirection.radians);
		
		for(int i = 0 ; i<6 ; i++){
			formation[i] = new Direction(ref.radians);
			ref = ref.rotateRightDegrees( 60 );
			plantedMatrix[i] = false;
			System.out.println("Circle formation ["+i+"]:" + formation[i]);
		}
		
	}
	
	public void setHalfCircleFormation(){
		formation = new Direction[3];
		plantedMatrix = new boolean[3];
		
		Direction ref = new Direction(baseDirection.radians);
		
		ref.rotateLeftDegrees(60);
		
		for(int i = 0 ; i<3 ; i++){
			formation[i] = new Direction(ref.radians);
			ref = ref.rotateRightDegrees( 60 );
			plantedMatrix[i] = false;
		}
		
	}
	
	public void setTriangleFormation(){
		formation = new Direction[3];
		plantedMatrix = new boolean[3];
		
		Direction ref = new Direction(baseDirection.radians);
		
		for(int i = 0 ; i<3 ; i++){
			formation[i] = new Direction(ref.radians);
			ref = ref.rotateRightDegrees( 120 );
			plantedMatrix[i] = false;
		}
		
	}
}
