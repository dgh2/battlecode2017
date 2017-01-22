package josiah_boid_garden;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class Vector {
    public float dx;
    public float dy;

    public Vector() {
        this(0, 0);
    }
    
    public Vector(Vector rhs) {
        this(rhs.dx, rhs.dy);
    }

    public Vector(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Vector(Direction direction, float distance) {
        this(direction.getDeltaX(distance), direction.getDeltaY(distance));
    }
    
    public Vector(MapLocation start , MapLocation finish){
    	dx = finish.x - start.x;
    	dy = finish.y - start.y;
    }

    public Vector add(Vector vector) {
        dx += vector.dx;
        dy += vector.dy;
        return this;
    }
    
    public Vector add(int dx, int dy) {
        this.dx += dx;
        this.dy += dy;
        return this;
    }

    public Vector scale(float scale) {
    	dx *= scale;
    	dy *= scale;
        return this;
    }

    public Direction getDirection() {
        return new Direction(dx, dy);
    }

    public float getMagnitude() {
    	
    	if(dx == dy && dx == 0)
    		return 0;
    	else
    		return (float) Math.sqrt( (dx*dx) + (dy*dy) );
    }
    
    public Vector setMagnitude(float magnitude){
    	if(magnitude == 0){
    		dx = 0;
    		dy = 0;
    	} else if(dx == 0 && dy == 0){
    		dx = magnitude;
    	} else {
    		
	    	float scalingConstant =  magnitude /  getMagnitude() ;
	    	dx *= scalingConstant;
	    	dy *= scalingConstant;
	    	
    	}
    	
    	return this;
    }
}
