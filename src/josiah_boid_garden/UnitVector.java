package josiah_boid_garden;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class UnitVector extends Vector{


    public UnitVector() {
        super(1, 0);
    }

    public UnitVector(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
        super.setMagnitude(1);
        
    }
    
    public UnitVector(Vector source) {
        super(source);
        super.setMagnitude(1);
    	System.out.println("(Copy constructor)Hi, I'm a unit vector and I have dx of "+dx+" and a dy of "+dy);
        
    }

    public UnitVector(Direction direction, float distance) {
        this(direction.getDeltaX(distance), direction.getDeltaY(distance));
        super.setMagnitude(1);
    	System.out.println("(Direction Constructor)Hi, I'm a unit vector and I have dx of "+dx+" and a dy of "+dy);
    }
    
    public UnitVector(MapLocation start , MapLocation finish){
    	dx = finish.x - start.x;
    	dy = finish.y - start.y;
    	super.setMagnitude(1);
    	System.out.println("(Map Constructor)Hi, I'm a unit vector and I have dx of "+dx+" and a dy of "+dy);
    }

    public Vector add(Vector vector) {
        dx += vector.dx;
        dy += vector.dy;
        super.setMagnitude(1);
        return this;
    }
    
    public Vector add(int dx, int dy) {
        this.dx += dx;
        this.dy += dy;
        super.setMagnitude(1);
        return this;
    }
    

    @Deprecated
    @Override
    /**
     * This function no longer scales but sets the magnitude to 1.
     */
    public Vector scale(float scale) {
    	super.setMagnitude(1);
        return this;
    }
    
    /**
     * this function no longer sets the magnitude to the desired, but
     * instead sets the magnitude to 1.
     */
    @Deprecated
    @Override
    public Vector setMagnitude(float magnitude){
    	super.setMagnitude(1);
    	return this;
    }



    

}
