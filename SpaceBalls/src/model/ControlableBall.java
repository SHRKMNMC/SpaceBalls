package model;

import java.awt.*;

/**
 * Pelota controlada por el jugador.
 * Usa aceleración, velocidad máxima y fricción para un movimiento suave.
 */
public class ControlableBall extends Ball {

    private double acceleration = 600;
    private double maxVelocity = 300;
    private double friction = 500;

    public ControlableBall(double x, double y, int radius, int maxVelocity, Color color) {
        super(x, y, radius, 0, 0, color);
        this.maxVelocity = maxVelocity;
    }

    /**
     * Movimiento suave basado en aceleración y fricción.
     */
    public void move(int dirX, int dirY, double deltaTime) {

        // Acelerar según la dirección del input
        velX += dirX * acceleration * deltaTime;
        velY += dirY * acceleration * deltaTime;

        // Limitar velocidad máxima
        double speed = Math.sqrt(velX * velX + velY * velY);
        if (speed > maxVelocity) {
            velX = (velX / speed) * maxVelocity;
            velY = (velY / speed) * maxVelocity;
        }

        // Aplicar fricción si no hay input
        if (dirX == 0) velX = applyFriction(velX, deltaTime);
        if (dirY == 0) velY = applyFriction(velY, deltaTime);

        // Actualizar posición
        x += velX * deltaTime;
        y += velY * deltaTime;
    }

    private double applyFriction(double velocity, double deltaTime) {
        if (Math.abs(velocity) < 1) return 0;

        double sign = Math.signum(velocity);
        velocity -= sign * friction * deltaTime;

        // Si cambia de signo, detener
        if (sign != Math.signum(velocity)) return 0;

        return velocity;
    }
}
