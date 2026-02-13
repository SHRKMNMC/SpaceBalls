package controller;

import master.MasterController;
import model.*;
import view.View;

import java.util.List;

public class Controller {

    private final Model model;
    private final View view;

    // 🔥 NUEVO: referencia al MasterController
    private MasterController masterController;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    // 🔥 NUEVO: setter para inyectar el MasterController
    public void setMasterController(MasterController masterController) {
        this.masterController = masterController;
    }

    public void init() {
        view.initUI();
        model.startGameLoop(view::repaintViewer);
    }

    public void onCreateBall(int size, int speed) {
        model.createRandomBall(size, speed);

        // 🔥 Enviar la última pelota creada al otro jugador
        if (masterController != null) {
            List<BallDTO> balls = model.getBallsSnapshot();
            if (!balls.isEmpty()) {
                BallDTO last = balls.get(balls.size() - 1);
                masterController.sendBall(last);
            }
        }
    }

    public void onCreateControlableBall(int size, int speed) {
        model.createControlableBall(size, speed);

        // 🔥 Enviar también la pelota controlable al otro jugador
        if (masterController != null) {
            List<BallDTO> balls = model.getBallsSnapshot();
            if (!balls.isEmpty()) {
                BallDTO last = balls.get(balls.size() - 1);
                masterController.sendBall(last);
            }
        }
    }

    /** 🔥 Recibir pelota desde red */
    public void onReceiveBall(BallDTO dto) {
        model.createBallFromDTO(dto);
    }

    public List<BallDTO> getBallsSnapshot() { return model.getBallsSnapshot(); }
    public List<CollissionEvent> getCollisionEvents() { return model.getEventDetector().getEvents(); }

    public CollissionEvent getLastCollisionEvent() {
        List<CollissionEvent> events = model.getEventDetector().getEvents();
        return events.isEmpty() ? null : events.get(events.size() - 1);
    }

    public int getBallCount() { return model.getBallCount(); }
    public double getFPS() { return model.getCurrentFPS(); }

    public void setControlDirection(int dx, int dy) { model.setControlDirection(dx, dy); }

    public void updateBounds(int width, int height) { model.setBounds(width, height); }
}
