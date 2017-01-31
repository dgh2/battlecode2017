package josiah_boid_garden.monteCarlo;

import battlecode.common.TreeInfo;
import josiah_boid_garden.Vector;

public class NextToTreeMetric implements AttractivenessMetric {

	int distanceConstant = 10;
	
	TreeInfo[] trees;
	
	NextToTreeMetric(TreeInfo[] trees){
		this.trees = trees;
	}
	
	
	
	@Override
	
	
	/**
	 * So there are a few goals here that need to be established:
	 * 1. Clump to neutral trees.
	 * 2. Away from I traffic areas.
	 * 3. Want ally trees to be in a line.
	 * 1. We want to be close to neutral trees or the edge of the map.
	 * 			- We add value for being close to trees
	 * 2. We want to NOT close in trees 
	 * 			- We want trees kind of in a line
	 * 				- We use the closest tree as a reference.
	 * 				- Draw a line to another tree
	 * 				- Apply a value depending on distance trees are from each other and distance the checked point is from the line.
	 * 
	 * 3. We want to be away from high traffic areas
	 * 		- We apply a boid-style addition algorithm to add the distance of nearby trees
	 * 		- We then use the magnitude of the resulting vector as a result
	 * 
	 */
	public int appraiseAttractiveness(McPoint point) {
		
		int distanceValue=0;
		for(int i = 0 ; i<trees.length ; i++){
			distanceValue += (int) (distanceConstant/point.location.distanceSquaredTo(trees[0].location));
		}
		
		int linearity = appraiseLinearity();
		return distanceValue + linearity;
	}
	
	private int appraiseLinearity(){
		return 1;
	}
	
}
