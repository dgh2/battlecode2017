package josiah_boid_garden.boid;

import battlecode.common.BulletInfo;
import battlecode.common.RobotInfo;
import josiah_boid_garden.UnitVector;

public class BulletDodger extends BulletResponse{


	
	static final int antiBulletPower = 400;
	static final int halfPower;
	static final int quarterPower;
	
	static{
		halfPower = antiBulletPower/2;
		quarterPower =  halfPower/2;
	}
	
	public BulletDodger(Boid base) {
		super(base);
		
	}
	public BulletDodger(Boid base , int calculationPower) {
		super(base, calculationPower);
		
	}
	
	/**
	 * respond to where a bullet will be now and in the next 2 turns
	 */
	protected void respond ( Boid controller , BulletInfo bullet){

		singleBulletResponse(controller ,bullet,antiBulletPower);
		int turnsTillImpactApprox = (int)( bullet.getLocation().distanceTo(controller.rc.getLocation()) / bullet.speed );
		BulletInfo bulletInXTurns = getBulletInXTurns(bullet,turnsTillImpactApprox*2);
		singleBulletResponse(controller ,bulletInXTurns,antiBulletPower);
	
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

	private BulletInfo getBulletInXTurns(BulletInfo bullet,int turns){
		return new BulletInfo( 0 , bullet.getLocation().add(bullet.getDir(),bullet.getSpeed()*turns), bullet.getDir(), bullet.getSpeed(), bullet.getDamage());
	}
		
	
	
	
}
