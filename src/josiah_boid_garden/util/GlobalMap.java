package josiah_boid_garden.util;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class GlobalMap {
	
	private static final int MapOffset = 1000;
	
	private boolean westAcquired, eastAcquired,
					northAcquired , southAcquired,
					widthAcquired, heightAcquired,
					offsetAcquired , dimensionsAcquired;
	private float xOffset,yOffset,width,height;
	RobotController rc;
	
	private static final int leftMostIndex = MapOffset + 1;
	private static final int rightMostIndex = MapOffset + 2;
	private static final int bottomIndex = MapOffset + 3;
	private static final int topIndex = MapOffset + 4;
	
	public GlobalMap(RobotController rc){
		widthAcquired=false;
		heightAcquired=false;
		offsetAcquired=false;
		westAcquired=false;
		eastAcquired = false;
		northAcquired = false;
		southAcquired = false;
		dimensionsAcquired = false;
		this.rc = rc;
		
	}
	
	/**
	 * check to see the dimensions of the map have been determined
	 */
	public boolean check(){
		
		//If you've already got the dimensions, there's really no need to check again.
		if(!dimensionsAcquired){
			float left = 0,right=0,top=0,bottom=0;
			
			if(!widthAcquired){
				
				//Check to see if the left and right most points of the map have been found
				try {
					left = rc.readBroadcastFloat(leftMostIndex);
					right = rc.readBroadcastFloat(rightMostIndex);
					
					if(left!=0){
						westAcquired = true;
						xOffset = left;
					}
					
					if(right!=0){
						eastAcquired = true;
					}
					
					if(westAcquired && eastAcquired){
						widthAcquired = true;
						width  = right - left;
					}
					
				} catch (GameActionException e) {
					System.err.println("There was a problem with GlobalMap.check()");
				}
			}
			
			//Check to see if the top and bottom have been found
			if(!heightAcquired){
				try {
					top = rc.readBroadcastFloat(topIndex);
					bottom = rc.readBroadcastFloat(bottomIndex);
					
					if(top!=0){
						northAcquired = true;
					}
					
					if(bottom!=0){
						southAcquired = true;
						yOffset = bottom;
					}
					
					if(northAcquired && southAcquired){
						heightAcquired = true;
						height = top - bottom;
					}
					
				} catch (GameActionException e) {
					System.err.println("There was a problem with GlobalMap.check()");
				}
			}
			
			//Check to see if all the map's dimensions have been discovered
			if(heightAcquired && widthAcquired ){
				dimensionsAcquired = true;
				offsetAcquired = true;
			}
		}
		
		return dimensionsAcquired;
	}
	
	/**
	 * set left most point of map
	 * @param width
	 */
	public void setWestBound(float left){
		if(! westAcquired){
			try {
				rc.broadcastFloat(leftMostIndex, left);
			} catch (GameActionException e) {
				e.printStackTrace();
			}	
		}
		
	}
	
	/**
	 * set right most point of map
	 * @param right
	 */
	public void setEastBound(float right){
		if(! eastAcquired){
			try {
				rc.broadcastFloat(rightMostIndex, right);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * set top of map
	 * @param top
	 */
	public void setNorthBound(float top){
		if( ! northAcquired ){
			try {
				rc.broadcastFloat(topIndex, top);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * set bottom of map.
	 * @param bottom
	 */
	public void setSouthBound(float bottom){
		if(! southAcquired){
			try {
				rc.broadcastFloat(bottomIndex, bottom);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}

	
	public boolean hasWestBound(){
		return westAcquired;
	}
	
	public boolean hasEastBound(){
		return eastAcquired;
	}
	
	public boolean hasNorthBound(){
		return northAcquired;
	}
	
	public boolean hasSouthBound(){
		return southAcquired;
	}
}
