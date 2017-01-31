package josiah_boid_garden.boid;

import josiah_boid_garden.UnitVector;
import josiah_boid_garden.Vector;

public class ALittleRandomMovement {
	
	public void run(Boid source){
		
		UnitVector dir = new UnitVector();
		
		dir.add(new Vector( (float)(Math.random() * 100 -50) , (float)(Math.random() *100-50) ) );
		source.addForce(new UnitVector(), 1);
		
	}

}
