package finalStretch.gardening;

import battlecode.common.TreeInfo;

public class ForestationChecker {
		
	public ForestationChecker( ){

	}
	
	public float getForestationAtLocation(TreeInfo[] trees , float senseRadius){
		float forestArea = 0;
		float senseArea = (float) (Math.PI*Math.pow(senseRadius, 2));
		
		for(TreeInfo tree : trees){
			forestArea += (float) (Math.PI*Math.pow(tree.radius, 2));
		}

		return senseArea / forestArea ;
	}

}
