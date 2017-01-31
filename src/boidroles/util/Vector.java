package boidroles.util;

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

    public Vector(Direction direction) {
        this(direction, 1);
    }

    public Vector(Direction direction, float distance) {
        this(direction.getDeltaX(distance), direction.getDeltaY(distance));
    }

    public Vector add(Vector vector) {
        dx += vector.dx;
        dy += vector.dy;
        return this;
    }

    public Vector subtract(Vector vector) {
        dx -= vector.dx;
        dy -= vector.dy;
        return this;
    }

    public Vector opposite() {
        dx = -dx;
        dy = -dy;
        return this;
    }

    public Vector normalize(float maxRadius) {
        Direction direction = getDirection();
        float normalizedDistance = getDistance() / maxRadius;
        dx = direction.getDeltaX(normalizedDistance);
        dx = direction.getDeltaX(normalizedDistance);
        return this;
    }

    public Vector scale(float scale) {
        dx *= scale;
        dy *= scale;
        return this;
    }

    public Vector inverseScale(float scale) {
        dx /= scale;
        dy /= scale;
        return this;
    }

    public Direction getDirection() {
        return new Direction(dx, dy);
    }

    public float getDistance() {
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
