package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Registra eventos de colisión para animaciones.
 */
public class EventDetector {

    private final List<CollissionEvent> events = new ArrayList<>();

    public void registerCollision(Point p) {
        events.add(new CollissionEvent(p, 15));
    }

    public List<CollissionEvent> getEvents() {
        return events;
    }

    public void update() {
        events.removeIf(e -> --e.framesRemaining <= 0);
    }
}
