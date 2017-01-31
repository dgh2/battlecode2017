package boidrolesv2.gardening;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

import java.util.Random;

public class Formation {
	
	RobotController rc;
	Random rand = new Random();
	
	public enum Form {CIRCLE , HALFCIRCLE , TRIANGLE , C}
	protected Direction[] complimentaryAngles;
	protected Direction[] formation;
	protected boolean[] planted;
	Direction plantDirection;
	
	public Formation(RobotController rc , Direction plantDirection, Form form){
		this.rc = rc;
		this.plantDirection = plantDirection;
		switch(form){
		case CIRCLE:
			setCircleFormation();
			break;
		case HALFCIRCLE:
			setHalfCircleFormation();
			break;
			
		case TRIANGLE:
			setTriangleFormation();
			break;
		case C:
			setCFormation();
			break;
		}
	}
	
	/**
	 * Plant a tree in the formation
	 * @return true - done planting
	 * 			false - still planting
	 */
	public boolean plant(){
		try{
			int unplanted = findUnplantedTree();
			
			if(rc.canPlantTree(formation[unplanted])){
				try {
					rc.plantTree(formation[unplanted]);
					planted[unplanted] = true;
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return false;
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			return true;
		}
		
	}

	public boolean hasPlanted() {
		if(formation.length > 1) { //if there are no trees that have been planted
			return true;
		}
		return false;
	}

	public Direction getBuildDirection(){
    	return this.plantDirection.opposite();
    }

    public Direction[] getComplimentaryAngles(){
    	return this.complimentaryAngles;
    }

	private int findUnplantedTree() throws Exception{
		int[] indexes = new int[6]; //there can only be 6 positions max around a gardener
		
		//get all the unplanted locations
		int numberOfUnplantedTrees = 0;
		for(int i =0; i<formation.length ; i++){
			if(!planted[i]){
				indexes[numberOfUnplantedTrees] = i;
				numberOfUnplantedTrees++;
			}
		}
		
		if(numberOfUnplantedTrees == 0){
			throw new Exception("no unplanted trees.");
		}
		
		//pick a random one
		return indexes[ rand.nextInt(numberOfUnplantedTrees) ];
		
	}
	
	public void print(){
		for ( Direction dir : formation){
			System.out.println("Formation direction:" + dir);
		}
	}
	
	private void setCircleFormation(){
		
		formation = new Direction[6];
		planted = new boolean[]{false,false,false,false,false,false};
		complimentaryAngles = new Direction[]{};
		
		Direction start = new Direction(plantDirection.radians);
		
		for(int i = 0 ; i < 6 ; i++){
			formation[i] = start;
			start = start.rotateLeftDegrees(60);//number of degrees between trees
			
		}
		
	}

	private void setHalfCircleFormation(){
		
		formation = new Direction[3];
		planted = new boolean[]{false,false,false};
		complimentaryAngles = new Direction[]{new Direction(plantDirection.radians).opposite()};
		
		Direction start = new Direction(plantDirection.radians).rotateLeftDegrees(60);
		
		
		for(int i = 0 ; i < 3 ; i++){
			formation[i] = start;
			start = start.rotateRightDegrees(60);//number of degrees between trees
			
		}
		
	}
	
	private void setTriangleFormation(){
		
		formation = new Direction[3];
		planted = new boolean[]{false,false,false};
		complimentaryAngles = new Direction[3];
		
		Direction start = new Direction(plantDirection.radians);
		
		
		for(int i = 0 ; i < 3 ; i++){
			complimentaryAngles[i] = start.rotateRightDegrees(60);
			formation[i] = start;
			start = start.rotateRightDegrees(120);//number of degrees between trees
			
		}

	}

	private void setCFormation(){
		
		formation = new Direction[4];
		planted = new boolean[]{false,false,false,false};
		complimentaryAngles = new Direction[]{plantDirection.opposite()};
		
		Direction start = new Direction(plantDirection.radians).rotateRightDegrees(90);
		
		for(int i = 0 ; i < 4 ; i++){
			formation[i] = start;
			start = start.rotateLeftDegrees(60);//number of degrees between trees
			
		}
		
	}
	
}
