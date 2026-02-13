package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo principal del juego.
 * Gestiona pelotas, física, eventos y el game loop.
 */
public class Model {

    private final List<Ball> balls = Collections.synchronizedList(new ArrayList<>());
    private ControlableBall controlableBall;

    private final EventDetector eventDetector = new EventDetector();
    private final Fisics fisics = new SimpleFisics(this);

    private volatile boolean running = false;
    private double currentFPS = 0.0;

    private Rectangle bounds = new Rectangle(0, 0, 1, 1);

    private int controlDX = 0, controlDY = 0;

    public EventDetector getEventDetector() { return eventDetector; }

    public void setBounds(int width, int height) {
        this.bounds = new Rectangle(0, 0, width, height);
    }

    public void startGameLoop(Runnable onFrameRendered) {
        if (running) return;
        running = true;

        Thread loop = new Thread(() -> {
            long last = System.nanoTime();

            while (running) {
                long now = System.nanoTime();
                double delta = (now - last) / 1_000_000_000.0;
                last = now;

                update(delta);
                onFrameRendered.run();

                currentFPS = 1.0 / Math.max(delta, 1e-6);

                try { Thread.sleep(1); }
                catch (InterruptedException e) { running = false; }
            }
        });

        loop.setDaemon(true);
        loop.start();
    }

    private void update(double deltaSeconds) {
        synchronized (balls) {
            fisics.update(balls, bounds, deltaSeconds);
        }

        if (controlableBall != null)
            controlableBall.move(controlDX, controlDY, deltaSeconds);

        eventDetector.update();
    }

    public void createRandomBall(int size, int speed) {
        balls.add(Ball.randomBall(bounds, size, speed));
    }

    public void createControlableBall(int size, int speed) {
        controlableBall = new ControlableBall(bounds.getCenterX(), bounds.getCenterY(), size, speed, Color.WHITE);
        balls.add(controlableBall);
    }

    public void setControlDirection(int dx, int dy) {
        controlDX = dx;
        controlDY = dy;
    }

    /** 🔥 Crear pelota idéntica desde red */
    public void createBallFromDTO(BallDTO dto) {
        Ball ball = new Ball(
                dto.getX(),
                dto.getY(),
                dto.getRadius(),
                dto.getVelX(),
                dto.getVelY(),
                dto.getColor()
        );

        synchronized (balls) {
            balls.add(ball);
        }
    }

    public List<BallDTO> getBallsSnapshot() {
        List<BallDTO> snapshot = new ArrayList<>();
        synchronized (balls) {
            for (Ball b : balls) snapshot.add(b.toDTO());
        }
        return snapshot;
    }

    public int getBallCount() { return balls.size(); }
    public double getCurrentFPS() { return currentFPS; }
}