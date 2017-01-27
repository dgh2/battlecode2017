package josiah_boid_garden.monteCarlo;

import java.util.ArrayDeque;
import java.util.Queue;

import battlecode.common.MapLocation;

public class MonteCarlo {
	
	private final static int MaxTestPoints = 32;
	
	private Queue<McPoint> testPoints = new ArrayDeque<McPoint>(); //array of test points

	static final int MAX_MODIFIERS = 16;
	AttractivenessMetric[] metrics = new AttractivenessMetric[MAX_MODIFIERS];
	int metricCount = 0;
	
	public MonteCarlo(){}
	
	public void addPoint(MapLocation point){
		testPoints.add(new McPoint(point));
		if(testPoints.size() > MaxTestPoints){
			testPoints.remove();
		}

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
		if(testPoints.isEmpty()){
			return null;
		}
		//There are no metrics
		if(metricCount == 0){
			return testPoints.peek();
		}
		//normal case
		McPoint bestLocation = testPoints.peek();
		
		for(McPoint point : testPoints){
			//zeroize each point
			point.value = 0;
			//appraise attractiveness of each point
			for(int j = 0 ; j<metricCount ; j++){
				int appraisal =  metrics[j].appraiseAttractiveness(point);
				point.addValue(appraisal);	
			}
			//get the best point
			if(point.getValue() > bestLocation.getValue()){
				bestLocation = point;
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
	
	public void clearModifiers(){
		metricCount=0;
	}
	
	
	

}
