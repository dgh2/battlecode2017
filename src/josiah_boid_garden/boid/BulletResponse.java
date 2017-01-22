package josiah_boid_garden.boid;

import battlecode.common.BulletInfo;
import battlecode.common.Clock;

public abstract class BulletResponse {

		
	Boid base;
	int calculationTime;
	public BulletResponse (Boid base){
		this(base , 2000);
	}
	
	public BulletResponse (Boid base , int calculationTime){
		this.base = base;
		this.calculationTime = calculationTime;
		
	}
	
	public void run(BulletInfo[] robots ){
		
		int timeout = Clock.getBytecodeNum() + calculationTime;
		
		for(int i = 0 ; i<robots.length ; i++){
			respond ( base , robots[i]) ;
			//limit calculation time
			if(Clock.getBytecodeNum() > timeout)
				break;
		}
		
	}
	
	protected abstract void respond ( Boid controller , BulletInfo robot);

	
}
