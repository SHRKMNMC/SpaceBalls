package model;

import java.awt.*;

/** Evento visual de colisión con duración limitada. */
public class CollissionEvent {
    public final Point position;
    public int framesRemaining;

    public CollissionEvent(Point position, int frames) {
        this.position = position;
        this.framesRemaining = frames;
    }
}
