package josiah_boid_garden.boid;

import battlecode.common.BulletInfo;

public abstract class BulletResponse {

		
	Boid base;
	
	BulletResponse (Boid base){
		this.base = base;
	}
	
	public void run(BulletInfo[] robots ){
		
		for(int i = 0 ; i<robots.length ; i++){
			
			respond ( base , robots[i]) ;
			
		}
		
	}
	
	protected abstract void respond ( Boid controller , BulletInfo robot);

	
}
