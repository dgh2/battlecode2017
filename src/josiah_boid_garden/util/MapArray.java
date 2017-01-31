package josiah_boid_garden.util;

public interface MapArray {
	
	public float readBroadcast(int channel);
	
	public void broadcast(int channel , float value); 
	
}
