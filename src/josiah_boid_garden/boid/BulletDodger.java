package josiah_boid_garden.boid;

import battlecode.common.BulletInfo;
import battlecode.common.RobotInfo;
import josiah_boid_garden.UnitVector;

public class BulletDodger extends BulletResponse{


	
	int antiBulletPower = 400;
	int halfPower;
	int quarterPower;
	
	public BulletDodger(Boid base) {
		super(base);
		halfPower = antiBulletPower/2;
		quarterPower =  halfPower/2;
	}
	
	
	/**
	 * respond to where a bullet will be now and in the next 2 turns
	 */
	protected void respond ( Boid controller , BulletInfo bullet){
		
		
		BulletInfo nextBullet = getWhereBulletWillBeNextTurn(bullet);	
		BulletInfo nextNextBullet = getWhereBulletWillBeNextTurn(nextBullet);
		
		singleBulletResponse(controller ,bullet,antiBulletPower);
		singleBulletResponse(controller ,nextBullet,halfPower);
		singleBulletResponse(controller ,nextNextBullet,quarterPower);
	}
	
	private void singleBulletResponse(Boid controller , BulletInfo bullet , int power ){
		if(bullet.getID() == controller.rc.getID()){
			return;
		}
		
		System.out.print("Being antisocial toward robot "+bullet.getID() + " at " + bullet.getLocation() );
		
		//We use a formula similar to the Gravitational Attraction between two bodies equation.
		float distance = controller.rc.getLocation().distanceSquaredTo( bullet.getLocation() );
		
		int multiplier = (int) (power * bullet.damage / distance);
		
		UnitVector dir = new UnitVector(bullet.getLocation() , controller.rc.getLocation());
		
		controller.addForce(dir, multiplier);
	}

	private BulletInfo getWhereBulletWillBeNextTurn(BulletInfo bullet){
		return new BulletInfo( 0 , bullet.getLocation().add(bullet.getDir(),bullet.getSpeed()), bullet.getDir(), bullet.getSpeed(), bullet.getDamage());
	}
		
	
	
	
}
