package josiah_boid_garden.util;

/**
 * responsible for reading and writing to the proper index of the array.
 * Takes care of the interface between the team array and the GlobalMap index becuase,
 * well, lets be honest now, it's impossible to unit test Battlecode objects.
 * @author user
 *
 */
public class MapArrayLocal implements MapArray {

	float[] mapArray = new float[10000];
	
	public MapArrayLocal(){
		for(int i = 0 ; i < mapArray.length ; i++){
			mapArray[i]=0;
		}
	}

	public void broadcast(int channel , float value){
		mapArray[channel] = value;
	}
	
	public float readBroadcast(int index){
		return mapArray[index];
	}

	
	

}
