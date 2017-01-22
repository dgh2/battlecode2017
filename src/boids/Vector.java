package boids;

import battlecode.common.Direction;

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
}
