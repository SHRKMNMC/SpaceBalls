package model;

import java.awt.*;

/**
 * Pelota controlada por el usuario con movimiento suave:
 * - Aceleración
 * - Velocidad máxima
 * - Desaceleración natural
 * - Movimiento fluido
 */
public class ControlableBall extends Ball {

    private double accel = 600;       // aceleración px/s²
    private double maxSpeed = 300;    // velocidad máxima
    private double friction = 500;    // desaceleración automática

    public ControlableBall(double x, double y, int radius, int speed, Color color) {
        super(x, y, radius, 0, 0, color);
        this.maxSpeed = speed;
    }

    /**
     * Movimiento suave basado en aceleración.
     */
    public void move(int dx, int dy, double dt) {

        // Acelerar según dirección
        velX += dx * accel * dt;
        velY += dy * accel * dt;

        // Limitar velocidad máxima
        double speed = Math.sqrt(velX * velX + velY * velY);
        if (speed > maxSpeed) {
            velX = (velX / speed) * maxSpeed;
            velY = (velY / speed) * maxSpeed;
        }

        // Si no hay input → aplicar fricción
        if (dx == 0) {
            velX = applyFriction(velX, dt);
        }
        if (dy == 0) {
            velY = applyFriction(velY, dt);
        }

        // Actualizar posición
        x += velX * dt;
        y += velY * dt;
    }

    private double applyFriction(double v, double dt) {
        if (Math.abs(v) < 1) return 0;
        double sign = Math.signum(v);
        v -= sign * friction * dt;
        if (sign != Math.signum(v)) return 0;
        return v;
    }
}
