package model;

import java.awt.*;
import java.util.Random;

/**
 * Pelota básica con posición, velocidad y color.
 * Cada pelota tiene su propio hilo y actualiza su posición.
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

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    /** Crear pelota aleatoria */
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

    /** Hilo propio de la pelota */
    @Override
    public void run() {
        long last = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            double dt = (now - last) / 1_000_000_000.0;
            last = now;

            updatePosition(dt);

            try { Thread.sleep(5); }
            catch (InterruptedException ignored) {}
        }
    }

    /** Movimiento básico */
    public void updatePosition(double dt) {
        x += velX * dt;
        y += velY * dt;
    }

    public BallDTO toDTO() {
        return new BallDTO(x, y, radius, velX, velY, color);
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
