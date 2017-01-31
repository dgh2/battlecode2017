package josiah_boid_garden.gardening;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RandomRobotBuilder {
	
	RobotController rc;
	
	Random rnd = new Random();
	RobotType[] rouletteWheel;
	int totalChance;
	
	public RandomRobotBuilder(RobotController rc){
		this.rc = rc;
		populateRouletteWheel();
	}
	
	private void populateRouletteWheel(){
		totalChance = getTankChance() + getSoldierChance() +getLumberJackChance();
		rouletteWheel = new RobotType[totalChance];
		int index = 0;
		for(int i = 0 ; i< getTankChance() ; i++){
			rouletteWheel[index] = RobotType.TANK;
			index++;
		}
		for(int i = 0 ; i< getSoldierChance() ; i++){
			rouletteWheel[index] = RobotType.SOLDIER;
			index++;
		}
		for(int i = 0 ; i< getLumberJackChance() ; i++){
			rouletteWheel[index] = RobotType.LUMBERJACK;
			index++;
		}
	}
	
	int getTankChance(){
		return 15;
	}
	
	int getSoldierChance(){
		return 5;
	}
	
	int getLumberJackChance(){
		return 5;
	}
	
	public void buildRandomRobot(Direction buidDirection){
		if(rc.canBuildRobot( rouletteWheel[ rnd.nextInt(totalChance) ] , buidDirection )){
			try {
				rc.buildRobot( rouletteWheel[ rnd.nextInt(totalChance) ] , buidDirection );
			} catch (GameActionException e) {
				
			}
		}
	}
	
	
	
}
