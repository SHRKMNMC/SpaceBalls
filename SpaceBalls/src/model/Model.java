package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo principal del juego.
 * Ahora NO mueve pelotas: cada Ball tiene su propio hilo.
 */
public class Model {

    private final List<Ball> balls = Collections.synchronizedList(new ArrayList<>());
    private ControlableBall playerBall;

    private final EventDetector eventDetector = new EventDetector();
    private final Fisics physicsEngine = new SimpleFisics(this);

    private volatile boolean running = false;
    private double currentFPS = 0.0;

    private Rectangle worldBounds = new Rectangle(0, 0, 1, 1);

    public EventDetector getEventDetector() { return eventDetector; }

    public void setBounds(int width, int height) {
        this.worldBounds = new Rectangle(0, 0, width, height);
    }

    /** Game loop solo para colisiones y FPS */
    public void startGameLoop(Runnable onFrameRendered) {
        if (running) return;
        running = true;

        Thread loop = new Thread(() -> {
            long last = System.nanoTime();

            while (running) {
                long now = System.nanoTime();
                double dt = (now - last) / 1_000_000_000.0;
                last = now;

                // Solo colisiones
                synchronized (balls) {
                    physicsEngine.update(balls, worldBounds, dt);
                }

                eventDetector.update();
                onFrameRendered.run();

                currentFPS = 1.0 / Math.max(dt, 1e-6);

                try { Thread.sleep(1); }
                catch (InterruptedException ignored) {}
            }
        });

        loop.setDaemon(true);
        loop.start();
    }

    /** Crear pelota normal */
    public void createRandomBall(int radius, int speed) {
        balls.add(Ball.randomBall(worldBounds, radius, speed));
    }

    /** Crear pelota controlable */
    public void createControlableBall(int radius, int speed) {
        playerBall = new ControlableBall(worldBounds.getCenterX(), worldBounds.getCenterY(), radius, speed, Color.WHITE);
        balls.add(playerBall);
    }

    /** Actualizar dirección del jugador */
    public void setControlDirection(int dx, int dy) {
        if (playerBall != null) playerBall.setDirection(dx, dy);
    }

    /** Crear pelota recibida por red */
    public void createBallFromDTO(BallDTO dto) {
        Ball ball = new Ball(dto.getX(), dto.getY(), dto.getRadius(), dto.getVelX(), dto.getVelY(), dto.getColor());
        balls.add(ball);
    }

    /** Snapshot seguro */
    public List<BallDTO> getBallsSnapshot() {
        List<BallDTO> snapshot = new ArrayList<>();
        synchronized (balls) {
            for (Ball ball : balls) snapshot.add(ball.toDTO());
        }
        return snapshot;
    }

    public int getBallCount() { return balls.size(); }
    public double getCurrentFPS() { return currentFPS; }
}
