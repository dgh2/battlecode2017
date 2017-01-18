package boids;

import battlecode.common.Direction;
import battlecode.common.MapLocation;


public class Vector {
    public float dx;
    public float dy;

    public Vector() {
        this(0, 0);
    }
    
    public Vector(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
	Vector(MapLocation start , MapLocation end){
		dx = end.x -start.x;
		dy = end.y - start.y;
	}
	
    public Vector(Direction direction, float distance) {
        this(direction.getDeltaX(distance), direction.getDeltaY(distance));
    }

    public Vector add(Vector vector) {
        dx += vector.dx;
        dy += vector.dy;
        return this;
    }

    public Vector scale(float scale) {
        Direction direction = getDirection();
        float scaledDistance = getDistance() * scale;
        dx = direction.getDeltaX(scaledDistance);
        dy = direction.getDeltaY(scaledDistance);
        return this;
    }

    public Direction getDirection() {
        return new Direction(dx, dy);
    }

    public float getDistance() {
        return (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }
    

	

	/**
	 * gets the unit vector of this vector
	 * @param source
	 * @return
	 */
	public static Vector getUnit(Vector source){
		float length = (float)source.getMagnitude();
		return new Vector( (source.dx/length) , (source.dy/length) );
	}
	
	/**
	 * 
	 * @param dir
	 * @return
	 */
	public static Vector getUnit(Direction dir){	
		return new Vector ( (float)(Math.cos(dir.radians)) , (float)(Math.sin(dir.radians)) );
	}
	
	
	/**
	 * adds two vectors
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Vector add(Vector lhs ,Vector rhs){
		return new Vector( lhs.dx+rhs.dx,lhs.dy+rhs.dy);	
	}
	
	/**
	 * subtracts two vectors
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Vector subtract(Vector lhs , Vector rhs){
		return new Vector( lhs.dx -rhs.dx,lhs.dy-rhs.dy);
	}
	
	/**
	 * scales a vector to the given length
	 * @param base
	 * @param newLength
	 * @return
	 */
	public static Vector setMagnitude(Vector base , float newLength){
		float length = (float) (base.getMagnitude()/newLength);
		return new Vector( base.dx * length , base.dy * length  );
	}
	
	/**
	 * multiplies a vectors length by the scalar amount
	 * @param base
	 * @param length
	 * @return
	 */
	public static Vector multiply(Vector base , float length){
		return new Vector( base.dx * length , base.dy * length  );
	}
	
	
	/**
	 * gets the magnitude of the vector
	 * @return
	 */
	public double getMagnitude(){
        return getDistance();
    }
	
}
