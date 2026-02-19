package controller;

import master.MasterController;
import model.*;
import view.View;

import java.util.List;

/**
 * Controlador principal del juego.
 * Coordina:
 * - Vista
 * - Modelo
 * - Comunicación en red (MasterController)
 */
public class Controller {

    private final Model model;
    private final View view;

    /** Controlador maestro para red (cliente/servidor) */
    private MasterController masterController;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    /** Inyecta el MasterController después de crearlo */
    public void setMasterController(MasterController masterController) {
        this.masterController = masterController;
    }

    /**
     * Inicializa la UI y arranca el game loop del modelo.
     */
    public void init() {
        view.initUI();
        model.startGameLoop(view::repaintViewer);
    }

    /**
     * Crear pelota normal y enviarla por red si procede.
     */
    public void onCreateBall(int radius, int speed) {
        model.createRandomBall(radius, speed);

        if (masterController != null) {
            List<BallDTO> snapshot = model.getBallsSnapshot();
            if (!snapshot.isEmpty()) {
                BallDTO lastBall = snapshot.get(snapshot.size() - 1);
                masterController.sendBall(lastBall);
            }
        }
    }

    /**
     * Crear pelota controlable y enviarla por red.
     */
    public void onCreateControlableBall(int radius, int speed) {
        model.createControlableBall(radius, speed);

        if (masterController != null) {
            List<BallDTO> snapshot = model.getBallsSnapshot();
            if (!snapshot.isEmpty()) {
                BallDTO lastBall = snapshot.get(snapshot.size() - 1);
                masterController.sendBall(lastBall);
            }
        }
    }

    /**
     * Recibir pelota desde red y crearla en el modelo.
     */
    public void onReceiveBall(BallDTO dto) {
        model.createBallFromDTO(dto);
    }

    // Métodos de acceso para la vista

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

    /** Actualiza dirección de movimiento del jugador */
    public void setControlDirection(int dx, int dy) {
        model.setControlDirection(dx, dy);
    }

    /** Actualiza límites del área de juego */
    public void updateBounds(int width, int height) {
        model.setBounds(width, height);
    }
}
