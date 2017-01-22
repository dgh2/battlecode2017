package josiah_boid_garden.monteCarlo;

import battlecode.common.TreeInfo;

public class NextToTreeMetric implements AttractivenessMetric {

	TreeInfo[] trees;
	
	NextToTreeMetric(TreeInfo[] trees){
		this.trees = trees;
	}
	
	
	
	@Override
	
	
	/**
	 * So there are a few goals here that need to be established:
	 * 
	 * 1. We want to be close to neutral trees or the edge of the map.
	 * 2. We want to NOT close in trees 
	 * 
	 * 
	 * 
	 */
	public int appraiseAttractiveness(McPoint point) {
		
		
		for(int i = 0 ; i<trees.length ; i++){
			float distance = point.location.distanceTo(trees[0].location);
		}
		
		
		return 0;
	}

}
