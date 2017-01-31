package josiah_boid_garden.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class GlobalMap {
	
	private static final int MapOffset = 1000;
	
	private static final float gridSize = 3.0f;
	private static int xDivisions,yDivisions;
	
	private static final int headerOffset = 2000;
	
	
	private static final int dataPackageSize = 5;
	private static final int forestationDataOffset = 0;
	private static final int dangerOffset = 1;
	private static final int pheromoneOffset = 2;
	private static final int timeStampOffset = 3;
	private static final int qualityOfDataOffset = 4;
	
	
	private boolean westAcquired, eastAcquired,
					northAcquired , southAcquired,
					widthAcquired, heightAcquired,
					offsetAcquired , dimensionsAcquired;
	private float xOffset,yOffset,width,height;
	MapArray mapArray;
	
	//header data
	private static final int leftMostIndex = headerOffset + 1;
	private static final int rightMostIndex = leftMostIndex + 1;
	private static final int bottomIndex = rightMostIndex + 1;
	private static final int topIndex = bottomIndex + 1;
	
	private static final int MapArrayOffset =topIndex+1 ;
	public GlobalMap(MapArray rc){
		widthAcquired=false;
		heightAcquired=false;
		offsetAcquired=false;
		westAcquired=false;
		eastAcquired = false;
		northAcquired = false;
		southAcquired = false;
		dimensionsAcquired = false;
		this.mapArray = rc;
		
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
				
				left = mapArray.readBroadcast(leftMostIndex);
				right = mapArray.readBroadcast(rightMostIndex);
				
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
					

			}
			
			//Check to see if the top and bottom have been found
			if(!heightAcquired){
				top = mapArray.readBroadcast(topIndex);
				bottom = mapArray.readBroadcast(bottomIndex);
				
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
					

			}
			
			//Check to see if all the map's dimensions have been discovered
			if(heightAcquired && widthAcquired ){
				dimensionsAcquired = true;
				offsetAcquired = true;
				xDivisions = (int) (width/gridSize);
				yDivisions = (int) (height / gridSize);
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

			mapArray.broadcast(leftMostIndex, left);

		}
		
	}
	
	/**
	 * set right most point of map
	 * @param right
	 */
	public void setEastBound(float right){
		if(! eastAcquired){
			mapArray.broadcast(rightMostIndex, right);
		}
	}
	
	/**
	 * set top of map
	 * @param top
	 */
	public void setNorthBound(float top){
		if( ! northAcquired ){
	
			mapArray.broadcast(topIndex, top);

		}
	}
	
	/**
	 * set bottom of map.
	 * @param bottom
	 */
	public void setSouthBound(float bottom){
		if(! southAcquired){

			mapArray.broadcast(bottomIndex, bottom);

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
	
	public float getWidth(){
		if(widthAcquired){
			return width;
		} else {
			return -1;
		}
	}
	
	public float getHeight(){
		if(heightAcquired){
			return height;
		} else {
			return -1;
		}
	}
	
	
	public float getXOffset(){
		return xOffset;
	}
	
	public float getYOffset(){
		return yOffset;
	}
	
	public int getLastChannel(){
		return getChannelOffsetFromLocation( 
				new MapLocation (xOffset+100 , yOffset+100)) + qualityOfDataOffset;
	}
	
	public void setQualityOfData(MapLocation location , int qualityOfDataValue){
		int channel = getChannelOffsetFromLocation(location) + qualityOfDataOffset;
		mapArray.broadcast(channel, qualityOfDataValue);
	}
	
	public float getQualityOfData(MapLocation location){
		int channel = getChannelOffsetFromLocation(location) + qualityOfDataOffset;
		return mapArray.readBroadcast(channel);
	}
	
	public void setTimeStamp(MapLocation location , int timeStampValue){
		int channel = getChannelOffsetFromLocation(location) + timeStampOffset;
		mapArray.broadcast(channel, timeStampValue);
	}
	
	public float getTimeStamp(MapLocation location){
		int channel = getChannelOffsetFromLocation(location) + timeStampOffset;
		return mapArray.readBroadcast(channel);
	}
	
	
	public void setPheromone(MapLocation location , int pheromoneValue){
		int channel = getChannelOffsetFromLocation(location) + pheromoneOffset;
		mapArray.broadcast(channel, pheromoneValue);
	}
	
	public float getPheromone(MapLocation location){
		int channel = getChannelOffsetFromLocation(location) + pheromoneOffset;
		return mapArray.readBroadcast(channel);
	}
	
	public void setDanger(MapLocation location , int dangerValue){
		int channel = getChannelOffsetFromLocation(location) + dangerOffset;
		mapArray.broadcast(channel, dangerValue);
	}
	
	public float getDanger(MapLocation location){
		int channel = getChannelOffsetFromLocation(location) + dangerOffset;
		return mapArray.readBroadcast(channel);
	}
	
	public void setForestation(MapLocation location , int forestationValue){
		int channel = getChannelOffsetFromLocation(location) + forestationDataOffset;
		mapArray.broadcast(channel, forestationValue);
	}
	
	public float getForestionation(MapLocation location){
		int channel = getChannelOffsetFromLocation(location) + forestationDataOffset;
		return mapArray.readBroadcast(channel);
	}
	
	public int getChannelOffsetFromLocation (MapLocation location){
		if(offsetAcquired){
			MapIndex index = new MapIndex(location);
			
			int arrayIndex = getArrayIndexFromMapIndex(index);
			
			int channel = getChannelNumberFromArrayIndex( arrayIndex );
			
			return channel;
		} else {
			return -1;
		}
	}
	
	/**
	 * 
	 * @param location
	 * 			starting location
	 * @param x
	 * 			number of horizontal grid movements
	 * @param y
	 * 			number of vertical grid movements
	 * @return
	 */
	public MapLocation translateByGridLocations( MapLocation location ,int x , int y){
		return location.translate(x*gridSize, y*gridSize);
	}
	
	/**
	 * gets the rounded location that is in the center of the grid location that
	 * corresponds to the provided location and puts a dot there.
	 * @param rc
	 * @param location
	 */
	public void markMapLocation(RobotController rc , MapLocation location){
//		try{
		MapLocation transLocation = getMapPoint(location);
			rc.setIndicatorDot(transLocation, 255, 0, 255);
//		} catch (GameActionException e){
//			
//		}
	}
	
	/**
	 * Returns the center of the gridpoint that the location resides in.
	 * Important for figuring out where data is coming from.
	 * @param location
	 * @return The location as seen by the underlying map structure
	 */
	public MapLocation getMapPoint(MapLocation location){
		MapIndex indexLocation = new MapIndex(location);
		
		return new MapLocation (
				(indexLocation.x * gridSize)+xOffset
				,
				(indexLocation.y * gridSize)+yOffset
				);
	}
	
	public int getArrayIndexFromMapIndex(int x , int y){
		return this.getArrayIndexFromMapIndex(new MapIndex(x,y));
	}
	
	public int getArrayIndexFromMapIndex(MapIndex mapIndex){
		int arrayIndex = 0;
		
		arrayIndex = mapIndex.x + (mapIndex.y*xDivisions);
		
		return arrayIndex;
	}

	public int getChannelNumberFromArrayIndex(int arrayIndex){
		
		return (arrayIndex * dataPackageSize) + MapArrayOffset;
		
	}
	
	public int getXDivisions(){
		return xDivisions;
	}
	
	public int getYDivisions(){
		return yDivisions;
	}
	
	public int getArrayIndexFromChannelNumber(int channelNumber){
		
		return (channelNumber - MapArrayOffset)/dataPackageSize;
	}
	
	public float getGridSize(){
		return gridSize;
	}

	public int getXIndex(float xPosition){
		float adjustedPostion = xPosition - xOffset;
		int index = (int)(adjustedPostion / gridSize);
		return index;
	}
	
	public int getYIndex(float yPosition){
		float adjustedPostion = yPosition - yOffset;
		int index = (int)(adjustedPostion / gridSize);
		return index;
	}
	
	public int getDataOffset(){
		return MapArrayOffset;
	}
	
	private MapLocation getLocationFromMapIndex(MapIndex index){
		
		float x = (index.x * xDivisions) + xOffset;
		
		float y = (index.y * yDivisions) + yOffset;
		
		return new MapLocation(x,y);
		
	}
	
	class MapIndex{
		
		public MapIndex(){
			x=y=0;
		}
		
		public MapIndex(int x , int y){
			this.x = x;
			this.y = y;
		}
		
		public MapIndex(MapLocation location){
			this( getXIndex(location.x + gridSize/2) , getYIndex(location.y + gridSize/2) );
		}
		
		public MapIndex(MapIndex rhs){
			this.x = rhs.x;
			this.y= rhs.y;
		}
		
		
		
		
		public int x , y;
	}
}
