package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema que registra eventos de colisión para que la vista
 * pueda mostrar animaciones (explosiones).
 */
public class EventDetector {

    /** Lista de eventos activos */
    private final List<CollissionEvent> collisionEvents = new ArrayList<>();

    /** Registrar un nuevo evento de colisión */
    public void registerCollision(Point impactPoint) {
        collisionEvents.add(new CollissionEvent(impactPoint, 15));
    }

    /** Obtener todos los eventos activos */
    public List<CollissionEvent> getEvents() {
        return collisionEvents;
    }

    /** Reducir duración de cada evento y eliminar los expirados */
    public void update() {
        collisionEvents.removeIf(event -> --event.framesLeft <= 0);
    }
}
