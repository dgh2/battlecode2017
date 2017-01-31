package josiah_boid_garden.monteCarlo;

import battlecode.common.TreeInfo;
import josiah_boid_garden.Vector;

public class TreeAntiCloggingMetric implements AttractivenessMetric {

	
	TreeInfo[] trees;	// the known trees in the area
	Vector clumpingVector = new Vector();  // We use a vector to determine there are trees on both sides of the tested point
	int antiClumpingConstant = 100; // used in scaling the weight of this metric

	TreeAntiCloggingMetric(TreeInfo[] trees){
		this.trees = trees;
	}
	
	@Override
	public int appraiseAttractiveness(McPoint point) {
		
		for(int i = 0 ; i<trees.length ; i++){
			addClumpingVector(point,trees[i]);
		}
		
		int clumpingValue = appraiseClumping();
		return  clumpingValue ;
	}
	

	/**
	 * 
	 * @param point
	 * @return
	 */
	private void addClumpingVector(McPoint point , TreeInfo tree){
		Vector d = new Vector (point.location , tree.location);
		d.setMagnitude( (float)( antiClumpingConstant / (d.getMagnitude() + 1) ) );
		clumpingVector.add(d);
	}
	
	private int appraiseClumping(){
		return (int)clumpingVector.getMagnitude();
	}

}
