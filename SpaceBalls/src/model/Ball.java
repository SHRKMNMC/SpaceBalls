package model;

import java.awt.*;
import java.util.Random;

/**
 * Pelota básica con posición, velocidad y color.
 * La física real la gestiona SimpleFisics.
 */
public class Ball implements Runnable {

    protected double x, y;
    protected double velX, velY;
    protected int radius;
    protected Color color;

    private volatile boolean running = true;

    public Ball(double x, double y, int radius, double velX, double velY, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.velX = velX;
        this.velY = velY;
        this.color = color;
    }

    /** Crea una pelota aleatoria dentro de los límites. */
    public static Ball randomBall(Rectangle bounds, int radius, int speed) {
        Random r = new Random();
        double x = bounds.x + radius + r.nextInt(Math.max(1, bounds.width - 2 * radius));
        double y = bounds.y + radius + r.nextInt(Math.max(1, bounds.height - 2 * radius));

        double angle = r.nextDouble() * Math.PI * 2;
        double vx = Math.cos(angle) * speed;
        double vy = Math.sin(angle) * speed;

        Color color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        return new Ball(x, y, radius, vx, vy, color);
    }

    /** Actualiza la posición según la velocidad. */
    public void updatePosition(double deltaSeconds) {
        x += velX * deltaSeconds;
        y += velY * deltaSeconds;
    }

    /** Convierte la pelota a un DTO para la vista. */
    public BallDTO toDTO() {
        return new BallDTO(x, y, radius, color);
    }

    @Override
    public void run() {
        while (running) {
            try { Thread.sleep(10); }
            catch (InterruptedException e) { running = false; }
        }
    }

    public void stop() { running = false; }

    // Getters y setters usados por la física
    public double getX() { return x; }
    public double getY() { return y; }
    public int getRadius() { return radius; }
    public double getVelX() { return velX; }
    public double getVelY() { return velY; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setVelX(double velX) { this.velX = velX; }
    public void setVelY(double velY) { this.velY = velY; }
}
