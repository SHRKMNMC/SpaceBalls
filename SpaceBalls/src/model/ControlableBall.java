package model;

import java.awt.*;

/**
 * Pelota controlada por el jugador.
 * Usa aceleración y fricción, pero se mueve en su propio hilo.
 */
public class ControlableBall extends Ball {

    private double acceleration = 600;
    private double maxVelocity = 300;
    private double friction = 500;

    private volatile int dirX = 0;
    private volatile int dirY = 0;

    public ControlableBall(double x, double y, int radius, int maxVelocity, Color color) {
        super(x, y, radius, 0, 0, color);
        this.maxVelocity = maxVelocity;
    }

    /** Actualiza dirección desde Controller */
    public void setDirection(int dx, int dy) {
        this.dirX = dx;
        this.dirY = dy;
    }

    @Override
    public void updatePosition(double dt) {

        // Acelerar según input
        velX += dirX * acceleration * dt;
        velY += dirY * acceleration * dt;

        // Limitar velocidad
        double speed = Math.sqrt(velX * velX + velY * velY);
        if (speed > maxVelocity) {
            velX = (velX / speed) * maxVelocity;
            velY = (velY / speed) * maxVelocity;
        }

        // Fricción
        if (dirX == 0) velX = applyFriction(velX, dt);
        if (dirY == 0) velY = applyFriction(velY, dt);

        // Movimiento real
        super.updatePosition(dt);
    }

    private double applyFriction(double v, double dt) {
        if (Math.abs(v) < 1) return 0;
        double sign = Math.signum(v);
        v -= sign * friction * dt;
        if (sign != Math.signum(v)) return 0;
        return v;
    }
}
