package model;

import java.awt.*;

/** DTO inmutable para enviar datos seguros a la vista. */
public final class BallDTO {

    private final double x, y;
    private final int radius;
    private final Color color;

    public BallDTO(double x, double y, int radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getRadius() { return radius; }
    public Color getColor() { return color; }
}
