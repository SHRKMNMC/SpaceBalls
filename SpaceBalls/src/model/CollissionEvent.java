package model;

import java.awt.*;

/**
 * Evento que representa una colisión detectada.
 * Se usa para mostrar animaciones (explosiones) en la vista.
 */
public class CollissionEvent {

    /** Punto exacto donde ocurrió el impacto */
    public final Point impactPoint;

    /** Cuántos frames le quedan antes de desaparecer */
    public int framesLeft;

    public CollissionEvent(Point impactPoint, int framesLeft) {
        this.impactPoint = impactPoint;
        this.framesLeft = framesLeft;
    }
}
