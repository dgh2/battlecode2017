package josiah_boid_garden.boid;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import josiah_boid_garden.UnitVector;
import josiah_boid_garden.Vector;

public class AvoidEdges {
	
	RobotController rc;
	Boid controller;
	Vector[] MapDirection;
	int edgeAvoidingness = 20;
	int scaledEdgeAvoidingness;
	float radius;
	public AvoidEdges(Boid boid){
		this.rc=boid.rc;
		this.controller = boid;
		radius = rc.getType().sensorRadius / 2;
		scaledEdgeAvoidingness = (int) (edgeAvoidingness/radius);
		MapDirection = new Vector[]{
				new Vector(1, 0),
				new Vector(-1, 0),
				new Vector( 0 , 1),
				new Vector(0 , -1)
		};
	}
	
	public void run(){
		MapLocation checkLocation;
		for(int i = 0 ; i < MapDirection.length ; i++){
			try {
				checkLocation = rc.getLocation().translate(MapDirection[i].dx,MapDirection[i].dy);
				for(int j = 0 ; !rc.onTheMap(checkLocation) && j<10 ; j++){
					controller.addForce(new UnitVector(-MapDirection[i].dx,-MapDirection[i].dy), scaledEdgeAvoidingness);
					checkLocation = rc.getLocation().translate(MapDirection[i].dx*radius/j,MapDirection[i].dy*radius/j);
				}
				
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
