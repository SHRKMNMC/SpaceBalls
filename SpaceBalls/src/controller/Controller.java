package controller;

import model.*;
import view.View;

import java.util.List;

/**
 * Controlador principal del patrón MVC.
 * Recibe acciones de la vista y opera sobre el modelo.
 */
public class Controller {

    private final Model model;
    private final View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    /** Inicializa la UI y arranca el game loop. */
    public void init() {
        view.initUI();
        model.startGameLoop(view::repaintViewer);
    }

    // --- Acciones desde la vista ---

    public void onCreateBall(int size, int speed) {
        model.createRandomBall(size, speed);
    }

    public void onCreateControlableBall(int size, int speed) {
        model.createControlableBall(size, speed);
    }

    // --- Datos para la vista ---

    public List<BallDTO> getBallsSnapshot() {
        return model.getBallsSnapshot();
    }

    public List<CollissionEvent> getCollisionEvents() {
        return model.getEventDetector().getEvents();
    }

    public CollissionEvent getLastCollisionEvent() {
        List<CollissionEvent> events = model.getEventDetector().getEvents();
        return events.isEmpty() ? null : events.get(events.size() - 1);
    }

    public int getBallCount() {
        return model.getBallCount();
    }

    public double getFPS() {
        return model.getCurrentFPS();
    }

    /** Movimiento desde Viewer. */
    public void setControlDirection(int dx, int dy) {
        model.setControlDirection(dx, dy);
    }

    /** 🔥 NUEVO: actualizar límites del modelo */
    public void updateBounds(int width, int height) {
        model.setBounds(width, height);
    }
}
