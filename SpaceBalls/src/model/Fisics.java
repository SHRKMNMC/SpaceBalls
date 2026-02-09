package model;

import java.awt.*;
import java.util.List;

/** Interfaz para sistemas de física intercambiables. */
public interface Fisics {
    void update(List<Ball> balls, Rectangle bounds, double deltaSeconds);
}
