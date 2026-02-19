package model;

import java.awt.*;
import java.util.Random;

/**
 * Pelota básica con posición, velocidad y color.
 * La física real la gestiona SimpleFisics.
 */
public class Ball implements Runnable {

    protected double x, y;       // posición (centro)
    protected double velX, velY; // velocidad
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

    /**
     * Crear una pelota aleatoria dentro de los límites.
     */
    public static Ball randomBall(Rectangle bounds, int radius, int speed) {
        Random random = new Random();

        double x = bounds.x + radius + random.nextInt(Math.max(1, bounds.width - 2 * radius));
        double y = bounds.y + radius + random.nextInt(Math.max(1, bounds.height - 2 * radius));

        double angle = random.nextDouble() * Math.PI * 2;
        double velX = Math.cos(angle) * speed;
        double velY = Math.sin(angle) * speed;

        Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        return new Ball(x, y, radius, velX, velY, color);
    }

    /** Actualizar posición según velocidad */
    public void updatePosition(double deltaSeconds) {
        x += velX * deltaSeconds;
        y += velY * deltaSeconds;
    }

    /** Convertir a DTO para red o vista */
    public BallDTO toDTO() {
        return new BallDTO(x, y, radius, velX, velY, color);
    }

    @Override
    public void run() {
        while (running) {
            try { Thread.sleep(10); }
            catch (InterruptedException e) { running = false; }
        }
    }

    public void stop() { running = false; }

    // Getters y setters
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
