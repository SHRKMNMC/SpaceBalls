package model;

import java.awt.*;
import java.io.Serializable;

/** DTO inmutable para enviar datos seguros a la vista y por red. */
public final class BallDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final double x, y;
    private final double velX, velY;
    private final int radius;
    private final Color color;

    public BallDTO(double x, double y, int radius, double velX, double velY, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.velX = velX;
        this.velY = velY;
        this.color = color;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getVelX() { return velX; }
    public double getVelY() { return velY; }
    public int getRadius() { return radius; }
    public Color getColor() { return color; }
}