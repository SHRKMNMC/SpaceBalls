package controller;

import master.MasterController;
import model.*;
import view.View;
import world.Decoration;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final Model model;
    private final View view;

    private MasterController masterController;

    // Mundo visual
    private BufferedImage worldBackground;
    private List<Decoration> worldDecorations = new ArrayList<>();

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public void setMasterController(MasterController masterController) {
        this.masterController = masterController;
    }

    public void init() {
        view.setController(this);
        view.initUI();
        model.startGameLoop(view::repaintViewer);
    }

    // ============================================================
    // WORLD GENERATOR → VIEWER
    // ============================================================

    public void setWorldBackground(BufferedImage bg) {
        this.worldBackground = bg;
        view.setWorldBackground(bg);
    }

    public void setWorldDecorations(List<Decoration> decorations) {
        this.worldDecorations = decorations;
        view.setWorldDecorations(decorations);
    }

    // ============================================================
    // BALL MANAGEMENT
    // ============================================================

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

    public void onCreateControlableBall(int radius, int speed) {
        model.createControlableBall(radius, speed);
    }

    public void onReceiveBall(BallDTO dto) {
        model.createBallFromDTO(dto);
    }

    // ============================================================
    // VIEW ACCESSORS
    // ============================================================

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

    public void setControlDirection(int dx, int dy) {
        model.setControlDirection(dx, dy);
    }

    public void updateBounds(int width, int height) {
        model.setBounds(width, height);
    }
}
