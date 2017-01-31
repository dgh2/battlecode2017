package josiah_boid_garden.monteCarlo;

import java.util.ArrayDeque;
import java.util.Queue;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.TreeInfo;
import josiah_boid_garden.UnitVector;
import josiah_boid_garden.boid.Boid;

public class JohnnyAppleSeed {
	
	RobotController controller;
	
	MonteCarlo seedingPlot = new MonteCarlo();
	
	private static int maxPlantingChecks = 32;
	
	private static float thresholdPlantingDistance = 1.2f;
	private static int minimumAttractivenessValue = 0;
	private static float plantSeedAttractionPower = 100;
	
	private Queue<McPoint> plantingQueue = new ArrayDeque<McPoint>(maxPlantingChecks);
	
	
	
	public JohnnyAppleSeed(RobotController controller){
		this.controller = controller;
		

	}
	
	
	public void addSeedingPlot(PointGenerator generator){
		generator.addPoints(seedingPlot);
	}
	
	public void plantSeeds(TreeInfo[] trees , Boid boid){
		
		seedingPlot.clearModifiers();
		seedingPlot.addModifier(new CanPlantMetric(controller));
		seedingPlot.addModifier(new TreeAntiCloggingMetric( trees ));
		
		addSeedingPlot( new CardinalPoints( controller) );
		
		McPoint plantingPlace = seedingPlot.appraise();
		
		
		
		//If there's a place returned...
		if( plantingPlace!=null ){
			//see if it's close enought that you want to plant it
			if( plantingPlace.location.distanceTo(controller.getLocation()) < thresholdPlantingDistance ){
				//see if it's valueable enough that you want to plant it
				if(plantingPlace.value >= minimumAttractivenessValue){
					try {
						System.out.println("Planting a tree with a attractiveness of : " + plantingPlace.value);
						controller.plantTree(new Direction(controller.getLocation(),plantingPlace.location));
					} catch (GameActionException e) {
						System.out.println("Johnny Appleseed tried planting a seed that he can't.");
	
					}	
				}
			//move toward the most valuable looking seeding location
			} else {
				boid.addForce(new UnitVector(controller.getLocation(),plantingPlace.location),
						(int)(plantSeedAttractionPower/ (controller.getLocation().distanceTo(plantingPlace.location) +1 ) ) );
			}

		}
		
		
		
		//remove old points
		for(int i = 0 ; i< plantingQueue.size() - maxPlantingChecks; i++){
			try{
			plantingQueue.remove();
			} catch (Exception e){
				//tried to remove from an empty queue
				System.out.println("Something is horribly wrong with Johnny Appleseed.");
			}
		}
		
		

	}
	
	/**
	 * Tree to plant a tree when you can 40% of the time.
	 * @return
	 */
	private boolean doIWantToPlantAnAppleSeed(){
		return controller.getTeamBullets() > GameConstants.BULLET_TREE_COST ;
	}
	
	
	

}
