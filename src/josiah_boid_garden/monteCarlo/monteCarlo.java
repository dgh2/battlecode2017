package josiah_boid_garden.monteCarlo;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class monteCarlo {
	
	McPoint[] testPoints = new McPoint[32]; //array of test points
	int tpCount = 0; // how many test points there are.
	RobotController rc;
	static final int MAX_MODIFIERS = 16;
	AttractivenessMetric[] metrics = new AttractivenessMetric[MAX_MODIFIERS];
	int metricCount = 0;
	
	monteCarlo(RobotController rc){
		this.rc = rc;
	}
	
	public boolean addPoint(MapLocation point){
		if(tpCount >= 32 || !rc.canMove(point)){
			return false;
		}
		testPoints[tpCount] = new McPoint(point);
		tpCount++;
		return true;
	}
	
	/**
	 * add a modifier to this 
	 * @param mod
	 * @return
	 */
	public boolean addModifier(AttractivenessMetric mod){
		if(metricCount>=MAX_MODIFIERS){
			return false;
		}else{
			metrics[metricCount] = mod;
			metricCount++;
			return true;
		}
	}
	/**
	 * Returns the best map location given the set of attractiveness metrics
	 * @return
	 * 		Best map location or null if no locations were given.
	 * 		If no metrics are provided, it returns the first provided test point.
	 */
	public McPoint appraise(){
		//There are no test points
		if(tpCount == 0){
			return null;
		}
		//There are no metrics
		if(metricCount == 0){
			return testPoints[0];
		}
		//normal case
		McPoint bestLocation = testPoints[0];
		
		for(int i = 0;i<tpCount;i++){
			for(int j = 0 ; j<metricCount ; j++){
				int appraisal =  metrics[j].appraiseAttractiveness(testPoints[i]);
				testPoints[i].addValue(appraisal);	
			}
			
			if(testPoints[i].getValue() > bestLocation.getValue()){
				bestLocation = testPoints[i];
			}
		}
		
		return bestLocation;
	}
	
	/**
	 * Returns the best map location given the set of attractiveness metrics
	 * @return
	 * 		Best map location or null if no locations were given.
	 * 		If no metrics are provided, it returns the first provided test point.
	 */
	public MapLocation appraiseForMapLocation(){
		McPoint best = appraise();
		if(best!=null){
			return best.location;
		}else{
			return null;
		}
	}
	
	
	
	
	

}
